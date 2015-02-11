package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;

/**
 * This class mean, that object go straight from point "start" to point "end".
 * It should be used in GUI for showing object's movements and, maybe, for calculating fitness-function.
 */

public class Segment {
	Point start;
	Point end;
	double dx, dy;
	double timeStart, timeEnd;
	
	public Segment(Point start, Point end, double timeStart, double timeEnd) {
		this.start = start;
		this.end = end;
		this.dx = end.getX() - start.getX();
		this.dy = end.getY() - start.getY();
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
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
		double k = (time - timeStart) / (timeEnd - timeStart);
		return new Point(-1, start.getX() + dx * k, start.getY() + dy * k); // which id should this point have?
	}
	
	@Override
	public String toString() {
		return "from = " + start + "; to = " + end + "; beginTime = " + timeStart + "; endTime = " + timeEnd;
	}
}
