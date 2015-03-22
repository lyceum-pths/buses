package ru.ioffe.school.buses.graphManaging;

import java.util.ArrayList;
import java.util.HashMap;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;
import ru.ioffe.school.buses.structures.Heap;

public class RoadManager {
	final ArrayList<Point> nodes;
	final HashMap<Point, Integer> indexs;
	final ArrayList<Edge>[] roads;
	final int size;

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
		this.size = roads.length;
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

	public Road[] findWay(Point start, Point finish) {
		int from;
		int to;
		try {
			from = indexs.get(start);
		} catch (NullPointerException e) {
			from = tryFindNearestPoint(start);
			System.err.println("There isn't such point:" + start);
			System.err.println("For the start will be used the nearest point: " + nodes.get(from) + 
					" (distance between = " + GeographyManager.getDistance(start, nodes.get(from)));
		}
		try {
			to = indexs.get(finish);
		} catch (NullPointerException e) {
			to = tryFindNearestPoint(finish);
			System.err.println("There isn't such point:" + finish);
			System.err.println("For the finish will be used the nearest point: " + nodes.get(to) + 
					" (distance between = " + GeographyManager.getDistance(finish, nodes.get(to)));
		}
//		TreeSet<Step> heap = new TreeSet<>();
//		boolean[] checked = new boolean[nodes.size()];
//		double[] distance = new double[nodes.size()];
//		Edge[] lastEdge = new Edge[nodes.size()];
//		for (int i = 0; i < nodes.size(); i++)
//			distance[i] = Double.POSITIVE_INFINITY;
//		distance[from] = 0;
//		heap.add(new Step(0, from));
//		int current;
//		int next;
//		while (!heap.isEmpty() && !checked[to]) {
//			current = heap.pollFirst().getTo();
//			checked[current] = true;
//			for (Edge edge : roads[current]) {
//				next = edge.getEnd();
//				if (!checked[next]
//						&& distance[current] + edge.getLength() < distance[next]) {
//					heap.remove(new Step(distance[next], next));
//					distance[next] = distance[current] + edge.getLength();
//					heap.add(new Step(distance[next], next));
//					lastEdge[next] = edge;
//				}
//			}
//		}
		Heap<Step> heap = new Heap<>(new Step[size]);
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
			current = heap.poll().getTo();
			if (checked[current])
				continue;
			checked[current] = true;
			for (Edge edge : roads[current]) {
				next = edge.getEnd();
				if (!checked[next]
						&& distance[current] + edge.getLength() < distance[next]) {
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
	
	public Point[] getCrossroads() { 
		return nodes.toArray(new Point[nodes.size()]);
	}

	private class Step implements Comparable<Step> {
		double length;
		int to;

		public Step(double length, int to) {
			this.length = length;
			this.to = to;
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

	private class Edge {
		Road road;
		int from, to;

		public Edge(Road road) {
			from = indexs.get(road.from);
			to = indexs.get(road.to);
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
