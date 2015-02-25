package ru.ioffe.school.buses.randomGeneration;

import java.util.Random;

public class RandomIndexGenerator {
	int[] probability;
	Tree tree;
	Random rand;
	
	public RandomIndexGenerator(int[] probability) {
		this.probability = probability;
		rand = new Random();
		tree = new Tree(probability);
	}
	
	public RandomIndexGenerator(Generateable[] objects) {
		this.probability = new int[objects.length];
		for (int i = 0; i < objects.length; i++)
			probability[i] = objects[i].getProbability();
		rand = new Random();
		tree = new Tree(probability);
	}
	
	public int getRandomIndex() {
		if (probability.length == 0)
			throw new IllegalArgumentException("Cant generate index because there are no objects");
		int randomNumber = rand.nextInt(tree.sum());
		return tree.getIndex(randomNumber);
	}
	
	public void setAbility(int index, boolean flag) {
		if (index < 0 || index >= probability.length)
			throw new IndexOutOfBoundsException();
		tree.set(index, flag? probability[index] : 0);
	}
	
	public int size() {
		return probability.length;
	}
	
	public static class Tree {
		private int[] sum;
		private int size;
		
		public Tree(int[] array) {
			size = array.length;
			sum = new int[size << 2];
			init(0, 0, size, array);
		}
		
		private void init(int n, int L, int R, int[] array) {
			if (R - L == 1) {
				sum[n] = array[L];
				return;
			}
			int M = (R + L) >> 1;
			init((n << 1) + 1, L, M, array);
			init((n << 1) + 2, M, R, array);
			update(n);
		}
		
		public int sum() {
			if (sum.length == 0) {
				return 0;
			}
			return sum[0];
		}
		
		public void set(int index, int value) {
			set(0, 0, size, index, value);
		}
		
		public int getIndex(int number) {
			if (number < 0 || number > sum()) {
				return -1;
			}
			return getIndex(0, 0, size, number);
		}
		
		private int getIndex(int n, int L, int R, int number) {
			if (R - L == 1) {
				return L;
			}
			int prefSum = sum[(n << 1) + 1];
			if (prefSum > number) {
				return getIndex((n << 1) + 1, L, (R + L) >> 1, number);
			} else {
				return getIndex((n << 1) + 2, (R + L) >> 1, R, number - prefSum);
			}
		}
		
		private void set(int n, int L, int R, int index, int value) {
			if (R - L == 1) {
				sum[n] = value;
				return;
			}
			int M = (R + L) >> 1;
			if (index < M) {
				set((n << 1) + 1, L, M, index, value);
			} else {
				set((n << 1) + 2, M, R, index, value);
			}
			update(n);
		}
		
		private void update(int n) {
			sum[n] = sum[(n << 1) + 1] + sum[(n << 1) + 2];
		}
	}
}
