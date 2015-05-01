package ru.ioffe.school.buses.randomGeneration;


public class RandomObjectGenerator <E extends Generateable> {
	E[] objects;
	RandomIndexGenerator generator;
	
	public RandomObjectGenerator(E[] objects) {
		this.objects = objects;
		generator = new RandomIndexGenerator(objects);
	}
	
	public RandomObjectGenerator(E[] objects, long seed) {
		this.objects = objects;
		generator = new RandomIndexGenerator(objects, seed);
	}
	
	public E getRandomObject() {
		return objects[generator.getRandomIndex()];
	}
}
