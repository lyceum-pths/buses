package ru.ioffe.school.buses.data;

public class BusSegment implements Segment {
	final Point start;
	final Point end;
	final Bus bus;
	final double timeStart, timeEnd;
	
	public BusSegment(Bus bus, double timeStart, double timeEnd) {
		this.start = bus.getPosition(timeStart);
		this.end = bus.getPosition(timeEnd);
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
		this.bus = bus;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	public double getTimeStart() {
		return timeStart;
	}

	public double getTimeEnd() {
		return timeEnd;
	}
	
	public Point getPosition(double time) {
		if (time < timeStart || time > timeEnd)
			return null;
		return bus.getPosition(time);
	}
	
	@Override
	public String toString() {
		return "from = " + start + "; to = " + end + "; beginTime = " + timeStart + "; endTime = " + timeEnd + "; by bus";
	}
}
