package teachMeToSeparateClassesOnPackeges;


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
