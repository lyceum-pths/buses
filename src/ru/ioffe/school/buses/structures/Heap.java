package ru.ioffe.school.buses.structures;

import java.util.ArrayList;

public class Heap<E extends Comparable<E>> {
	ArrayList<E> heap;
	
	public Heap() {
		heap = new ArrayList<>();
	}

	public void add(E value) {
		heap.add(value);
		int next;
		for (int current = heap.size() - 1; current != 0;) {
			next = (current - 1) >> 1;
			if (heap.get(next).compareTo(heap.get(current)) > 0)
				swap(next, current);
			else 
				break;
			current = next;
		}
	}

	public E poll() {
		if (heap.size() == 0)
			return null;
		if (heap.size() == 1)
			return heap.remove(0);
		E res = heap.get(0);
		heap.set(0, heap.remove(heap.size() - 1));
		int current = 0;
		for (int next = (current << 1) + 1; next < heap.size();) {
			if (next + 1 != heap.size() && heap.get(next).compareTo(heap.get(next + 1)) > 0) 
				next++;
			if (heap.get(next).compareTo(heap.get(current)) < 0) {
				swap(next, current);
				current = next;
			} else {
				break;
			}
			next = (current << 1) + 1;
		}
		return res;
	}

	private void swap(int i, int j) {
		E help = heap.get(i);
		heap.set(i, heap.get(j));
		heap.set(j, help);
	}
	
	public boolean isEmpty() {
		return heap.size() == 0;
	}
	
//	public static void main(String[] args) {
//		Heap<Integer> t = new Heap<>();
////		long time = System.currentTimeMillis();
//		TreeSet<Integer> set = new TreeSet<>();
//		Random rand = new Random();
//		for (int i = 0; i < 1000000; i++) {
//			int ty = rand.nextInt(8);
////			System.out.println(i);
//			if (ty < 3) {
//				int in = rand.nextInt();
//				t.add(in);
//				set.add(in);
////			} else if (ty < 6) {
////				int in = rand.nextInt();
////				t.remove(in);
////				set.remove(in);
////			} else if (ty < 7) {
////				Integer a = t.pollFirst();
////				Integer b = set.pollFirst();
////				if (!(a == b || a.compareTo(b) == 0))
////					System.out.println("Error");
//			} else {
//				Integer a = t.poll();
//				Integer b = set.pollFirst();
//				if (!(a == b || a.compareTo(b) == 0))
//					System.out.println("Error");
//			}
//		}
////		System.out.println(System.currentTimeMillis() - time);
//	}
}
