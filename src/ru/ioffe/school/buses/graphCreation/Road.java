package ru.ioffe.school.buses.graphCreation;

import java.io.Serializable;
import java.util.Collection;

import ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges.*;

/**
 * This class contains information about road from point "from" to point "to".
 * The information is presented as a list of points crossed by the road.
 * It is used for looking for ways in a town.
 */

public class Road implements Serializable {
	private static final long serialVersionUID = 2284501799222650859L;
	
	Point[] crossroads;
	
	public Road(Point... crossroads) {
		this.crossroads = crossroads;
	}
	
	public Road(Collection<Point> crossroads) {
		this.crossroads = new Point[crossroads.size()];
		int pos = 0;
		for (Point crossroad : crossroads) 
			this.crossroads[pos++] = crossroad;
	}
	
	public Point[] getCrossroads() {
		return crossroads;
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < crossroads.length; i++) 
			build.append((i == 0? "" : " -> ") + crossroads[i]);
		return build.toString();
	}
}
