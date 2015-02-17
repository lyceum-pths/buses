package ru.ioffe.school.buses.graphCreation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;

public class RoadManager {
	ArrayList<Point> nodes;
	HashMap<Point, Integer> indexs;
	ArrayList<Edge>[] roads;
	GeographyManager geographyManager;

	private void addNode(Point point) {
		if (indexs.containsKey(point))
			return;
		indexs.put(point, nodes.size());
		nodes.add(point);
	}

	@SuppressWarnings("unchecked")
	public RoadManager(GeographyManager geographyManager, Road... roads) {
		this.geographyManager = geographyManager;
		this.indexs = new HashMap<>();
		this.nodes = new ArrayList<>();
		for (Road road : roads)
			for (Point point : road.getCrossroads())
				addNode(point);
		this.roads = new ArrayList[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			this.roads[i] = new ArrayList<>();
		int lastPoint;
		int currentPoint;
		for (Road road : roads) {
			lastPoint = -1;
			for (Point point : road.getCrossroads()) {
				currentPoint = indexs.get(point);
				if (lastPoint != -1)
					this.roads[lastPoint].add(new Edge(lastPoint, currentPoint));
				lastPoint = currentPoint;
			}
		}
	}

	public Road findWay(Point start, Point finish) {
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
		int[] pred = new int[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			distance[i] = Double.POSITIVE_INFINITY;
			pred[i] = -1;
		}
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
					pred[next] = current;
				}
			}
		}
		if (distance[to] == Double.POSITIVE_INFINITY)
			throw new IllegalArgumentException(
					"There is no way between these points.");
		ArrayList<Point> invertedWay = new ArrayList<>();
		current = to;
		while (current != -1) {
			invertedWay.add(nodes.get(current));
			current = pred[current];
		}
		Point[] way = new Point[invertedWay.size()];
		for (int i = 0; i < way.length; i++)
			way[i] = invertedWay.get(way.length - 1 - i);
		return new Road(way);
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
		int nodeStart;
		int nodeEnd;

		double length;

		public Edge(int from, int to) {
			nodeStart = from;
			nodeEnd = to;
			length = -1;
		}

		public Edge(Point from, Point to) {
			nodeStart = indexs.get(from);
			nodeEnd = indexs.get(to);
			length = -1;
		}

		public double getLength() {
			if (length == -1) { // didn't calculated yet
				length = geographyManager.getDistance(nodes.get(nodeStart),
						nodes.get(nodeEnd));
			}
			return length;
		}

		public int getStart() {
			return nodeStart;
		}

		public int getEnd() {
			return nodeEnd;
		}
	}
}
