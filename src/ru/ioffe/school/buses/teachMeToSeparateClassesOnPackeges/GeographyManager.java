package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;


public class GeographyManager {
	
	public GeographyManager() {}
	
	public double getDistance(Point a, Point b) {
		return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
	}
	
	public Point convert(double latitude, double longitude) {
		return new Point(0, latitude, longitude); // geography is not my favorite subject at school
	}
}
