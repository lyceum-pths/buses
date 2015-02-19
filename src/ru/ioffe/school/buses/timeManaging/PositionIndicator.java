package ru.ioffe.school.buses.timeManaging;

import ru.ioffe.school.buses.data.Point;


/**
 * These classes should be used if times of demands will be increased only.
 * Average complexity of demand is O(1). 
 */

public interface PositionIndicator {
	public Point getPosition(); // O(1)
	public double getCurrentTime();
	public void skipTime(double time); // O(N) (O(1) average)
	public void setTime(double time); // O(log(N))
}
