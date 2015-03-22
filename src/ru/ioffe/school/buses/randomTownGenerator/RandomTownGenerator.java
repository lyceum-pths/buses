package ru.ioffe.school.buses.randomTownGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import ru.ioffe.school.buses.data.House;
import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.geographyManaging.GeographyManager;

public class RandomTownGenerator {

	static Random random = new Random();
	/**
	 * Generate town which has "size" cross-roads, "houses" houses, "interestingPoints" interesting points. 
	 * Parameter "roads" is number of roads, you want. Real number of roads will be between "size" - 1 and "roads".   
	 */
	public static Town generateTown(int size, int houses, int interestingPoints, int roads, double boundX, double boundY) {
		if (size < 2 || houses < 0 || interestingPoints < 0 || roads < size - 1) 
			throw new IllegalArgumentException("Wrong input");
		Point[] points = new Point[size];
		for (int i = 0; i < size; i++)
			points[i] = new Point(0, nextDouble(boundX), nextDouble(boundY));
		HashSet<Road> buffer = new HashSet<>();
		minTree(buffer, points);
		roads -= points.length - 1;
		tryGenerateRoad(buffer, points, roads);
		Road[] roadsArray = new Road[buffer.size() << 1];
		int pos = 0;
		for (Road road : buffer) {
			roadsArray[pos++] = road;
			roadsArray[pos++] = road.invert();
		}
		House[] housesArray = new House[houses];
		int maxSize = Integer.MAX_VALUE / (houses << 1); // not allow overflow  
		for (int i = 0; i < houses; i++)
			housesArray[i] = new House(nextDouble(boundX), nextDouble(boundY), random.nextInt(maxSize));
		InterestingPoint[] interestingpointsArray = new InterestingPoint[interestingPoints];
		for (int i = 0; i < interestingPoints; i++)
			interestingpointsArray[i] = new InterestingPoint(nextDouble(boundX), nextDouble(boundY), random.nextInt(maxSize));
		return new Town(roadsArray, housesArray, interestingpointsArray);
	}

	private static double nextDouble(double bound) {
		return random.nextDouble() * bound;
	}

	private static void minTree(HashSet<Road> buffer, Point[] points) {
		// Prim's algorithm
		int n = points.length;
		double[] cost = new double[n];
		boolean[] vis = new boolean[n];
		Road[] way = new Road[n];
		for (int i = 1; i < n; i++)
			cost[i] = Integer.MAX_VALUE;
		int minIndex;
		Road road;
		for (int i = 0; i < n; i++) {
			minIndex = -1;
			for (int j = 0; j < n; j++)
				if ((minIndex == -1 || cost[minIndex] > cost[j]) && !vis[j]) 
					minIndex = j;
			if (way[minIndex] != null) {
				buffer.add(way[minIndex]);
			}
			vis[minIndex] = true;
			for (int j = 0; j < n; j++) {
				if (vis[j])
					continue;
				road = new Road(points[minIndex], points[j]);
				if (!check(buffer, road))
					continue;
				if (road.getLength() < cost[j]) {
					cost[j] = road.getLength();
					way[j] = road;
				}
			}
		}
	}

	private static void tryGenerateRoad(HashSet<Road> buffer, Point[] points, int bound) {
		Road road;
		//double all = points.length / 2.0 * (points.length - 1);
		for (int step = 1; step < points.length; step++) {
			for (int j = 0; j + step < points.length; j++) {
				if (bound <= 0)
					return;
				road = new Road(points[j], points[j + step]);
				if (buffer.contains(road))
					continue;
				if (!check(buffer, road))
					continue;
				buffer.add(road);
				bound--;
			}
		}
	}

	private static boolean check(Collection<Road> roads, Road next) {
		for (Road road : roads) 
			if (GeographyManager.checkCrossing(road, next))
				return false;
		return true;
	}
}
