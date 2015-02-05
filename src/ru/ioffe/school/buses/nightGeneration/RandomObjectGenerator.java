package ru.ioffe.school.buses.nightGeneration;
import java.util.Random;


public class RandomObjectGenerator <E extends Generateable> {
	E[] objects;
	int[] prefSum; // long?
	Random rand;
	
	public RandomObjectGenerator(E[] objects) {
		this.objects = objects;
		rand = new Random();
		prefSum = new int[objects.length];
		for (int i = 0; i < prefSum.length; i++) 
			prefSum[i] = (i == 0? 0 : prefSum[i - 1]) + objects[i].getProbability(); 
	}
	
	public RandomObjectGenerator(E[] objects, long seed) {
		this.objects = objects;
		rand = new Random(seed);
		prefSum = new int[objects.length];
		for (int i = 0; i < prefSum.length; i++) 
			prefSum[i] = (i == 0? 0 : prefSum[i - 1]) + objects[i].getProbability(); 
	}
	
	public E getRandomObject() {
		if (objects.length == 0)
			throw new IllegalArgumentException("Cant generate object because there are no objects");
		int randomNumber = rand.nextInt(prefSum[prefSum.length - 1]) + 1;
		int L = -1, R = objects.length - 1, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (prefSum[M] < randomNumber)
				L = M;
			else
				R = M;
		}
		return objects[R];
	}
}
