package ru.ioffe.school.buses.parsing;

import java.io.File;
import java.io.IOException;

public class Parse {
	public static void main(String[] args) throws IOException {
		String filename = "map";
		MapParser.getRoads(new File(filename), true);
	}
}
