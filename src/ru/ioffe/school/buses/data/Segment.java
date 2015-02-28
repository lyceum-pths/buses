package ru.ioffe.school.buses.data;

public interface Segment {
	
	public Point getStart();

	public Point getEnd();

	public double getTimeStart();

	public double getTimeEnd();
	
	public Point getPosition(double time);
}
