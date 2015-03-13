package ru.ioffe.school.buses.emulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.BusSegment;
import ru.ioffe.school.buses.data.Night;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.Segment;
import ru.ioffe.school.buses.data.StraightSegment;
import ru.ioffe.school.buses.data.WaitingSegment;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;
import ru.ioffe.school.buses.timeManaging.Transfer;

public class Emulator {

	//  input data
	final ArrayList<Point> nodes;
	final ArrayList<BusEdge>[] buses; 
	final ArrayList<Edge>[] edges;
	final HashMap<Point, Integer> indexs;
	final double speed;

	@SuppressWarnings("unchecked")
	public Emulator(double speed, Transfer[] transfers, Road[] roads) {
		this.nodes = new ArrayList<>();
		for (Transfer transfer : transfers) { 
			addNode(transfer.getFrom());
			addNode(transfer.getTo());
		}
		for (Road road : roads) {
			addNode(road.getFrom());
			addNode(road.getTo());
		}
		this.buses = new ArrayList[nodes.size()];
		this.edges = new ArrayList[nodes.size()];
		this.speed = speed;
		this.indexs = new HashMap<>();
		for (int i = 0; i < nodes.size(); i++) {
			this.buses[i] = new ArrayList<>();
			this.edges[i] = new ArrayList<>();
		}
		for (Transfer tr : transfers)
			this.buses[indexs.get(tr.getFrom())].add(new BusEdge(tr));
		int from, to;
		for (Road road : roads) {
			from = indexs.get(road.getFrom());
			to = indexs.get(road.getTo());
			this.edges[from].add(new Edge(from, to, road.getLength()));
			//			if (!road.isOneway)
			this.edges[to].add(new Edge(to, from, road.getLength()));
		}
	}

	@SuppressWarnings("unchecked")
	public Emulator(double speed, Collection<Transfer> transfers, Collection<Road> roads) {
		this.nodes = new ArrayList<>();
		this.indexs = new HashMap<>();
		for (Transfer transfer : transfers) { 
			addNode(transfer.getFrom());
			addNode(transfer.getTo());
		}
		for (Road road : roads) {
			addNode(road.getFrom());
			addNode(road.getTo());
		}
		this.buses = new ArrayList[nodes.size()];
		this.edges = new ArrayList[nodes.size()];
		this.speed = speed;
		for (int i = 0; i < nodes.size(); i++) {
			this.buses[i] = new ArrayList<>();
			this.edges[i] = new ArrayList<>();
		}
		for (Transfer tr : transfers)
			this.buses[indexs.get(tr.getFrom())].add(new BusEdge(tr));
		int from, to;
		for (Road road : roads) {
			from = indexs.get(road.getFrom());
			to = indexs.get(road.getTo());
			this.edges[from].add(new Edge(from, to, road.getLength()));
			//			if (!road.isOneway)
			this.edges[to].add(new Edge(to, from, road.getLength()));
		}
	}

	private void addNode(Point point) {
		if (indexs.containsKey(point))
			return;
		indexs.put(point, nodes.size());
		nodes.add(point);
	}

	public Report startEmulation(Night nigth, int threadsNumber) {
		if (threadsNumber < 1) 
			throw new IllegalArgumentException("Bad idea");
		threadsNumber = Math.min(threadsNumber, nodes.size());
		Thread[] threads = new Thread[threadsNumber];
		int n = nigth.getPersons().length;
		PersonalReport[] routes = new PersonalReport[n];
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
		//		System.out.println(cnt + " people used a bus");
		return new Report(routes);
	}


	public class Module implements Runnable {
		// input
		Person[] persons;
		int begin;
		int end;

		// output
		PersonalReport[] routes;


		public Module(Person[] persons, int begin, int end, PersonalReport[] routes) {
			this.begin = begin;
			this.end = end;
			this.persons = persons;
			this.routes = routes;
		}

		@Override
		public void run() {
			for (int i = begin; i < end; i++) {
				try {
					routes[i] = findRoute(persons[i]);
				} catch (Exception e) {
					System.err.println("Way wasn't found");
				}
			}
		}
		//
		//		public Point getPoint(Person person, int index) {
		//			if (index == nodes.size()) 
		//				return person.getFrom();
		//			if (index == nodes.size() + 1)
		//				return person.getTo();
		//			return nodes.get(index);
		//		}
		//
		//		public int getIndex(Person person, Point point) {
		//			if (point == person.getFrom())
		//				return nodes.index.;
		//			if (point == person.getTo())
		//				return nodes.length + 1;
		//			return indexs.get(point);
		//		}

		//		public ArrayList<Transfer> getRoads(int index) {
		//			return index < transfers.length? transfers[index] : null;
		//		}

		private int tryFindNearestPoint(Point input) {
			double minDist = Double.POSITIVE_INFINITY;
			double dist;
			int ans = -1;
			for (int i = 0; i < nodes.size(); i++) {
				dist = GeographyManager.getSquaredDistance(nodes.get(i), input);
				if (minDist > dist) {
					ans = i;
					minDist = dist;
				}
			}
			return ans;
		}

