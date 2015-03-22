package ru.ioffe.school.buses.structures;



public class Heap<E extends Comparable<E>> {
	E[] heap;
	int end;
	
	public Heap(E[] buffer) {
		heap = buffer;
	}

	public void add(E value) {
		heap[end++] = value;
		int next;
		for (int current = end - 1; current != 0;) {
			next = (current - 1) >> 1;
			if (heap[next].compareTo(heap[current]) > 0)
				swap(next, current);
			else 
				break;
			current = next;
		}
	}

	public E poll() {
		if (end == 0)
			return null;
		E res = heap[0];
		heap[0] = heap[end - 1];
		int current = 0;
		for (int next = (current << 1) + 1; next < end;) {
			if (next + 1 != end && heap[next].compareTo(heap[next + 1]) > 0) 
				next++;
			if (heap[next].compareTo(heap[current]) < 0) {
				swap(next, current);
				current = next;
			} else {
				break;
			}
			next = (current << 1) + 1;
		}
		heap[--end] = null;
		return res;
	}

	private void swap(int i, int j) {
		E help = heap[i];
		heap[i] = heap[j];
		heap[j] = help;
	}
	
	public boolean isEmpty() {
		return end == 0;
	}
	
//	public static void main(String[] args) {
//		Heap<Integer> t = new Heap<>(new Integer[100000]);
//		long time = System.currentTimeMillis();
////		TreeSet<Integer> set = new TreeSet<>();
//		Random rand = new Random();
//		for (int i = 0; i < 100000; i++) {
//			int ty = rand.nextInt(8);
//			if (ty < 5) {
//				int in = rand.nextInt();
//				t.add(in);
////				set.add(in);
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
////				Integer b = set.pollFirst();
////				if (!(a == b || a.compareTo(b) == 0))
////					System.out.println("Error");
//			}
//		}
//		System.out.println(System.currentTimeMillis() - time);
//	}
}
