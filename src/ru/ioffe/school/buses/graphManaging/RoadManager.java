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
		nodes.trimToSize();
		this.roads = new ArrayList[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			this.roads[i] = new ArrayList<>();
		int from, to;
		for (Road road : roads) {
			from = indexs.get(road.getFrom());
			to = indexs.get(road.getTo());
			this.roads[from].add(new Edge(road, from, to));
		}
		for (ArrayList<Edge> list : this.roads)
			list.trimToSize();
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
		Heap<Step> heap = new Heap<>();
		boolean[] checked = new boolean[nodes.size()];
		double[] time = new double[nodes.size()];
		Edge[] lastEdge = new Edge[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			time[i] = Double.POSITIVE_INFINITY;
		time[from] = 0;
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
						&& time[current] + edge.getTime() < time[next]) {
					time[next] = time[current] + edge.getTime();
					heap.add(new Step(time[next], next));
					lastEdge[next] = edge;
				}
			}
		}
		if (time[to] == Double.POSITIVE_INFINITY)
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
		double time;
		int to;

		public Step(double length, int to) {
			this.time = length;
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
