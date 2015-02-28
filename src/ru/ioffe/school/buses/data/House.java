package ru.ioffe.school.buses.data;

import ru.ioffe.school.buses.randomGeneration.Generateable;

public class House extends Point implements Generateable {
	
	private static final long serialVersionUID = 866189470358886686L;
	final int size;
	
	public House(double x, double y, int size) {
		super(0, x, y);
		this.size = size;
	}
	
	public House(double x, double y) {
		this(x, y, 1);
	}

	public int getSize() {
		return size;
	}
	
	@Override
	public int getProbability() {
		return size;
	}
	
	@Override
	public String toString() {
		return "(HOUSE at " + super.toString() + "; size = " + size + ")";
	}
}
