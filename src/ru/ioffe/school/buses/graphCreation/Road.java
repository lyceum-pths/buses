package ru.ioffe.school.buses.graphCreation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ru.ioffe.school.buses.data.Point;

/**
 * This class contains information about road from point "from" to point "to".
 * The information is presented as a list of points crossed by the road.
 * It is used for looking for ways in a town.
 */

public class Road implements Serializable {
	
	private static final long serialVersionUID = 2284501799222650859L;
	
	ArrayList<Point> crossroads;
	
	public Road(Point... crossroads) {
		this.crossroads = new ArrayList<>(crossroads.length);
		for (Point p : crossroads)
			this.crossroads.add(p);
	}
	
	public Road(Collection<Point> crossroads) {
		this.crossroads = new ArrayList<>(crossroads);
	}
	
	public ArrayList<Point> getCrossroads() {
		return crossroads;
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < crossroads.size(); i++) 
			build.append((i == 0? "" : " -> ") + crossroads.get(i));
		return build.toString();
	}
}
