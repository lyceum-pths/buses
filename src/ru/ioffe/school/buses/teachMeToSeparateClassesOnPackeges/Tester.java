package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;

import java.util.Scanner;

import ru.ioffe.school.buses.timeManaging.PositionIndicator;


public class Tester {
	public static void main(String[] args) {
//		Point p = new Point(1.1, 2);
//		FileManager manager = new FileManager("");
//		manager.save("point", p);
//		Double.sum(a, b)
//		Point d = (Point) manager.load("point.point");
//		System.out.println(d);
//		Night nigth = new Night(new Person(new Point(0, 0,0), new Point(0, 10, 10), 0));
//		Point[] points = {new Point(0, 2, 2), new Point(0, 10, 10)};
//		Emulator em = new Emulator(points, new GeographyManager(), 1, new Transfer(1, new Point(0, 2, 2), new Point(0, 10, 10), 3, 0, 1, 3, 4));
//		System.out.println(em.startEmulation(nigth, 1));
//		GeographyManager man = new GeographyManager();
//		RoadManager manager = new RoadManager(new GeographyManager(), new Road(new Point(1, 1), new Point(2,3), new Point(3, 5)), new Road(new Point(3, 1), new Point(2, 3), new Point(4, -3)));
		Scanner in = new Scanner(System.in);
//		while(in.hasNext()) {
//			System.out.println(Arrays.toString(manager.findWay(new Point(in.nextDouble(), in.nextDouble()), new Point(in.nextDouble(), in.nextDouble())).getCrossroads()));
//		}
//		in.close();
//		new GUIWindow(new Road(new Point(0, 200, 200), new Point(0, 250, 400), new Point(0, 300, 250), new Point(0, 20, 20)));
		Segment[] segments = {new Segment(new Point(1, 0, 0), new Point(1, 100, 100), 12, 112), new Segment(new Point(1, 100, 100), new Point(1, 100, 200), 112, 162)};
		Route route = new Route(segments);
		PositionIndicator ind = route.getPositionIndicator(0);
		while (true) {
			ind.skipTime(in.nextDouble());
			System.out.println(ind.getPosition());
		}
	}
}
