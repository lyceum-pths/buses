package ru.ioffe.school.buses.data;


// WHERE ARE A LOT OF BUGS
public class BusSegment implements Segment {
	final Point start;
	final Point end;
	final Bus bus;
	final double time, timeStart, timeEnd;
	
	public BusSegment(Bus bus, double time, double timeStart, double timeEnd, Point from, Point to) {
		if (timeEnd < timeStart)
			throw new IllegalArgumentException("Time of end of movement mustn't be lowwer than time of start: start = "
					+ timeStart + ", end = " + timeEnd);
		this.start = from;
		this.end = to;
		this.time = time;
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
		return bus.getRoute().getPosition(time - timeStart + this.time);
	}
	
	@Override
	public String toString() {
		return "from = " + start + "; to = " + end + "; beginTime = " + timeStart + "; endTime = " + timeEnd + "; by bus";
	}
}
