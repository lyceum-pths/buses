package ru.ioffe.school.buses.data;

import ru.ioffe.school.buses.timeManaging.PositionReport;

public class WaitingSegment implements Segment {
	final Point p;
	final double timeStart, timeEnd;

	public WaitingSegment(Point p, double timeStart, double timeEnd) {
		if (timeEnd < timeStart)
			throw new IllegalArgumentException("Time of end of movement mustn't be lowwer than time of start: start = "
					+ timeStart + ", end = " + timeEnd);
		this.p = p;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
	}

	@Override
	public Point getStart() {
		return p;
	}

	@Override
	public Point getEnd() {
		return p;
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
		if (time > timeEnd || time < timeStart)
			return null;
		return p;
	}

	@Override
	public PositionReport getPositionReport(double time) {
		return new PositionReport(p, null, -1);
	}
}
