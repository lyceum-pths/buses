package ru.ioffe.school.buses.data;

import java.io.Serializable;

import ru.ioffe.school.buses.geographyManaging.GeographyManager;

/**
 * This class contains information about road from point "from" to point "to".
 * The information is presented as two points: the start and the end of the road.
 * It is used for looking for ways in a town.
 */

public class Road implements Serializable {
	
	private static final long serialVersionUID = 2284501799222650859L;
	
	public final Point from, to;
	public final double speedBound;
	public final double length;
	public boolean isOneway;
	
	public Road(Point from, Point to) {
		this.from = from;
		this.to = to;
		this.length = GeographyManager.getDistance(from, to);
		this.speedBound = 60;
	}
	
	public Road(Point from, Point to, boolean isOneway) {
		this.from = from;
		this.to = to;
		this.length = GeographyManager.getDistance(from, to);
		this.speedBound = 60;
		this.isOneway = isOneway;
	}
	
	private Road(Point from, Point to, double length) {
		this.from = from;
		this.to = to;
		this.length = length;
		this.speedBound = 60;
	}
	
	/**
	 * return Road.from
	 * @since alpha alpha alpha
	 */
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
	
	public void setOneway(boolean isOneway) {
		this.isOneway = isOneway;
	}
	
	public Road invert() {
		return new Road(to, from, length);
	}

	@Override
	public String toString() {
		return from + " -> " + to;
	}
}
