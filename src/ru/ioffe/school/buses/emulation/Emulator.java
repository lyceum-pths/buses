package ru.ioffe.school.buses.emulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import ru.ioffe.school.buses.Settings;
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
import ru.ioffe.school.buses.graphManaging.Edge;
import ru.ioffe.school.buses.graphManaging.Graph;
import ru.ioffe.school.buses.structures.Heap;
import ru.ioffe.school.buses.timeManaging.TimeTable;
import ru.ioffe.school.buses.timeManaging.Transfer;

public class Emulator {

	//  input data
	final ArrayList<Point> nodes;
	final ArrayList<BusEdge>[] buses; 
	final ArrayList<Edge>[] edges;
	final HashMap<Point, Integer> indexs;
	final TimeTable timeTable;

	@SuppressWarnings("unchecked")
	public Emulator(TimeTable timeTable, Road[] roads) {
		double speed = Settings.PERSONS_SPEED;
		this.nodes = new ArrayList<>();
		this.timeTable = timeTable;
		this.indexs = new HashMap<>();
		for (Road road : roads) {
			addNode(road.getFrom());
			addNode(road.getTo());
		}
		nodes.trimToSize();
		this.buses = new ArrayList[nodes.size()];
		this.edges = new ArrayList[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			this.buses[i] = new ArrayList<>();
			this.edges[i] = new ArrayList<>();
		}
		for (Transfer tr : timeTable.getTransfers())
			this.buses[indexs.get(tr.getFrom())].add(new BusEdge(tr));
		int from, to;
		for (Road road : roads) {
			from = indexs.get(road.getFrom());
			to = indexs.get(road.getTo());
			this.edges[from].add(new Edge(road, speed, from, to));
			this.edges[to].add(new Edge(road, speed, to, from));
		}
		for (ArrayList<BusEdge> list : buses)
			list.trimToSize();
		for (ArrayList<Edge> list : edges)
			list.trimToSize();
	}

	public Emulator(TimeTable timeTable, Collection<Road> roads) {
		this(timeTable, roads.toArray(new Road[roads.size()]));
	}
	
	@SuppressWarnings("unchecked")
	public Emulator(TimeTable timeTable, Graph graph) {
		this.nodes = graph.getNodes();
		this.edges = graph.getEdges();
		this.indexs = graph.getIndexs();
		this.timeTable = timeTable;
		this.buses = new ArrayList[nodes.size()];
		for (int i = 0; i < buses.length; i++)
			buses[i] = new ArrayList<>();
		for (Transfer tr : timeTable.getTransfers())
			this.buses[indexs.get(tr.getFrom())].add(new BusEdge(tr));
		for (ArrayList<BusEdge> list : buses)
			list.trimToSize();
	}
	
	public Graph getGraph() {
		return new Graph(nodes, edges, indexs);
	}
	
	public TimeTable getTimeTable() {
		return timeTable;
	}

	private void addNode(Point point) {
		if (indexs.containsKey(point))
			return;
		indexs.put(point, nodes.size());
		nodes.add(point);
	}

	public Report startEmulation(Night nigth, int threadsNumber) {
		long time = System.currentTimeMillis();
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
		System.out.println("Time for emulating: " + (System.currentTimeMillis() - time));
		return new Report(routes, timeTable);
	}

	public ShortReport startFastEmulation(Night nigth, int threadsNumber) {
		long time = System.currentTimeMillis();
		if (threadsNumber < 1) 
			throw new IllegalArgumentException("Bad idea");
		threadsNumber = Math.min(threadsNumber, nodes.size());
		Thread[] threads = new Thread[threadsNumber];
		int n = nigth.getPersons().length;
		double[] times = new double[n];
		int d = (n - 1) / threadsNumber + 1;  // rounding up
		for (int i = 0; i < threadsNumber; i++) {
			threads[i] = new Thread(
					new Module(nigth.getPersons(), i * d, Math.min((i + 1) * d, n), times, false));
			threads[i].start();
		}
		for (int i = 0; i < threadsNumber; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {}
		}
		System.out.println("Time for fast emulating: " + (System.currentTimeMillis() - time));
		return new ShortReport(nigth.getPersons(), times, timeTable);
	}
	
