package ru.ioffe.school.buses.graphManaging;

import java.io.Serializable;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;

/**
 * This class contains information about road from point "from" to point "to".
 * The information is presented as a list of points crossed by the road.
 * It is used for looking for ways in a town.
 */

public class Road implements Serializable {
	
	private static final long serialVersionUID = 2284501799222650859L;
	
	final Point from, to;
	// final double speedBound; It should appear soon 
	final double length;
	
	public Road(Point from, Point to) {
		this.from = from;
		this.to = to;
		this.length = GeographyManager.getDistance(from, to);
	}
	
	public Point getFrom() {
		return from;
	}

	public Point getTo() {
		return to;
	}
	
	public double getLength() {
		return length;
	}

	@Override
	public String toString() {
		return from + " -> " + to;
	}
}
