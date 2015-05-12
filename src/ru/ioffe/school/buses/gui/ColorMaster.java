package ru.ioffe.school.buses.gui;

import java.awt.Color;

/**
 * @author Mikhail Dvorkin
 */
public class ColorMaster {
	int from, to;
	long seed;
	
	public ColorMaster(int from, int to, long seed) {
		this.from = from;
		this.to = to;
		this.seed = seed;
	}
	
	public ColorMaster() {
		this(50, 255, 394243585);
	}
	
	public int[] colorAsArray(long id) {
		id *= seed;
		int range = to - from;
		int[] color = new int[3];
		for (int i = 0; i < color.length; i++) {
			color[i] = (int) (to - 1 - id % range);
			id /= range;
		}
		return color;
	}
	
	public Color color(long id) {
		int[] array = colorAsArray(id);
		return new Color(array[0], array[1], array[2]);
	}

	public String colorAsString(long id) {
		return arrayToString(colorAsArray(id));
	}

	public static String arrayToString(int[] array) {
		return "#" + hexColor(array[0]) + hexColor(array[1]) + hexColor(array[2]);
	}
	
	public static String hexColor(int color) {
		String result = Integer.toHexString(color);
		if (result.length() == 1) {
			result = "0" + result;
		}
		return result;
	}
}
