package ru.ioffe.school.buses.geographyManaging;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;

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
	
	public static boolean checkCrossing(Road a, Road b) {
		double prodABAC = vectorProduct(a.getFrom(), a.getTo(), a.getFrom(), b.getFrom());
		double prodABAD = vectorProduct(a.getFrom(), a.getTo(), a.getFrom(), b.getTo());
		double prodCDCA = vectorProduct(b.getFrom(), b.getTo(), b.getFrom(), a.getFrom());
		double prodCDCB = vectorProduct(b.getFrom(), b.getTo(), b.getFrom(), a.getTo());
		if (prodABAC == 0 && prodABAD == 0 && prodCDCA == 0 && prodCDCB == 0) { // lay on one Line
			return isBetween(a.getFrom(), a.getTo(), b.getFrom()) || isBetween(a.getFrom(), a.getTo(), b.getTo()) 
					|| isBetween(b.getFrom(), b.getTo(), a.getFrom()) || isBetween(b.getFrom(), b.getTo(), a.getTo());
		}
		return Math.signum(prodABAD) * Math.signum(prodABAC) == -1 && Math.signum(prodCDCB) * Math.signum(prodCDCA) == -1;  
	}
	
	// ab * cd
	private static double vectorProduct(Point a, Point b, Point c, Point d) {
		return (b.getX() - a.getX()) * (d.getY() - c.getY()) - (d.getX() - c.getX()) * (b.getY() - a.getY());
	}
	
	private static boolean isBetween(Point left, Point rigth, Point test) {
		if (left.getX() != rigth.getX())
			return isBetween(left.getX(), rigth.getX(), test.getX());
		return isBetween(left.getY(), rigth.getY(), test.getY());
	}
	
	private static boolean isBetween(double l, double r, double test) {
		return Math.min(l, r) <= test && test <= Math.max(l, r);
	}
}
