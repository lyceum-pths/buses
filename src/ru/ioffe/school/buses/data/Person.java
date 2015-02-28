package ru.ioffe.school.buses.data;



public class Person {
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
