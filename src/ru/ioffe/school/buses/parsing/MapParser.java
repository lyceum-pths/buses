package ru.ioffe.school.buses.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapParser {
	
	static HashSet<Long> usedId;
	static HashSet<Long> idsInRoads;
	
	static boolean isCorrectRoad(Road r) {
		for (long id : r.path) {
			if (!usedId.contains(id))
				return false;
		}
		return true;
	}
	
	static String[] getTextFromFile(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
//		StringBuilder builder = new StringBuilder();
		ArrayList<String> arr = new ArrayList<>();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
//			builder.append(inputLine);
			arr.add(inputLine.trim());
		}
		in.close();
//		String text = builder.toString();
		String[] ans = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			ans[i] = arr.get(i);
		}
		return ans;
//		return text;
	}
	
	static ArrayList<Node> getNodes(String[] text) {
		ArrayList<Node> nodes = new ArrayList<>();
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
					nodes.add(new Node(id, lon, lat));
					if (!usedId.contains(id)) {
						usedId.add(id);
					} else {
						System.out.println("Something strange: same node was found twice or more");
					}
				} else {
					System.out.println("Error: one of the nodes doesn't have info about it's id, lat or lon");
				}
			}
		}
		
//		Matcher idMatcher = Pattern.compile(idRegex).matcher(text);
//		Matcher lonMatcher = Pattern.compile(lonRegex).matcher(text);
//		Matcher latMatcher = Pattern.compile(latRegex).matcher(text);
//		while (idMatcher.find()) {
//			boolean b = lonMatcher.find() && latMatcher.find();
//			if (!b) {
//				System.out.println("Error: one of the nodes doesn't have info about it's lat or lon");
//				System.exit(0);
//			} else {
//				String idStr = text.substring(idMatcher.start(), idMatcher.end());
//				String lonStr = text.substring(lonMatcher.start(), lonMatcher.end());
//				String latStr = text.substring(latMatcher.start(), latMatcher.end());
//				idStr = idStr.substring(10, idStr.length() - 1);
//				long id = Long.parseLong(idStr);
//				lonStr = lonStr.substring(5, lonStr.length() - 1);
//				latStr = latStr.substring(5, latStr.length() - 1);
//				double lon = Double.parseDouble(lonStr);
//				double lat = Double.parseDouble(latStr);
//				nodes.add(new Node(id, lon, lat));
//				if (!usedId.contains(id)) {
//					usedId.add(id);
//				} else {
//					System.out.println("Something strange: same node was found twice or more");
//				}
//			}
//		}
		
		return nodes;
	}
	
	static ArrayList<Road> getRoads(String[] text) {
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
		ArrayList<Long> path; 
		for (int i = 0; i < text.length; i++) {
			if (text[i].matches(wayRegex)) {
				path = new ArrayList<>();
				while (i < text.length - 1) {
					i++;
					if (text[i].matches(wayCloseRegex))
						break;
					if (text[i].matches(refRegex)) {
						String ref = text[i].substring(9, text[i].length() - 3);
						Long refId = Long.parseLong(ref);
						path.add(refId);
					} else if (text[i].matches(highwayTagRegex)) {
						matcher = Pattern.compile(highwayTypeRegex).matcher(text[i]);
						if (matcher.find()) {
							String type = text[i].substring(matcher.start() + 3, matcher.end() - 1);
							if (neededTypes.contains(type)) {
								Road road = new Road(path);
								roads.add(road);
								idsInRoads.addAll(path);
							}
						}
					}
				}
			}
		}
		
		for (Road r : roads) {
			if (!isCorrectRoad(r))
				System.out.println("One of roads is incorrect. That will probably never happen but if it does you should punch me in my face with anger.");
		}
		
		return roads;
	}
	
	static void nodesToFile(String filename, ArrayList<Node> nodes) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(filename));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Node node : nodes) {
			oos.writeObject(node);			
		}
		oos.flush();
		oos.close();
	}
	
	static void roadsToFile(String filename, ArrayList<Road> roads) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(filename));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (Road road : roads) {
			oos.writeObject(road);			
		}
		oos.flush();
		oos.close();
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		long start = System.currentTimeMillis();
		usedId = new HashSet<>();
		idsInRoads = new HashSet<>();
		
		String filename = "map.osm";
		String text[] = getTextFromFile(filename);
		System.out.println("Text loaded successfully");
		
		ArrayList<Node> nodes = getNodes(text);
		System.out.println(nodes.size() + " nodes found");
		
		ArrayList<Road> roads = getRoads(text);
		System.out.println(roads.size() + " roads found");		
		
		ArrayList<Node> nodesInRoads = new ArrayList<>();
		for (Node curr : nodes) {
			if (idsInRoads.contains(curr.id))
				nodesInRoads.add(curr);
		}
		System.out.println(nodesInRoads.size() + " nodes are used in roads");
		
		nodesToFile("nodes.txt", nodes);
		roadsToFile("roads.txt", roads);
		
		System.out.println("Nodes and roads saved in files");
		System.out.println("Processed in " + (System.currentTimeMillis() - start) + " millis");
		
//		FileInputStream fis = new FileInputStream(new File("nodes.txt"));
//		ObjectInputStream oin = new ObjectInputStream(fis);
//		try {
//			while (true) {
//				Node node = (Node) oin.readObject();
//			}
//		} catch (Exception e) {}
//		oin.close();
	}
}

