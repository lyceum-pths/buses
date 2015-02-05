package ru.ioffe.school.buses.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.ioffe.school.buses.graphCreation.Road;
import ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges.Point;

/**
 * The class handles an XML file that describes a map.
 * MapParser can give information about Roads describes in the map
 * or only about Points in the map.
 * Results are written in .txt files in project root folder.
 */

public class MapParser {
	
	private static HashMap<Long, Point> pointsByIds;
	
	public MapParser() {
		pointsByIds = new HashMap<>();
	}
	
	public void getRoads(File file) throws IOException {
		String[] text = parseText(file);
		roadsToFile(new File("roads.txt"), parseRoads(text));
	}
	
	public void getPoints(File file) throws IOException {
		String[] text = parseText(file);
		pointsToFile(new File("nodes.txt"), parsePoints(text));
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
	
	private static ArrayList<Point> parsePoints(String[] text) {
		ArrayList<Point> points = new ArrayList<>();
		String nodeRegex = "<node.*";
		String idRegex = "<node id=\"\\d+\"";
		String lonRegex = "lon=\"\\d+\\.\\d+\"";
		String latRegex = "lat=\"\\d+\\.\\d+\"";
		Matcher idMatcher;
		Matcher lonMatcher;
		Matcher latMatcher;
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
					double lon = Double.parseDouble(lonStr);
					double lat = Double.parseDouble(latStr);
					Point currPoint = new Point(id, lon, lat);
					points.add(currPoint);
					pointsByIds.put(id, currPoint);
				} else {
					System.out.println("Error: one of the nodes doesn't have info about it's id, lat or lon");
				}
			}
		}
		
		return points;
	}
 	
	private static ArrayList<Road> parseRoads(String[] text) {
		parsePoints(text);
		ArrayList<Road> roads = new ArrayList<>();
		String wayRegex = "<way.*";
		String wayCloseRegex = "</way>";
		String highwayTagRegex = "<tag k=\"highway\".*";
		String highwayTypeRegex = "v=\"[a-z]+\"";
		String refRegex = "<nd ref=\"\\d+\"/>";
		String[] types = { "secondary", "tertiary", "primary", "residential" };
		HashSet<String> neededTypes = new HashSet<>();
		for (int i = 0; i < types.length; i++) {
			neededTypes.add(types[i]);
		}
		Matcher matcher;
		ArrayList<Point> crossroads; 
		for (int i = 0; i < text.length; i++) {
			if (text[i].matches(wayRegex)) {
				crossroads = new ArrayList<>();
				while (i < text.length - 1) {
					i++;
					if (text[i].matches(wayCloseRegex))
						break;
					if (text[i].matches(refRegex)) {
						String ref = text[i].substring(9, text[i].length() - 3);
						Long refId = Long.parseLong(ref);
						crossroads.add(pointsByIds.get(refId));
					} else if (text[i].matches(highwayTagRegex)) {
						matcher = Pattern.compile(highwayTypeRegex).matcher(text[i]);
						if (matcher.find()) {
							String type = text[i].substring(matcher.start() + 3, matcher.end() - 1);
							if (neededTypes.contains(type)) {
								Road road = new Road(crossroads);
								roads.add(road);
							}
						}
					}
				}
			}
		}
		
		return roads;
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

