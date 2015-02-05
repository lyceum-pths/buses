package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;

public class Point {
	double x;
	double y;
	long id;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "(" + x + "; " + y + ")";
	}

	@Override
	public int hashCode() {
		return (int) (x + (566239 * y));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point)
			return equals((Point) obj);
		return false;
	}
	
	public boolean equals(Point point) {
		return x == point.getX() && y == point.getY();
	}
}
