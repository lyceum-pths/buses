package ru.ioffe.school.buses.data;

import java.io.Serializable;

/* This class stores an elementary part of a road
 * from one Point to another. Will be just Road soon.
 */

public class Road2 implements Serializable {
	private static final long serialVersionUID = 6411040026365599897L;
	final long fromId;
	final long toId;
	
	public Road2(long from, long to) {
		this.fromId = from;
		this.toId = to;
	}
	
	public long getToId() {
		return toId;
	}
	
	public long getFromId() {
		return fromId;
	}
	
	public String toString() {
		return "[from " + fromId + " to " + toId + "]";
	}
}
