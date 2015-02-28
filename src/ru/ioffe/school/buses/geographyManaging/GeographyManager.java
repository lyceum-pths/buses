package ru.ioffe.school.buses.geographyManaging;

import ru.ioffe.school.buses.data.Point;

public class GeographyManager {
	public static double getDistance(Point a, Point b) {
		return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
	}
	
	public static double getSquaredDistance(Point a, Point b) {
		return sqr(a.getX() - b.getX()) + sqr(a.getY() - b.getY());
	}
	
	private static double sqr(double d) {
		return d * d; 
	}
	
	public static Point convert(double latitude, double longitude) {
		return new Point(0, latitude, longitude); // geography is not my favorite subject at school
	}
}
