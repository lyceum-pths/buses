package teachMeToSeparateClassesOnPackeges;
import java.util.Arrays;
import java.util.HashSet;

import nigthGeneration.RandomObjectGenerator;
import nigthGeneration.TimeGenerator;



public class Tester {
	public static void main(String[] args) {
		//		Point p = new Point(1.1, 2);
		//		FileManager manager = new FileManager("");
		//		manager.save("point", p);
		//		Double.sum(a, b)
		//		Point d = (Point) manager.load("point.point");
		//		System.out.println(d);
		House t = new House(13, 13, 0);
		House h = new House(12, 12, 1);
		House p = new House(14, 14, 3);
		House[] houses = {t, h, p};
		RandomObjectGenerator<House> gen = new RandomObjectGenerator<>(houses);
		TimeGenerator time = new TimeGenerator(100, 1.1);
		int[] counters = new int[101];
		for (int i = 0; i < 100000; i++) {
			counters[time.getRandomTime()]++;
		}
		System.out.println(Arrays.toString(counters));
		for (int i = 0; i < 3; i++)
			System.out.println(counters[i] / 100000.0);
		HashSet<Point> set = new HashSet<>();
		House h2 = new House(1, 1, 0);
		set.add(h2);
		System.out.println(set.contains(h2));
	}
}
