package ru.ioffe.school.buses.routeGeneration;

import java.util.ArrayList;
import java.util.Random;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.StraightSegment;
import ru.ioffe.school.buses.data.Station;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.randomGeneration.RandomIndexGenerator;


public class BusGenerator {
	private RandomIndexGenerator stationGenerator;
	private RoadManager roadManager;
	private Station[] stations;
	private Random random;
	
	public BusGenerator(RoadManager roadManager, Station[] stations) {
		this.roadManager = roadManager;
		this.stations = stations;
		this.random = new Random();
		int[] probability = new int[stations.length];
		for (int i = 0; i < stations.length; i++)
			probability[i] = stations[i].getProbability();
		this.stationGenerator = new RandomIndexGenerator(probability);
	}
	
	public synchronized Bus generateBus(int length, boolean isCicle, boolean reusing, double minTime, double maxTime) {
		if (length <= 1) 
			throw new IllegalArgumentException("Length of route too small");
		if (!reusing && length > stationGenerator.size())
			throw new IllegalArgumentException("There are no so many stations");
		int[] stations = new int[length];
		for (int i = 0; i < length; i++) {
			if (reusing) {
				if (i > 0) 
					stationGenerator.setAbility(stations[i - 1], false);
				if (i == length - 1)
					stationGenerator.setAbility(stations[0], false);
			}
			stations[i] = stationGenerator.getRandomIndex();
			if (reusing) {
				if (i > 0) 
					stationGenerator.setAbility(stations[i - 1], true);
				if (i == length - 1)
					stationGenerator.setAbility(stations[0], true);
			} else {
				stationGenerator.setAbility(stations[i], false);
			}
		}
		if (!reusing) 
			for (int i : stations)
				stationGenerator.setAbility(i, true);
		Station[] stationsOnWay = new Station[stations.length + (isCicle? 1 : 0)];
		for (int i = 0; i < stationsOnWay.length; i++)
			stationsOnWay[i] = this.stations[stations[i % stations.length]];
		ArrayList<StraightSegment> way = new ArrayList<>();
		Point from, to = this.stations[stations[0]].getPosition();
		double[] time = new double[stations.length + (isCicle? 1 : 0)];
		double currentTime = 0, dt;
		System.out.println("bus");
		for (int i = 0; i < stations.length - (isCicle? 0 : 1); i++) {
			from = to;
			to = this.stations[stations[(i + 1) % stations.length]].getPosition();
			System.out.println(from + " " + to);
			for (Road road : roadManager.findWay(from, to)) {
				dt = road.getLength() / road.getSpeedBound();
				way.add(new StraightSegment(road.getFrom(), road.getTo(), currentTime, currentTime + dt));
				currentTime += dt;
			}
			time[i + 1] = currentTime;
		}
		double lastEnd = minTime, randTime;
		ArrayList<Double> begins = new ArrayList<>();
		while (lastEnd < maxTime) {
			randTime = random.nextDouble() * (maxTime - lastEnd);
			begins.add(lastEnd + randTime);
			lastEnd += randTime + currentTime;
		}
		double[] arrayBegins = new double[begins.size()];
		for (int i = 0; i < arrayBegins.length; i++)
			arrayBegins[i] = begins.get(i);
		return new Bus(new Route(way.toArray(new StraightSegment[way.size()])), stationsOnWay, time, arrayBegins);
	}
}
