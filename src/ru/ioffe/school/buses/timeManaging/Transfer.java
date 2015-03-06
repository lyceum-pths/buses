package ru.ioffe.school.buses.timeManaging;

import java.util.Arrays;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;

/**
 * This class contain information about departures bus which has 
 * number "busNumber" from point "from" to point "to".
 * It is used for looking for best way for person to go home.
 */

public class Transfer {
	final double[] departure;
	final double continuance; 
	final Point to;
	final Point from;
	final Bus bus;
	
	public Transfer(Bus bus, Point from, Point to, double continuance, double... departure) {
		this.bus = bus;
		this.from = from;
		this.to = to;
		this.continuance = continuance;
		Arrays.sort(departure);
		this.departure = departure;
	}
	
	public double getNextDeparture(double current) {
		if (current > departure[departure.length - 1]) 
			return Double.POSITIVE_INFINITY; // infinity
		int L = -1, R = departure.length - 1, M; // departure[L] < current, departure[R] >= current
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (departure[M] < current) {
				L = M;
			} else {
				R = M;
			}
		}
		return departure[R];
	}

	public double[] getDeparture() {
		return departure;
	}

	public double getContinuance() {
		return continuance;
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
}
