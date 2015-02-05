package ru.ioffe.school.buses.graphCreation;

import java.util.Collection;

import ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges.*;

/**
 * This class contain information about road from point "from" to point "to".
 * It is used for looking ways in town.
 */

public class Road {
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
}
