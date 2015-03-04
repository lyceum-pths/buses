package ru.ioffe.school.buses;

import java.io.File;
import java.io.IOException;

import ru.ioffe.school.buses.gui.GUIControl;
import ru.ioffe.school.buses.parsing.MapParser;

/*
 * That's what you need to do if you've just cloned that project :)
 */

public class Main {
	public static void main(String[] args) throws IOException {
		System.out.println("Hello, buses!");
		System.out.println("Parsing roads from map.osm file...");
		MapParser.getRoads(new File("data/map.osm"));
		System.out.println("Running GUIControl...");
		GUIControl.main(args);
	}
}
