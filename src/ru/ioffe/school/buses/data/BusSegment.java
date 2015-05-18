package ru.ioffe.school.buses.data;

import ru.ioffe.school.buses.timeManaging.PositionReport;


// WHERE ARE A LOT OF BUGS
public class BusSegment implements Segment {
	final Point start;
	final Point end;
	final Bus bus;
	final int voyageNumber; 
	final double timeStart, timeEnd;
	
	public BusSegment(Bus bus, double time, double timeStart, double timeEnd, Point from, Point to) {
		if (timeEnd < timeStart)
			throw new IllegalArgumentException("Time of end of movement mustn't be lowwer than time of start: start = "
					+ timeStart + ", end = " + timeEnd);
		this.start = from;
		this.end = to;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
		this.bus = bus;
		this.voyageNumber = bus.findNearestVoyage(timeStart - time);
	}

	@Override
	public Point getStart() {
		return start;
	}

	@Override
	public Point getEnd() {
		return end;
	}

	@Override
	public double getTimeStart() {
		return timeStart;
	}

	@Override
	public double getTimeEnd() {
		return timeEnd;
	}
	
	@Override
	public Point getPosition(double time) {
		if (time < timeStart || time > timeEnd)
			return null;
		return bus.getPosition(time, voyageNumber);
	}

	@Override
	public PositionReport getPositionReport(double time) {
		return new PositionReport(getPosition(time), bus, voyageNumber);
	}
	
	@Override
	public String toString() {
		return "from = " + start + "; to = " + end + "; beginTime = " + timeStart + "; endTime = " + timeEnd + "; by bus";
	}
}
