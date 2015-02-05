package ru.ioffe.school.buses.parsing;

import java.io.Serializable;
import java.util.ArrayList;

public class Road implements Serializable {
	private static final long serialVersionUID = 1L;
	long[] path;
	
	public Road(ArrayList<Long> path) {
		this.path = new long[path.size()];
		for (int i = 0; i < path.size(); i++) {
			this.path[i] = path.get(i);
		}
	}
}
