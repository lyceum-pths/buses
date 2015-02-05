package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;

import ru.ioffe.school.buses.nightGeneration.Generateable;

public class House extends Point implements Generateable {
	int size;
	
	public House(double x, double y, int size) {
		super(x, y);
		this.size = size;
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
