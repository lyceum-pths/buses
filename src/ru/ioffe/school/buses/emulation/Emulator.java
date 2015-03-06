package ru.ioffe.school.buses.emulation;

import java.util.ArrayList;
import java.util.HashMap;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.BusSegment;
import ru.ioffe.school.buses.data.Night;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.Segment;
import ru.ioffe.school.buses.data.Station;
import ru.ioffe.school.buses.data.StraightSegment;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;
import ru.ioffe.school.buses.timeManaging.Transfer;

public class Emulator {

	//  input data
	final Point[] stations;
	final ArrayList<Transfer>[] transfers; 
	final HashMap<Point, Integer> indexs;
	final double speed;
	int cnt = 0;

	@SuppressWarnings("unchecked")
	public Emulator(Station[] stations, double speed, Transfer... transfers) {
		this.stations = new Point[stations.length];
		for (int i = 0; i < stations.length; i++)
			this.stations[i] = stations[i].getPosition();
		this.transfers = new ArrayList[stations.length];
		this.speed = speed;
		this.indexs = new HashMap<>();
		for (int i = 0; i < stations.length; i++) {
			indexs.put(this.stations[i], i);
			this.transfers[i] = new ArrayList<>();
		}
		for (Transfer tr : transfers)
			this.transfers[indexs.get(tr.getFrom())].add(tr);
	}

	public Report startEmulation(Night nigth, int threadsNumber) {
		if (threadsNumber < 1) 
			throw new IllegalArgumentException("Bad idea");
		threadsNumber = Math.min(threadsNumber, stations.length);
		Thread[] threads = new Thread[threadsNumber];
		int n = nigth.getPersons().length;
		Route[] routes = new Route[n];
		int d = (n - 1) / threadsNumber + 1;  // rounding up
		for (int i = 0; i < threadsNumber; i++) {
			threads[i] = new Thread(
					new Module(nigth.getPersons(), i * d, Math.min((i + 1) * d, n), routes));
			threads[i].start();
		}
		for (int i = 0; i < threadsNumber; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {}
		}
		System.out.println(cnt + " people used a bus");
		return new Report(routes);
	}


	public class Module implements Runnable {
		// input
		Person[] persons;
		int begin;
		int end;

		// output
		Route[] routes;


		public Module(Person[] persons, int begin, int end, Route[] routes) {
			this.begin = begin;
			this.end = end;
			this.persons = persons;
			this.routes = routes;
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
			Bus[] mode = new Bus[n + 2];
			double[] time = new double[n + 2];
			boolean[] checked = new boolean[n + 2];
			for (int i = 0; i < n + 2; i++) {
				time[i] = Integer.MAX_VALUE;
				pred[i] = -1;
			}
			time[n] = person.getTime();
			ArrayList<Transfer> roads;
			int indexTo;
			double nextTime, dt;
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
						if (nextTime == Double.POSITIVE_INFINITY)
							continue;
						if (time[indexTo] > nextTime + tr.getContinuance()) {
							time[indexTo] = nextTime + tr.getContinuance();
							pred[indexTo] = minIndex;
							mode[indexTo] = tr.getBus(); 
						}
					}
				}
				// maybe here should be some magic because people usually don't walk for long distance 
				for (int i = 0; i < time.length; i++) {
					if (checked[i])
						continue;
					// next line should work slowly
					dt = GeographyManager.getDistance(getPoint(person, minIndex), getPoint(person, i)) / speed;
					if (time[i] > time[minIndex] + dt) {
						time[i] = time[minIndex] + dt;
						pred[i] = minIndex;
						mode[i] = null;
					}
				}
			}
			ArrayList<Segment> way = new ArrayList<>();
			int current = time.length - 1;
			while (pred[current] != -1) {
				if (mode[current] == null) {
					way.add(new StraightSegment(getPoint(person, pred[current]),
						getPoint(person, current), time[pred[current]], time[current]));
				} else {
					cnt++;
					way.add(new BusSegment(mode[current], time[pred[current]], time[current]));
				}
				current = pred[current];
			}
			Segment[] rigthWay = new Segment[way.size()];
			for (int i = 0; i < rigthWay.length; i++) 
				rigthWay[i] = way.get(rigthWay.length - i - 1);
			return new Route(rigthWay);
		}
	}

	
}
