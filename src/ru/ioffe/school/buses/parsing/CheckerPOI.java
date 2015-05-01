package ru.ioffe.school.buses.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.graphManaging.GraphBuilder;

public class CheckerPOI {
	
	public static void validatePOI(File roads, File poi) throws IOException {
		GraphBuilder builder = new GraphBuilder(getRoads(roads)); 
		HashSet<InterestingPoint> result = new HashSet<>();
		for (Point point : getPoints(poi))
			result.add(new InterestingPoint(builder.findNearestPoint(point)));
		savePoints(new File("data/generated/poi.data"), result);
	}
	
	static ArrayList<Road> getRoads(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		ArrayList<Road> roads = new ArrayList<>();
		try {
			while (true) {
				roads.add((Road) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
		fis.close();
		return roads;
	}
	
	static ArrayList<Point> getPoints(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		ArrayList<Point> points = new ArrayList<>();
		try {
			while (true) {
				points.add((Point) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
		fis.close();
		return points;
	}
	
	static void savePoints(File file, Collection<InterestingPoint> points) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Point p : points) {
			oos.writeObject(p);			
		}
		oos.flush();
		oos.close();
	}
}
