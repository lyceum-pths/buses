package ru.ioffe.school.buses.parsing;

import java.io.Serializable;

public class Node implements Serializable {
	private static final long serialVersionUID = 1L;
	long id;
	double lon;
	double lat;
	boolean isUsed;
	
	public Node(long id, double lon, double lat) {
		this.id = id;
		this.lon = lon;
		this.lat = lat;
	}
}
