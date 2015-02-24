package ru.ioffe.school.buses.graphManaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;

public class RoadManager {
	ArrayList<Point> nodes;
	HashMap<Point, Integer> indexs;
	ArrayList<Edge>[] roads;

	private void addNode(Point point) {
		if (indexs.containsKey(point))
			return;
		indexs.put(point, nodes.size());
		nodes.add(point);
	}

	@SuppressWarnings("unchecked")
	public RoadManager(Road... roads) {
		this.indexs = new HashMap<>();
		this.nodes = new ArrayList<>();
		for (Road road : roads) {
			addNode(road.getFrom());
			addNode(road.getTo());
		}
		this.roads = new ArrayList[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			this.roads[i] = new ArrayList<>();
		for (Road road : roads)
			this.roads[indexs.get(road.getFrom())].add(new Edge(road));
	}

	public Road[] findWay(Point start, Point finish) {
		int from;
		int to;
		try {
			from = indexs.get(start);
			to = indexs.get(finish);
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("There is no such node");
		}
		TreeSet<Step> heap = new TreeSet<>();
		boolean[] checked = new boolean[nodes.size()];
		double[] distance = new double[nodes.size()];
		Edge[] lastEdge = new Edge[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			distance[i] = Double.POSITIVE_INFINITY;
		distance[from] = 0;
		heap.add(new Step(0, from));
		int current;
		int next;
		while (!heap.isEmpty() && !checked[to]) {
			current = heap.pollFirst().getTo();
			checked[current] = true;
			for (Edge edge : roads[current]) {
				next = edge.getEnd();
				if (!checked[next]
						&& distance[current] + edge.getLength() < distance[next]) {
					heap.remove(new Step(distance[next], next));
					distance[next] = distance[current] + edge.getLength();
					heap.add(new Step(distance[next], next));
					lastEdge[next] = edge;
				}
			}
		}
		if (distance[to] == Double.POSITIVE_INFINITY)
			throw new IllegalArgumentException(
					"There is no way between these points.");
		ArrayList<Road> invertedWay = new ArrayList<>();
		current = to;
		while (lastEdge[current] != null) {
			invertedWay.add(lastEdge[current].getRoad());
			current = lastEdge[current].getStart();
		}
		Road[] way = new Road[invertedWay.size()];
		for (int i = 0; i < way.length; i++)
			way[i] = invertedWay.get(way.length - 1 - i);
		return way;
	}

	class Step implements Comparable<Step> {
		double length;
		int to;

		public Step(double length, int to) {
			this.length = length;
			this.to = to;
		}

		public double getLength() {
			return length;
		}

		public int getTo() {
			return to;
		}

		@Override
		public int compareTo(Step o) {
			if (length == o.length)
				return to - o.to;
			return length > o.length ? 1 : -1;
		}
	}

	class Edge {
		Road road;
		int from, to;

		public Edge(Road road) {
			from = indexs.get(road.from);
			to = indexs.get(road.to);
			this.road = road;
		}

		public Edge(Road road, int from, int to) {
			this.from = from;
			this.to = to;
			this.road = road;
		}

		public double getLength() {
			return road.getLength();
		}

		public int getStart() {
			return from;
		}

		public int getEnd() {
			return to;
		}

		public Road getRoad() {
			return road;
		}
	}
}
