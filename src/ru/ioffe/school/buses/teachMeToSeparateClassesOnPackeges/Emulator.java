package teachMeToSeparateClassesOnPackeges;

import java.util.ArrayList;
import java.util.HashMap;

public class Emulator {

	//  input data
	Point[] stations;
	ArrayList<Transfer>[] transfers; 
	HashMap<Point, Integer> indexs;
	GeographyManager geographyManager;
	double speed;

	// output
	Route[] routes;

	@SuppressWarnings("unchecked")
	public Emulator(Point[] stations, GeographyManager geographyManager, double speed, Transfer... transfers) {
		this.stations = stations;
		this.transfers = new ArrayList[stations.length];
		this.geographyManager = geographyManager;
		this.speed = speed;
		this.indexs = new HashMap<>();
		for (int i = 0; i < stations.length; i++) {
			indexs.put(stations[i], i);
			this.transfers[i] = new ArrayList<>();
		}
		for (Transfer tr : transfers) {
			Integer ind = indexs.get(tr.from); 
			this.transfers[
			               indexs.get(tr.from)]
					.add(tr);
		}
	}

	public synchronized Report startEmulation(Nigth nigth, int threadsNumber) {
		if (threadsNumber < 1) 
			throw new IllegalArgumentException("Bad idea");
		threadsNumber = Math.min(threadsNumber, stations.length);
		Thread[] threads = new Thread[threadsNumber];
		int n = nigth.getPersons().length;
		routes = new Route[n];
		int d = (n - 1) / threadsNumber + 1;  // rounding up
		for (int i = 0; i < threadsNumber; i++) {
			threads[i] = new Thread(
					new Module(nigth.getPersons(), i * d, Math.min((i + 1) * d, n)));
			threads[i].start();
		}
		for (int i = 0; i < threadsNumber; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {}
		}
		return new Report(routes);
	}


	public class Module implements Runnable {
		Person[] persons;
		int begin;
		int end;

		public Module(Person[] persons, int begin, int end) {
			this.begin = begin;
			this.end = end;
			this.persons = persons;
		}

		@Override
		public void run() {
			for (int i = begin; i < end; i++)
				routes[i] = findRoute(persons[i]);
		}

		public Point getPoint(Person person, int index) {
			if (index == 0) 
				return person.from;
			index--;
			if (index == stations.length)
				return person.to;
			return stations[index];
		}

		public int getIndex(Person person, Point point) {
			if (point == person.from)
				return 0;
			if (point == person.to)
				return stations.length + 1;
			return 1 + indexs.get(point);
		}

		public ArrayList<Transfer> getRoads(int index) {
			if (index == 0 || index == stations.length + 1) 
				return null;
			return transfers[--index];
		}

		public Route findRoute(Person person) {
			int[] pred = new int[stations.length + 2];
			int[] time = new int[stations.length + 2];
			boolean[] checked = new boolean[stations.length + 2];
			for (int i = 0; i < time.length; i++) {
				time[i] = Integer.MAX_VALUE;
				pred[i] = -1;
			}
			time[0] = person.getTime();
			ArrayList<Transfer> roads;
			int indexTo;
			int nextTime;
			while(!checked[stations.length + 1]) {
				int minIndex = -1;
				for (int i = 0; i < time.length; i++) 
					if (!checked[i] && (minIndex == -1 || time[i] < time[minIndex]))
						minIndex = i;
				checked[minIndex] = true;
				roads = getRoads(minIndex);
				if (roads != null) {
					for (Transfer tr : roads) {
						indexTo = getIndex(person, tr.getTo());
						if (checked[indexTo])
							continue;
						nextTime = tr.getNextDeparture(time[minIndex]);
						if (nextTime == Integer.MAX_VALUE)
							continue;
						if (time[indexTo] > nextTime + tr.getContinuance()) {
							time[indexTo] = nextTime + tr.getContinuance();
							pred[indexTo] = minIndex;
						}
					}
				}
				// maybe here should be some magic because people usually don't walk for long distance 
				for (int i = 0; i < time.length; i++) {
					if (checked[i])
						continue;
					// next line should work slowly
					int dt = (int) (geographyManager.getDistance(getPoint(person, minIndex), getPoint(person, i)) / speed);
					if (time[i] > time[minIndex] + dt) {
						time[i] = time[minIndex] + dt;
						pred[i] = minIndex;
					}
				}
			}
			ArrayList<Segment> way = new ArrayList<>();
			int current = time.length - 1;
			while (pred[current] != -1) {
				way.add(new Segment(getPoint(person, pred[current]),
						getPoint(person, current), time[pred[current]], time[current]));
				current = pred[current];
			}
			Segment[] rigthWay = new Segment[way.size()];
			for (int i = 0; i < rigthWay.length; i++) 
				rigthWay[i] = way.get(rigthWay.length - i - 1);
			return new Route(rigthWay);
		}
	}
	
	
}
