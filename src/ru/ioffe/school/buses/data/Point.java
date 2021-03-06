package ru.ioffe.school.buses.data;

import java.io.Serializable;

/**
 * This class contains information about one point on the map.
 * Currently x and y are linked to latitude and longitude.
 * id will probably be used while parsing POIs and adding them to the map. 
 */

public class Point implements Serializable {
	
	private static final long serialVersionUID = 2554523237233997911L;
	final double x;
	final double y;
	final long id;

	public Point(long id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public long getID() {
		return id;
	}

	@Override
	public String toString() {
		return "(" + x + "; " + y + ")";
	}

	@Override
	public int hashCode() {
		return Double.hashCode(x) + 235349 * Double.hashCode(y);
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
