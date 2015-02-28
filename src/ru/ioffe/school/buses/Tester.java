package ru.ioffe.school.buses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.randomTownGenerator.RandomTownGenerator;
import ru.ioffe.school.buses.randomTownGenerator.Town;


public class Tester {
	public static void main(String[] args) throws IOException {
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
//		Scanner in = new Scanner(System.in);
////		while(in.hasNext()) {
////			System.out.println(Arrays.toString(manager.findWay(new Point(in.nextDouble(), in.nextDouble()), new Point(in.nextDouble(), in.nextDouble())).getCrossroads()));
////		}
////		System.out.println(1.0 >> 1);
////		in.close();
////		new GUIWindow(new Road(new Point(0, 200, 200), new Point(0, 250, 400), new Point(0, 300, 250), new Point(0, 20, 20)));
//		Segment[] segments = {new Segment(new Point(1, 0, 0), new Point(1, 100, 100), 0, 10), new Segment(new Point(1, 100, 100), new Point(1, 100, 200), 10, 15)};
//		Route[] routes = {new Route(segments)};
//		double[][] times = {{0, 31, 100}};
////		PositionIndicator ind = route.getPositionIndicator(0);
//		TimeTable table = new TimeTable(routes, times);
//		PositionIndicator ind = table.getBusIndicator(0, -10);
//		while (in.hasNextDouble()) {
//			ind.skipTime(in.nextDouble());
//			System.out.println(ind.getCurrentTime() + " " + ind.getPosition());
//		}
//		StraightSegment[] segments = {new StraightSegment(new Point(1, 0, 0), new Point(1, 100, 100), 0, 10), new StraightSegment(new Point(1, 100, 100), new Point(1, 100, 200), 10, 15)};
//		Route route = new Route(segments);
//		Station[] sts = {new Station(new Point(0,0, 0)), new Station(new Point(-1, 100, 200))};
//		double[] time = {0, 15};
//		double[] begins = {0, 40, 80};
//		Bus bus = new Bus(route, sts, time, begins);
//		for (int i = 0; i < 10; i++) {
//			System.out.println(bus.getPosition(in.nextDouble()));
//		}
//		in.close();
		Town t = RandomTownGenerator.generateTown(50, 5, 5, 5, 70, 100, 50);
		// copy-paste 
		FileOutputStream fos = new FileOutputStream(new File("roads.txt"));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Road road : t.getRoads()) {
			oos.writeObject(road);
		}
		oos.flush();
		oos.close();
			
	}
}
