package ru.ioffe.school.buses.data;

import java.io.Serializable;

import ru.ioffe.school.buses.randomGeneration.Generateable;

public class InterestingPoint extends Point implements Generateable, Serializable {
	
	private static final long serialVersionUID = 9133810140827049601L;
	final int popularity;

	public InterestingPoint(double x, double y, int popularity) {
		super(0, x, y);
		this.popularity = popularity;
	}
	
	public InterestingPoint(Point p, int popularity) {
		super(0, p.getX(), p.getY());
		this.popularity = popularity;
	}
	
	public InterestingPoint(Point p) {
		this(p, 1);
	}
	
	public InterestingPoint(double x, double y) {
		this(x, y, 1);
	}

	public int getPopularity() {
		return popularity;
	}
	
	@Override
	public int getProbability() {
		return popularity;
	}
	

	@Override
	public String toString() {
		return "(INTERESTING POINT at " + super.toString() + "; popularity = " + popularity + ")";
	}
}
