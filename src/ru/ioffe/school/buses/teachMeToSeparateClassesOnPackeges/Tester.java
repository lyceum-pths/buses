package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;

import java.util.Arrays;
import java.util.Scanner;

import ru.ioffe.school.buses.graphCreation.*;

public class Tester {
	public static void main(String[] args) {
		//		Point p = new Point(1.1, 2);
		//		FileManager manager = new FileManager("");
		//		manager.save("point", p);
		//		Double.sum(a, b)
		//		Point d = (Point) manager.load("point.point");
		//		System.out.println(d);
//		Nigth nigth = new Nigth(new Person(new Point(0,0), new Point(10, 10), 0));
//		Point[] points = {new Point(2, 2), new Point(10, 10)};
//		Emulator em = new Emulator(points, new GeographyManager(), 1, new Transfer(1, new Point(2, 2), new Point(10, 10), 3, 0, 1, 3, 4));
//		System.out.println(em.startEmulation(nigth, 1));
		GeographyManager man = new GeographyManager();
		RoadManager manager = new RoadManager(new Road(new Point(1, 1), new Point(2, 2), man),
				new Road(new Point(2, 2), new Point(1, 1), man),
				new Road(new Point(4, 4), new Point(2, 4), man),
				new Road(new Point(3, 3), new Point(4, 4), man),
				new Road(new Point(5, 3), new Point(3, 3), man),
				new Road(new Point(4, 2), new Point(5, 3), man),
				new Road(new Point(5, 3), new Point(4, 2), man),
				new Road(new Point(2, 2), new Point(4, 2), man),
				new Road(new Point(2, 4), new Point(2, 2), man));
		Scanner in = new Scanner(System.in);
		while(in.hasNext()) {
			System.out.println(Arrays.toString(manager.findWay(new Point(in.nextDouble(), in.nextDouble()), new Point(in.nextDouble(), in.nextDouble()))));
		}
		in.close();
	}
}
