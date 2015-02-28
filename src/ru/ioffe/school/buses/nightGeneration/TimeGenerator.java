package ru.ioffe.school.buses.nightGeneration;
import java.util.Random;


public class TimeGenerator {
	double min;
	double randBound;
	double scaleLog;
	Random rand;
	
	public TimeGenerator(int bound, double scale) {
		this.min = Math.pow(scale, -bound);
		this.randBound = 1 - min;
		this.scaleLog = Math.log(scale);
		this.rand = new Random();
	}
	
	public TimeGenerator(int bound, double scale, long seed) {
		this.min = Math.pow(scale, -bound);
		this.randBound = 1 - min;
		this.scaleLog = Math.log(scale);
		this.rand = new Random(seed);
	}
	
	private double random() {
		return rand.nextDouble() * randBound + min;
	}
	
	public double getRandomTime() {
		return (-Math.log(random()) / scaleLog);
	}
}
