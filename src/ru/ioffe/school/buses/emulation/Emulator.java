package emulation;

import java.util.ArrayList;
import java.util.HashMap;

import teachMeToSeparateClassesOnPackeges.GeographyManager;
import teachMeToSeparateClassesOnPackeges.Nigth;
import teachMeToSeparateClassesOnPackeges.Person;
import teachMeToSeparateClassesOnPackeges.Point;
import teachMeToSeparateClassesOnPackeges.Route;
import teachMeToSeparateClassesOnPackeges.Segment;
import teachMeToSeparateClassesOnPackeges.Transfer;

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
		for (Transfer tr : transfers)
			this.transfers[indexs.get(tr.getFrom())].add(tr);
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
			if (index == stations.length) 
				return person.getFrom();
			if (index == stations.length + 1)
				return person.getTo();
			return stations[index];
		}

		public int getIndex(Person person, Point point) {
			if (point == person.getFrom())
				return stations.length;
			if (point == person.getTo())
				return stations.length + 1;
			return indexs.get(point);
		}

		public ArrayList<Transfer> getRoads(int index) {
			return index < transfers.length? transfers[index] : null;
		}

		public Route findRoute(Person person) {
			// begin = n; end = n + 1
			int n = stations.length;
			int[] pred = new int[n + 2];
			int[] time = new int[n + 2];
			boolean[] checked = new boolean[n + 2];
			for (int i = 0; i < n + 2; i++) {
				time[i] = Integer.MAX_VALUE;
				pred[i] = -1;
			}
			time[n] = person.getTime();
			ArrayList<Transfer> roads;
			int indexTo;
			int nextTime;
			while(!checked[n + 1]) {
				int minIndex = -1;
				for (int i = 0; i < n + 2; i++) 
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
