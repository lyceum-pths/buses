package teachMeToSeparateClassesOnPackeges;

/**
 * This class mean, that object go straight from point "start" to point "end".
 * It should be used in GUI for showing object's movements and, maybe, for calculating fitness-function.
 */

public class Segment {
	Point start;
	Point end;
	int timeStart;
	int timeEnd;
	
	public Segment(Point start, Point end, int timeStart, int timeEnd) {
		this.start = start;
		this.end = end;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	public int getTimeStart() {
		return timeStart;
	}

	public int getTimeEnd() {
		return timeEnd;
	}
	
	@Override
	public String toString() {
		return "from = " + start + "; to = " + end + "; beginTime = " + timeStart + "; endTime = " + timeEnd;
	}
}
