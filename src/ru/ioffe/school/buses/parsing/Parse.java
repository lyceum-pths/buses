package ru.ioffe.school.buses.parsing;

import java.io.File;
import java.io.IOException;

public class Parse {
	public static void main(String[] args) throws IOException {
		String filename = "map.osm";
		MapParser parser = new MapParser();
		parser.getRoads(new File(filename));
		
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