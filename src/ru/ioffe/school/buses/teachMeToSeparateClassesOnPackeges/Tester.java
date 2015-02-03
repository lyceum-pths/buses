package teachMeToSeparateClassesOnPackeges;



public class Tester {
	public static void main(String[] args) {
		//		Point p = new Point(1.1, 2);
		//		FileManager manager = new FileManager("");
		//		manager.save("point", p);
		//		Double.sum(a, b)
		//		Point d = (Point) manager.load("point.point");
		//		System.out.println(d);
		Nigth nigth = new Nigth(new Person(new Point(0,0), new Point(10, 10), 0));
		Point[] points = {new Point(2, 2), new Point(10, 10)};
		Emulator em = new Emulator(points, new GeographyManager(), 1, new Transfer(1, new Point(2, 2), new Point(10, 10), 3, 0, 1, 2, 3, 4));
		System.out.println(em.startEmulation(nigth, 1));
	}
}
