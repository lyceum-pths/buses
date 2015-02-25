package ru.ioffe.school.buses.gui;

import ru.ioffe.school.buses.data.Point;

public class TestBus {
	final int maxTime = 43200;
	double k;
	double b;
	double minX;
	double minY;
	double maxX;
	double maxY;
	
	public TestBus(double k, double b, double minX, double minY, double maxX, double maxY) {
		this.k = k;
		this.b = b;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public Point getCoord(int time) {
		double x = (maxX - minX) * time / maxTime;
		double y = minY + k * x + b;
		return new Point(0, minX + x, y);
	}
}
