package ru.ioffe.school.buses.timeManaging;

import ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges.Point;


/**
 * These classes should be used if times of demands will be increased only.
 * Summary complexity of all demands is O(N), where N is number of Segments in way. 
 */

public interface PositionIndicator {
	public Point getPosition();
	public void skipTime(double time);
}
