package ru.ioffe.school.buses.graphManaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;

/**
 * This class will help you to do something with graph
 *
 */

public class GraphBuilder {
	HashSet<Road> roads;

	public GraphBuilder(Road...roads) {
		this.roads = new HashSet<>();
		for (Road road : roads)
			this.roads.add(road);
	}

	public GraphBuilder(Collection<Road> roads) {
		this.roads = new HashSet<>(roads);
	}

	public Road[] getRoads() {
		return roads.toArray(new Road[roads.size()]);
	}

	@SuppressWarnings("unchecked")
	public Graph getGraph(boolean ignoreRestrictions) {
		ArrayList<Point> nodes = new ArrayList<>();
		HashMap<Point, Integer> indexs = new HashMap<>();
		for (Road road : roads) {
			addPoint(indexs, nodes, road.getFrom());
			addPoint(indexs, nodes, road.getTo());
		}
		nodes.trimToSize();
		ArrayList<Edge>[] edges = new ArrayList[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			edges[i] = new ArrayList<>();
		int from, to;
		for (Road road : roads) {
			from = indexs.get(road.getFrom());
			to = indexs.get(road.getTo());
			edges[from].add(new Edge(road, from, to));
			if (ignoreRestrictions) 
				edges[to].add(new Edge(road, to, from));
		}
		for (ArrayList<Edge> list : edges)
			list.trimToSize();
		return new Graph(nodes, edges, indexs);
	}
	
	public Graph getGraph() {
		return getGraph(false);
	}

	@SuppressWarnings("unchecked")
	public Graph getGraph(double speedBound, boolean ignoreRestrictions) {
		ArrayList<Point> nodes = new ArrayList<>();
		HashMap<Point, Integer> indexs = new HashMap<>();
		for (Road road : roads) {
			addPoint(indexs, nodes, road.getFrom());
			addPoint(indexs, nodes, road.getTo());
		}
		nodes.trimToSize();
		ArrayList<Edge>[] edges = new ArrayList[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			edges[i] = new ArrayList<>();
		int from, to;
		for (Road road : roads) {
			from = indexs.get(road.getFrom());
			to = indexs.get(road.getTo());
			edges[from].add(new Edge(road, speedBound, from, to));
			if (ignoreRestrictions)
				edges[to].add(new Edge(road, speedBound, to, from));
		}
		for (ArrayList<Edge> list : edges)
			list.trimToSize();
		return new Graph(nodes, edges, indexs);
	}

	public Graph getGraph(double speedBound) {
		return getGraph(speedBound, false);
	}

	private void addPoint(HashMap<Point, Integer> indexs, ArrayList<Point> nodes, Point node) {
		if (indexs.containsKey(node))
			return;
		indexs.put(node, nodes.size());
		nodes.add(node);
	}

	public void addRoad(Road road) {
		roads.add(road);
	}

	/**
	 * @return number of roads which contained in current graph
	 */

	public int size() {
		return roads.size();
	}

	/**
	 * This method will find nearest point which lay on road and separate that road by this point
	 * @param p is the point which should become a cross-road
	 * @return added point
	 */

	public Point split(Point p) {
		if (roads.size() == 0)
			throw new IllegalArgumentException("There are no roads");
		Road best = null;
		double bestDist = Double.POSITIVE_INFINITY, current;
		for (Road road : roads) {
			if (road.getFrom().equals(p) || road.getTo().equals(p))
				return p;
			current = getDistance(road, p);
			if (current < bestDist) {
				best = road;
				bestDist = current;
			}
		}
		Point a = best.getFrom();
		Point b = best.getTo();
		double dx = b.getX() - a.getX();
		double dy = b.getY() - a.getY();
		double scalarProduct = dx * (p.getX() - a.getX()) + dy * (p.getY() - a.getY());
		if (scalarProduct <= 0)
			return a;
		scalarProduct /= best.getLength();
		if (scalarProduct >= best.getLength())
			return b;
		double k = scalarProduct / best.length;
		Point separator = new Point(0, a.getX() + dx * k, a.getY() + dy * k);
		roads.remove(best);
		boolean invert = roads.remove(best.invert());
		Road road = new Road(a, separator);
		roads.add(road);
		if (invert)
			roads.add(road.invert());
		road = new Road(separator, b);
		roads.remove(best.invert());
		roads.add(road);
		if (invert)
			roads.add(road.invert());
		return separator;
	}

	private double getDistance(Road road, Point point) {
		Point a = road.getFrom();
		Point b = road.getTo();
		double scalarProduct = (b.getX() - a.getX()) * (point.getX() - a.getX()) + (b.getY() - a.getY()) * (point.getY() - a.getY());
		if (scalarProduct > 0 && scalarProduct < road.getLength() * road.getLength())
			return getDistanceFromPointToLine(road, point);
		return Math.min(GeographyManager.getDistance(a, point), GeographyManager.getDistance(b, point));
	}

	/**
	 * @return distance from point "point" to line which contain road "road"
	 */

	private double getDistanceFromPointToLine(Road road, Point point) {
		Point a = road.getFrom();
		Point b = road.getTo();
		double vectorProduct = Math.abs((b.getX() - a.getX()) * (point.getY() - a.getY()) -
				(b.getY() - a.getY()) * (point.getX() - a.getX()));
		return vectorProduct / road.getLength();
	}

	/**
	 * This method separate graph on different connected components
	 * @return array of GraphBuilder which contains components
	 */

	public GraphBuilder[] separateGraph() {
		SNM snm = new SNM();
		for (Road road : roads) 
			snm.merge(road.getFrom(), road.getTo());
		HashMap<Integer, Integer> map = new HashMap<>();
		GraphBuilder[] graphs = new GraphBuilder[snm.size];
		for (int i = 0; i < snm.size; i++)
			graphs[i] = new GraphBuilder();
		int iterator = 0;
		int group; 
		Integer index;
		for (Road road : roads) {
			group = snm.get(road.getFrom());
			index = map.get(group);
			if (index == null) {
				index = iterator++;
				map.put(group, index);
			}
			graphs[index].addRoad(road);
		}
		return graphs;
	}

	/**
	 * This method separate graph on different connected components and find component 
	 * which contains more or equal roads than every another
	 * @return GraphBuilder which contain this biggest component
	 */

	public GraphBuilder findMaxComponent() {
		GraphBuilder[] comps = separateGraph();
		int maxIndex = 0;
		if (comps.length == 0)
			throw new IllegalArgumentException("Graph empty!");
		for (int i = 1; i < comps.length; i++) {
			if (comps[maxIndex].size() < comps[i].size())
				maxIndex = i;
		}
		return comps[maxIndex];
	}
	
	public Point findNearestPoint(Point point) {
		Point nearest = null;
		double distance = Double.POSITIVE_INFINITY;
		double buffer;
		for (Road road : roads) {
			buffer = GeographyManager.getSquaredDistance(point, road.from);
			if (buffer < distance) {
				distance = buffer;
				nearest = road.from;
			}
			buffer = GeographyManager.getSquaredDistance(point, road.to);
			if (buffer < distance) {
				distance = buffer;
				nearest = road.to;
			}
		}
		return nearest;
	}

	private static class SNM {
		ArrayList<Integer> pred;
		ArrayList<Integer> rang;
		HashMap<Point, Integer> map;
		int size;

		public SNM() {
			pred = new ArrayList<>();
			rang = new ArrayList<>();
			map = new HashMap<>();
			size = 0;
		}

		public int get(int n) {
			if (pred.get(n) == n) {
				return n;
			}
			pred.set(n, get(pred.get(n)));
			return pred.get(n);
		}

		public int get(Point p) {
			return get(find(p));
		}

		public int find(Point p) {
			Integer index = map.get(p);
			if (index != null)
				return index;
			size++;
			index = pred.size();
			pred.add(index);
			rang.add(0);
			map.put(p, index);
			return index;
		}

		public void merge(Point a, Point b) {
			merge(find(a), find(b));
		}

		public void merge(int i, int j) {
			i = get(i);
			j = get(j);
			if (i == j) {
				return;
			}
			if (rang.get(i) < rang.get(j)) {
				int help = i;
				i = j;
				j = help;
			}
			pred.set(j, i);
			size--;
			if (rang.get(i) == rang.get(j))
				rang.set(i, Math.max(rang.get(i), rang.get(j) + 1));
		}
	}
}
