package ru.ioffe.school.buses.geographyManaging;

import ru.ioffe.school.buses.data.Point;

public class GeographyManager {
	public static double getDistance(Point a, Point b) {
		return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
	}
	
	public static Point convert(double latitude, double longitude) {
		return new Point(0, latitude, longitude); // geography is not my favorite subject at school
	}
}
