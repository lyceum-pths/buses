package ru.ioffe.school.buses.data;

import java.io.Serializable;

public class Person implements Serializable {
	
	private static final long serialVersionUID = -2850356644438815411L;
	final Point from;
	final Point to;
	final double time;
	
	public Person(Point from, Point to, double time) {
		this.from = from;
		this.to = to;
		this.time = time;
	}
		
	public Point getFrom() {
		return from;
	}

	public Point getTo() {
		return to;
	}

	public double getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "(from : " + from.toString() + "; to : " + to.toString() + "; when : " + time + ")";
	}
}