		public PersonalReport findRoute(Person person) {
			int from;
			int to;
			// looking for points
			try {
				from = indexs.get(person.getFrom());
			} catch (NullPointerException e) {
				from = tryFindNearestPoint(person.getFrom());
				System.err.println("There isn't such point:" + person.getFrom());
				System.err.println("For the start will be used the nearest point: " + nodes.get(from) + 
						" (distance between = " + GeographyManager.getDistance(person.getFrom(), nodes.get(from)));
			}
			try {
				to = indexs.get(person.getTo());
			} catch (NullPointerException e) {
				to = tryFindNearestPoint(person.getTo());
				System.err.println("There isn't such point:" + person.getTo());
				System.err.println("For the finish will be used the nearest point: " + nodes.get(to) + 
						" (distance between = " + GeographyManager.getDistance(person.getTo(), nodes.get(to)));
			}

			int n = nodes.size();
			int[] pred = new int[n];
			Mode[] modes = new Mode[n];
			double[] time = new double[n];
			boolean[] checked = new boolean[n];
			for (int i = 0; i < n; i++) {
				time[i] = Double.POSITIVE_INFINITY;
				pred[i] = -1;
			}
			time[from] = person.getTime();
			double nextTime;
			TreeSet<Step> heap = new TreeSet<>();
			heap.add(new Step(person.getTime(), from));
			int current;
			int next;
			while (!heap.isEmpty() && !checked[to]) {
				current = heap.pollFirst().getTo();
				checked[current] = true;
				for (Edge edge : edges[current]) {
					next = edge.getEnd();
					if (checked[next])
						continue;
					if (time[current] + edge.getTime() < time[next]) {
						heap.remove(new Step(time[next], next));
						time[next] = time[current] + edge.getTime();
						heap.add(new Step(time[next], next));
						pred[next] = current;
						modes[next] = null;
					}
				}
				for (BusEdge bus : buses[current]) {
					next = bus.getEnd();
					if (checked[next])
						continue;
					nextTime = bus.nextDeparture(time[current]);
					if (nextTime + bus.getContinuance() < time[next]) {
						heap.remove(new Step(time[next], next));
						time[next] = nextTime + bus.getContinuance();
						heap.add(new Step(time[next], next));
						pred[next] = current;
						modes[next] = new Mode(bus.getBus(), nextTime - time[current]);
					}
				}
			}
			if (time[to] == Double.POSITIVE_INFINITY)
				throw new IllegalArgumentException(
						"Person cant come home: " + person);		
			ArrayList<Segment> way = new ArrayList<>();
			current = to;
			while (pred[current] != -1) {
				if (modes[current] == null) {
					way.add(new StraightSegment(nodes.get(pred[current]),
							nodes.get(current), time[pred[current]], time[current]));
				} else {
					way.add(new BusSegment(modes[current].getBus(), time[pred[current]] + modes[current].getWaitingTime(), time[current]));
					if (modes[current].timeWaiting != 0)
						way.add(new WaitingSegment(nodes.get(current), time[pred[current]], time[pred[current]] + modes[current].timeWaiting));
				}
				current = pred[current];
			}
			Segment[] rigthWay = new Segment[way.size()];
			for (int i = 0; i < rigthWay.length; i++) 
				rigthWay[i] = way.get(rigthWay.length - i - 1);
			return new PersonalReport(person, new Route(rigthWay));
		}
	}

	private class Mode {
		final Bus bus;
		final double timeWaiting;

		public Mode(Bus bus, double timeWaiting) {
			this.bus = bus;
			this.timeWaiting = timeWaiting;
		}		

		public Bus getBus() {
			return bus;
		}

		public double getWaitingTime() {
			return timeWaiting;
		}
	}

	class Edge {
		final double time;
		final int from, to;

		public Edge(Road road) {
			from = indexs.get(road.from);
			to = indexs.get(road.to);
			this.time = road.getLength() / speed;
		}

		public Edge(int from, int to, double length) {
			this.from = from;
			this.to = to;
			this.time = length / speed;
		}

		public double getTime() {
			return time;
		}

		public int getStart() {
			return from;
		}

		public int getEnd() {
			return to;
		}
	}

	class BusEdge {
		final Transfer transfer;
		final int from, to;

		public BusEdge(Transfer transfer) {
			this.transfer = transfer;
			this.from = indexs.get(transfer.getFrom());
			this.to = indexs.get(transfer.getTo());
		}

		public int getStart() {
			return from;
		}

		public int getEnd() {
			return to;
		}

		public Bus getBus() {
			return transfer.getBus();
		}

		public double getContinuance() {
			return transfer.getContinuance();
		}

		public double nextDeparture(double time) {
			return transfer.getNextDeparture(time);
		}
	}

	class Step implements Comparable<Step> {
		double time;
		int to;

		public Step(double time, int to) {
			this.time = time;
			this.to = to;
		}

		public double getTime() {
			return time;
		}

		public int getTo() {
			return to;
		}

		@Override
		public int compareTo(Step o) {
			if (time == o.time)
				return to - o.to;
			return time > o.time ? 1 : -1;
		}
	}
}
