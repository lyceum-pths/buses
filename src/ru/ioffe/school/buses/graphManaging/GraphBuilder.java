package ru.ioffe.school.buses.graphManaging;

import java.util.Collection;
import java.util.HashSet;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;

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
		Road[] res = new Road[roads.size()];
		int pos = 0;
		for (Road road : roads) 
			res[pos++] = road;
		return res;
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
}
