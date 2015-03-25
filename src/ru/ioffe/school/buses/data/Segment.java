package ru.ioffe.school.buses.data;

import ru.ioffe.school.buses.timeManaging.PositionReport;

public interface Segment {
	
	public Point getStart();

	public Point getEnd();

	public double getTimeStart();

	public double getTimeEnd();
	
	public Point getPosition(double time);
	
	public PositionReport getPositionReport(double time);
}
