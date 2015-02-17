package ru.ioffe.school.buses.emulation;

import java.util.Arrays;

import ru.ioffe.school.buses.data.Route;

public class Report {
	Route[] routes;
	//here should be some important information
	int fitness;
	
	public Report(Route[] routes) {
		this.routes = routes;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(routes);
	}
}
