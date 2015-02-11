package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;

import java.util.ArrayList;

import ru.ioffe.school.buses.graphCreation.Road;

public class Bus {
	Route route;
	int number;
	double totalTime;
	
	public Bus(Road road, double speed, int number, GeographyManager manager) {
		ArrayList<Point> way = road.getCrossroads();
		double time = 0, dist;
		Segment[] segments = new Segment[way.size()];
		for (int i = 0; i < way.size(); i++) {
			dist = manager.getDistance(way.get(i), way.get((i + 1) % way.size()));
			segments[i] = new Segment(way.get(i), way.get((i + 1) % way.size()), time, time = time + dist / speed);
		}
		this.route = new Route(segments);
		this.totalTime = time;
		this.number = number;
	}
}
