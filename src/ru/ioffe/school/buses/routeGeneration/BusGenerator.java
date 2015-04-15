package ru.ioffe.school.buses.routeGeneration;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.StraightSegment;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.nightGeneration.TimeGenerator;


public class BusGenerator {
	private RoadManager roadManager;
	private Point[] nodes;
	private Random random;
	private TimeGenerator timeGenerator;

	public BusGenerator(RoadManager roadManager) {
		this.roadManager = roadManager;
		this.nodes = roadManager.getCrossroads();
		this.random = new Random();
	}

	public BusGenerator(RoadManager roadManager, TimeGenerator generator) {
		this.roadManager = roadManager;
		this.nodes = roadManager.getCrossroads();
		this.random = new Random();
		this.timeGenerator = generator;
	}

	public Bus generateBus(double minTime, double maxTime, int buses, double minDistanceBetweenEnds) {
		minDistanceBetweenEnds *= minDistanceBetweenEnds;
		Point from;
		Point to;
		do {
			from = nodes[random.nextInt(nodes.length)];
			to = nodes[random.nextInt(nodes.length)];
		} while (GeographyManager.getSquaredDistance(from, to) < minDistanceBetweenEnds); 
		return generateBus(minTime, maxTime, buses, from, to);
	}
	
	public Bus generateBus(double minTime, double maxTime, int buses) {
		return generateBus(minTime, maxTime, buses, 0);
	}

	public Bus generateBus(double minTime, double maxTime, int buses, Point... controlPoints) {
		if (buses < 1) 
			throw new IllegalArgumentException("We need more buses: " + buses);
		if (maxTime <= minTime)
			throw new IllegalArgumentException("There is no time to begin!");
		if (controlPoints.length < 2)
			throw new IllegalArgumentException("There is not enough points to generate route: " + controlPoints.length);
		ArrayList<StraightSegment> way = new ArrayList<>();
		double currentTime = 0;
		for (int i = 0; i < controlPoints.length; i++) {
			for (Road road : roadManager.findWay(controlPoints[i], controlPoints[(i + 1) % controlPoints.length])) {
				way.add(new StraightSegment(road, currentTime));
				currentTime += road.getLength() / road.getSpeedBound();
			}
		}
		Route route = new Route(way.toArray(new StraightSegment[way.size()]));
		TreeSet<Double> begins = new TreeSet<>();
		// generating begins
		if (timeGenerator == null) {
			double dT = route.getTotalTime() / buses;
			for (int i = 0; i < buses; i++)
				begins.add(i * dT);
		} else {
			double buffer;
			while (begins.size() < buses) {
				buffer = timeGenerator.getRandomTime();
				if (minTime <= buffer && buffer < maxTime)
					begins.add(buffer);
			}
		}
		double[] times = new double[begins.size()];
		int index = 0;
		for (Double d : begins)
			times[index++] = d;
		return new Bus(route, times);
	}
}
