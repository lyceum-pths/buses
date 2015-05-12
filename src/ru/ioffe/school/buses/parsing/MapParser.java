package ru.ioffe.school.buses.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.graphManaging.GraphBuilder;

/**
 * The class handles an XML file that describes a map.
 * MapParser can give information about Roads described in the map
 * or only about Points in the map.
 * Results are written in .txt files in project root folder.
 */

public class MapParser {
	
	private static HashMap<Long, Point> pointsByIds = new HashMap<Long, Point>();
	private final static double k = 0.7, r = 6800;
	
	public static void getRoads(File file, boolean oneComponent) throws IOException {
		String[] text = parseText(file);
		String outFileName = "data/generated/roads.data";
		ArrayList<Road> ans = parseRoads(text);
		if (oneComponent) {
			GraphBuilder build = new GraphBuilder(ans).findMaxComponent();
			ans.clear();
			for (Road r : build.getRoads()) {
				ans.add(r);
			}
		}
		roadsToFile(new File(outFileName), ans);
	}
	
	public static void getPoints(File file) throws IOException {
		String[] text = parseText(file);
		String outFileName = "data/generated/points.data";
		pointsToFile(new File(outFileName), parsePoints(text));
	}
	
	private static String[] parseText(File file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		ArrayList<String> arr = new ArrayList<>();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			arr.add(inputLine.trim());
		}
		in.close();
		String[] ans = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			ans[i] = arr.get(i);
		}
		return ans;
	}
	
	private static ArrayList<Point> parsePoints(String[] text) throws FileNotFoundException {
		ArrayList<Point> points = new ArrayList<>();
		ArrayList<InterestingPoint> interesting = new ArrayList<>();
		String nodeRegex = "<node.*";
		String idRegex = "<node id=\"\\d+\"";
		String lonRegex = "lon=\"\\d+\\.\\d+\"";
		String latRegex = "lat=\"\\d+\\.\\d+\"";
		String pubRegex = "<tag k=\"amenity\" v=\"pub\"/>";
		String nodeEnd = "</node>";
		Matcher idMatcher;
		Matcher lonMatcher;
		Matcher latMatcher;
		PrintWriter out = new PrintWriter(new File("log.txt"));
		for (int i = 0; i < text.length; i++) {
			if (text[i].matches(nodeRegex)) {
				idMatcher = Pattern.compile(idRegex).matcher(text[i]);
				lonMatcher = Pattern.compile(lonRegex).matcher(text[i]);
				latMatcher = Pattern.compile(latRegex).matcher(text[i]);
				if (idMatcher.find() && lonMatcher.find() && latMatcher.find()) {
					String idStr = text[i].substring(idMatcher.start(), idMatcher.end());
					String lonStr = text[i].substring(lonMatcher.start(), lonMatcher.end());
					String latStr = text[i].substring(latMatcher.start(), latMatcher.end());
					idStr = idStr.substring(10, idStr.length() - 1);
					long id = Long.parseLong(idStr);
					lonStr = lonStr.substring(5, lonStr.length() - 1);
					latStr = latStr.substring(5, latStr.length() - 1);
					double lon = Math.toRadians(k * Double.parseDouble(lonStr)) * r * 3600;
					double lat = Math.toRadians(Double.parseDouble(latStr)) * r * 3600;
					Point currPoint = new Point(id, lon, lat);
					points.add(currPoint);
					out.println(id);
					pointsByIds.put(id, currPoint);
					if (!text[i].endsWith("/>")) { //there are tags
						while (!text[i].equals(nodeEnd)) {
							if (text[i].matches(pubRegex))
								interesting.add(new InterestingPoint(currPoint));
							i++;
						}
					}
				} else {
					System.out.println("Error: one of the nodes doesn't have info about it's id, lat or lon; line " + (i + 1));
					System.out.println(text[i]);
				}
			}
		}
		out.close();
		try {
			interestingPointsToFile(new File("int.data"), interesting);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return points;
	}
 	
	private static ArrayList<Road> parseRoads(String[] text) throws IOException {
		parsePoints(text);
		ArrayList<Road> roads = new ArrayList<>();
		String wayRegex = "<way.*";
		String wayCloseRegex = "</way>";
		String highwayTagRegex = "<tag k=\"highway\".*";
		String highwayTypeRegex = "v=\"[a-z]+\"";
		String refRegex = "<nd ref=\"\\d+\"/>";
		String onewayRegex = "<tag k=\"oneway\" v=\"yes\"/>";
		String[] types = { "secondary", "tertiary", "primary", "motorway" };
//		String[] types = { "residential" };
		HashSet<String> neededTypes = new HashSet<>();
		for (int i = 0; i < types.length; i++) {
			neededTypes.add(types[i]);
		}
		Matcher matcher;
		long last;
		ArrayList<Road> currentRoads;
		for (int i = 0; i < text.length; i++) {
			if (text[i].matches(wayRegex)) {
				last = -1;
				currentRoads = new ArrayList<>();
				boolean oneway = false;
				ArrayList<String> currentTypes = new ArrayList<>();
				while (i < text.length - 1) {
					i++;
					if (text[i].matches(wayCloseRegex))
						break;
					if (text[i].matches(refRegex)) {
						String ref = text[i].substring(9, text[i].length() - 3);
						Long refId = Long.parseLong(ref);
						if (last == -1) {
							last = refId;
						} else {
							currentRoads.add(new Road(pointsByIds.get(last), pointsByIds.get(refId)));
							if (last == 0 && refId == 0)
								System.out.println("lol");
							last = refId;
						}
					} else if (text[i].matches(highwayTagRegex)) {
						matcher = Pattern.compile(highwayTypeRegex).matcher(text[i]);
						if (matcher.find()) {
							String type = text[i].substring(matcher.start() + 3, matcher.end() - 1);
							if (!currentTypes.contains(type)) {
								currentTypes.add(type);
							}
						}
					} else if (text[i].matches(onewayRegex)) {
						oneway = true;
					}
				}
				if (neededTypes.containsAll(currentTypes) && currentTypes.size() > 0) {
					for (Road road : currentRoads) {
						road.setOneway(oneway);
					}
					roads.addAll(currentRoads);
				}
			}
		}
		return roads;
	}
	
	private static void interestingPointsToFile(File file, ArrayList<InterestingPoint> points) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Point p : points) {
			oos.writeObject(p);			
		}
		oos.flush();
		oos.close();
	}
	
	private static void pointsToFile(File file, ArrayList<Point> points) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Point p : points) {
			oos.writeObject(p);			
		}
		oos.flush();
		oos.close();
	}
	
	private static void roadsToFile(File file, ArrayList<Road> roads) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Road road : roads) {
			oos.writeObject(road);
		}
		oos.flush();
		oos.close();
	}
	
}

