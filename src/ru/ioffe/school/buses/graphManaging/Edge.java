package ru.ioffe.school.buses.graphManaging;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;



public class Edge {
	final double time;
	final Road road;
	final int from, to;

	public Edge(Road road, int from, int to) {
		this.from = from;
		this.to = to;
		this.road = road;
		this.time = road.getLength() / road.speedBound;
	}
	
	public Edge(Road road, double speed, int from, int to) {
		this.from = from;
		this.to = to;
		this.road = road;
		this.time = road.getLength() / speed;
	}

	public double getTime() {
		return time;
	}
	
	public int getStart() {
		return from;
	}

	public int getEnd() {
		return to;
	}
	
	public Point getStartPoint() {
		return road.getFrom();
	}
	
	public Point getEndPoint() {
		return road.getTo();
	}
	
	public Road getRoad() {
		return road;
	}
}