package ru.ioffe.school.buses.timeManaging;

import java.io.Serializable;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;

/**
 * This class contain information about departures bus which has 
 * number "busNumber" from point "from" to point "to".
 * It is used for looking for best way for person to go home.
 */

public class Transfer implements Serializable {

	private static final long serialVersionUID = 6322276115251647118L;
	final double continuance; 
	final double time; // time between bus started moving and it came to point from
	final Point to;
	final Point from;
	final Bus bus;
	
	public Transfer(Bus bus, Point from, Point to, double continuance, double time) {
		this.bus = bus;
		this.time = time;
		this.from = from;
		this.to = to;
		this.continuance = continuance;
	}
	
	public double getNextDeparture(double current) {
		return time + bus.nextDeparture(current - time);
	}

	public double getContinuance() {
		return continuance;
	}
	
	public double getTime() {
		return time;
	}
	
	public Point getFrom() {
		return from;
	}

	public Point getTo() {
		return to;
	}

	public Bus getBus() {
		return bus;
	}
	
	@Override
	public String toString() {
		return "(from = " + from + "; to = " + to + "; time = " + time + "; continuance = " + continuance + ")";
	}
}
