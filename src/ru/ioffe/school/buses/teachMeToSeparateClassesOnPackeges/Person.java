package teachMeToSeparateClassesOnPackeges;


public class Person {
	Point from;
	Point to;
	int time;
	
	public Person(Point from, Point to, int time) {
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

	public int getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "(from : " + from.toString() + "; to : " + to.toString() + "; when : " + time + ")";
	}
}
