package graphCreation;

import teachMeToSeparateClassesOnPackeges.GeographyManager;
import teachMeToSeparateClassesOnPackeges.Point;

/**
 * This class contain information about road from point "from" to point "to".
 * It is used for looking ways in town.
 */

public class Road {
	Point begin;
	Point end;
	GeographyManager geographyManager;
	
	double length;
	
	public Road(Point begin, Point end, GeographyManager geographyManager) {
		this.begin = begin;
		this.end = end;
		this.geographyManager = geographyManager;
		this.length = -1;
	}
	
	public Point getBegin() {
		return begin;
	}
	
	public Point getEnd() {
		return end;
	}
	
	public double getLength() {
		if (length == -1)
			length = geographyManager.getDistance(begin, end);
		return length;
	}
	
	@Override
	public String toString() {
		return begin  + " -> " + end;
	}
}
