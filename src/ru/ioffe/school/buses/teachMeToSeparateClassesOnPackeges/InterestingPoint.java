package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;

import ru.ioffe.school.buses.nightGeneration.Generateable;

public class InterestingPoint extends Point implements Generateable {
	
	private static final long serialVersionUID = 9133810140827049601L;
	int popularity;

	public InterestingPoint(double x, double y, int popularity) {
		super(0, x, y);
		this.popularity = popularity;
	}
	
	public InterestingPoint(Point p, int popularity) {
		super(0, p.getX(), p.getY());
		this.popularity = popularity;
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
