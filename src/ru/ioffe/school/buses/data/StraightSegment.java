package ru.ioffe.school.buses.data;

import java.io.Serializable;

import ru.ioffe.school.buses.timeManaging.PositionReport;


/**
 * This class mean, that object go straight from point "start" to point "end".
 * It should be used in GUI for showing object's movements and, maybe, for calculating fitness-function.
 */

public class StraightSegment implements Segment, Serializable {
	
	private static final long serialVersionUID = -2743436898136354624L;
	final Point start;
	final Point end;
	final double dx, dy;
	final double timeStart, timeEnd;

	public StraightSegment(Point start, Point end, double timeStart, double timeEnd) {
		if (timeEnd < timeStart)
			throw new IllegalArgumentException("Time of end of movement mustn't be lowwer than time of start: start = "
					+ timeStart + ", end = " + timeEnd);
		this.start = start;
		this.end = end;
		this.dx = end.getX() - start.getX();
		this.dy = end.getY() - start.getY();
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
	}
	
	public StraightSegment(Road road, double currentTime) {
		this(road.from, road.to, currentTime, currentTime + road.getLength() / road.getSpeedBound());
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
		// it help us if (timeStart = timeEnd) because else k will be NaN and so x = y = NaN
		if (timeStart == time)
			return start;
		double k = (time - timeStart) / (timeEnd - timeStart);
		return new Point(-1, start.getX() + dx * k, start.getY() + dy * k); // which id should this point have?
	}
	
	@Override
	public PositionReport getPositionReport(double time) {
		return new PositionReport(getPosition(time), null, -1);
	}

	@Override
	public String toString() {
		return "from = " + start + "; to = " + end + "; beginTime = " + timeStart + "; endTime = " + timeEnd;
	}
}