	public ShortReport startQuantumEmulation(Night nigth, int threadsNumber) {
		long time = System.currentTimeMillis();
		if (threadsNumber < 1) 
			throw new IllegalArgumentException("Bad idea");
		threadsNumber = Math.min(threadsNumber, nodes.size());
		Thread[] threads = new Thread[threadsNumber];
		int n = nigth.getPersons().length;
		double[] times = new double[n];
		int d = (n - 1) / threadsNumber + 1;  // rounding up
		for (int i = 0; i < threadsNumber; i++) {
			threads[i] = new Thread(
					new Module(nigth.getPersons(), i * d, Math.min((i + 1) * d, n), times, true));
			threads[i].start();
		}
		for (int i = 0; i < threadsNumber; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {}
		}
		System.out.println("Time for quantum emulating: " + (System.currentTimeMillis() - time));
		return new ShortReport(nigth.getPersons(), times, timeTable);
	}

	private class Module implements Runnable {
		// input
		Person[] persons;
		int begin;
		int end;
		boolean isQuantum;

		// output
		PersonalReport[] routes;
		double[] times;


		public Module(Person[] persons, int begin, int end, PersonalReport[] routes) {
			this.begin = begin;
			this.end = end;
			this.persons = persons;
			this.routes = routes;
		}

		public Module(Person[] persons, int begin, int end, double[] times, boolean isQuantum) {
			this.begin = begin;
			this.end = end;
			this.persons = persons;
			this.times = times;
			this.isQuantum = isQuantum;
		}

		@Override
		public void run() {
			if (routes == null) { // fast emulation
				for (int i = begin; i < end; i++) {
					try {
						times[i] = isQuantum? findAverageTime(persons[i]) : findTime(persons[i]);
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Way wasn't found");
					}
				}
			} else {
				for (int i = begin; i < end; i++) {
					try {
						routes[i] = findRoute(persons[i]);
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Way wasn't found");
					}
				}
			}
		}

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

