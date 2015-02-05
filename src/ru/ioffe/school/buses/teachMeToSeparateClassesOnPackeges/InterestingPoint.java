package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;
import nigthGeneration.Generateable;


public class InterestingPoint extends Point implements Generateable {
	int popularity;

	public InterestingPoint(double x, double y, int popularity) {
		super(x, y);
		this.popularity = popularity;
	}
	
	public InterestingPoint(Point p, int popularity) {
		super(p.getX(), p.getY());
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
