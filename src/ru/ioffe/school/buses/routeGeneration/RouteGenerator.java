package ru.ioffe.school.buses.routeGeneration;

import java.util.ArrayList;
import java.util.Arrays;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.Segment;
import ru.ioffe.school.buses.data.Station;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.randomGeneration.RandomIndexGenerator;


public class RouteGenerator {
	private RandomIndexGenerator stationGenerator;
	private RoadManager roadManager;
	private Station[] stations;
	
	public RouteGenerator(RoadManager roadManager, Station[] stations) {
		this.roadManager = roadManager;
		this.stations = stations;
		int[] probability = new int[stations.length];
		for (int i = 0; i < stations.length; i++)
			probability[i] = stations[i].getProbability();
		this.stationGenerator = new RandomIndexGenerator(probability);
	}
	
	public synchronized Route generateRoute(int length, boolean isCicle, boolean reusing) {
		if (length <= 1) 
			throw new IllegalArgumentException("Length of route too small");
		if (!reusing && length > stationGenerator.size())
			throw new IllegalArgumentException("There are no so many stations");
		@SuppressWarnings("unused")
		ArrayList<Segment> segments = new ArrayList<>();
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
		System.out.println(Arrays.toString(stations));
		ArrayList<Segment> way = new ArrayList<>();
		double currentTime = 0, dt;
		Point from, to = this.stations[stations[0]].getPosition();
		for (int i = 0; i < stations.length - (isCicle? 0 : 1); i++) {
			from = to;
			to = this.stations[stations[(i + 1) % stations.length]].getPosition();
			for (Road road : roadManager.findWay(from, to)) {
				dt = road.getLength() / road.getSpeedBound();
				way.add(new Segment(road.getFrom(), road.getTo(), currentTime, currentTime + dt));
				currentTime += dt;
			}
		}
		return new Route(way.toArray(new Segment[way.size()]));
	}
}