		private PersonalReport findRoute(Person person) {
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
			Heap<Step> heap = new Heap<>();
			heap.add(new Step(person.getTime(), from));
			int current;
			int next;
			while (!heap.isEmpty() && !checked[to]) {
				current = heap.poll().getTo();
				if (checked[current])
					continue;
				checked[current] = true;
				for (Edge edge : edges[current]) {
					next = edge.getEnd();
					if (checked[next])
						continue;
					if (time[current] + edge.getTime() < time[next]) {
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
						time[next] = nextTime + bus.getContinuance();
						heap.add(new Step(time[next], next));
						pred[next] = current;
						modes[next] = new Mode(bus.getTransfer(), nextTime - time[current]);
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
					way.add(new BusSegment(modes[current].getBus(), modes[current].getTime(), time[pred[current]] + modes[current].getWaitingTime(), 
							time[current], nodes.get(pred[current]), nodes.get(current)));
					if (modes[current].timeWaiting > 0)
						way.add(new WaitingSegment(nodes.get(pred[current]), time[pred[current]], time[pred[current]] + modes[current].timeWaiting));
				}
				current = pred[current];
			}
			Segment[] rigthWay = new Segment[way.size()];
			for (int i = 0; i < rigthWay.length; i++) 
				rigthWay[i] = way.get(rigthWay.length - i - 1);
			return new PersonalReport(person, new Route(rigthWay));
		}
		
		// find only time which person must spend to come home
		private double findTime(Person person) {
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
			double[] time = new double[n];
			boolean[] checked = new boolean[n];
			Arrays.fill(time, Double.POSITIVE_INFINITY);
			time[from] = person.getTime();
			double nextTime;
			Heap<Step> heap = new Heap<>();
			heap.add(new Step(person.getTime(), from));
			int current;
			int next;
			while (!heap.isEmpty() && !checked[to]) {
				current = heap.poll().getTo();
				if (checked[current])
					continue;
				checked[current] = true;
				for (Edge edge : edges[current]) {
					next = edge.getEnd();
					if (checked[next])
						continue;
					if (time[current] + edge.getTime() < time[next]) {
						time[next] = time[current] + edge.getTime();
						heap.add(new Step(time[next], next));
					}
				}
				for (BusEdge bus : buses[current]) {
					next = bus.getEnd();
					if (checked[next])
						continue;
					nextTime = bus.nextDeparture(time[current]);
					if (nextTime + bus.getContinuance() < time[next]) {
						time[next] = nextTime + bus.getContinuance();
						heap.add(new Step(time[next], next));
					}
				}
			}
			if (time[to] == Double.POSITIVE_INFINITY)
				throw new IllegalArgumentException(
						"Person cant come home: " + person);		
			return time[to] - person.getTime();
		}
		
		// find average time for person to go from his start point for every cross-road in town
		private double findAverageTime(Person person) {
			int from;
			// looking for point
			try {
				from = indexs.get(person.getFrom());
			} catch (NullPointerException e) {
				from = tryFindNearestPoint(person.getFrom());
				System.err.println("There isn't such point:" + person.getFrom());
				System.err.println("For the start will be used the nearest point: " + nodes.get(from) + 
						" (distance between = " + GeographyManager.getDistance(person.getFrom(), nodes.get(from)));
			}
			int n = nodes.size();
			double[] time = new double[n];
			boolean[] checked = new boolean[n];
			Arrays.fill(time, Double.POSITIVE_INFINITY);
			time[from] = person.getTime();
			double nextTime;
			Heap<Step> heap = new Heap<>();
			heap.add(new Step(person.getTime(), from));
			int current;
			int next;
			while (!heap.isEmpty()) {
				current = heap.poll().getTo();
				if (checked[current])
					continue;
				checked[current] = true;
				for (Edge edge : edges[current]) {
					next = edge.getEnd();
					if (checked[next])
						continue;
					if (time[current] + edge.getTime() < time[next]) {
						time[next] = time[current] + edge.getTime();
						heap.add(new Step(time[next], next));
					}
				}
				for (BusEdge bus : buses[current]) {
					next = bus.getEnd();
					if (checked[next])
						continue;
					nextTime = bus.nextDeparture(time[current]);
					if (nextTime + bus.getContinuance() < time[next]) {
						time[next] = nextTime + bus.getContinuance();
						heap.add(new Step(time[next], next));
					}
				}
			}
			int achievabled = 0;
			double average = 0;
			for (double d : time)
				if (d != Double.POSITIVE_INFINITY) {
					achievabled++;
					average += d - person.getTime();
				}
			return average / achievabled;
		}
	}

	private class Mode {
		final Transfer transfer;
		final double timeWaiting;

		public Mode(Transfer transfer, double timeWaiting) {
			this.transfer = transfer;
			this.timeWaiting = timeWaiting;
		}		

		public Bus getBus() {
			return transfer.getBus();
		}

		public double getTime() {
			return transfer.getTime();
		}

		public double getWaitingTime() {
			return timeWaiting;
		}
	}

	private class BusEdge {
		final Transfer transfer;
		final int to;

		public BusEdge(Transfer transfer) {
			this.transfer = transfer;
			this.to = indexs.get(transfer.getTo());
		}

		public int getEnd() {
			return to;
		}

		public Transfer getTransfer() {
			return transfer;
		}

		public double getContinuance() {
			return transfer.getContinuance();
		}

		public double nextDeparture(double time) {
			return transfer.getNextDeparture(time);
		}
	}

	private class Step implements Comparable<Step> {
		double time;
		int to;

		public Step(double time, int to) {
			this.time = time;
			this.to = to;
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
