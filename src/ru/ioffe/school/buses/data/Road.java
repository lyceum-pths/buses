package ru.ioffe.school.buses.data;

import java.io.Serializable;

import ru.ioffe.school.buses.geographyManaging.GeographyManager;

/**
 * This class contains information about road from point "from" to point "to".
 * The information is presented as a list of points crossed by the road.
 * It is used for looking for ways in a town.
 */

public class Road implements Serializable {
	
	private static final long serialVersionUID = 2284501799222650859L;
	
	public final Point from, to;
	public final double speedBound;// It should appear soon 
	public final double length;
	
	public Road(Point from, Point to) {
		this.from = from;
		this.to = to;
		this.length = GeographyManager.getDistance(from, to);
		this.speedBound = 1;
	}
	
	public Point getFrom() {
		return from;
	}

	public Point getTo() {
		return to;
	}
	
	public double getSpeedBound() {
		return speedBound;
	}
	
	public double getLength() {
		return length;
	}

	@Override
	public String toString() {
		return from + " -> " + to;
	}
}
