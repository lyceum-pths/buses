package ru.ioffe.school.buses.emulation;

import java.util.Arrays;

import ru.ioffe.school.buses.data.Route;

public class Report {
	final Route[] routes;
	//here should be some important information
	final double fitness;
	
	public Report(Route[] routes) {
		this.routes = routes;
		double average = 0;
		for (Route route : routes) 
			average += route.getTotalTime();
		average /= routes.length;
		this.fitness = average;
	}
	
	public Route[] getRoutes() {
		return routes;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(routes);
	}
}
