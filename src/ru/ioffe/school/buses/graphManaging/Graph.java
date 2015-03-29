package ru.ioffe.school.buses.graphManaging;

import java.util.ArrayList;
import java.util.HashMap;

import ru.ioffe.school.buses.data.Point;

public class Graph {
	final ArrayList<Point> nodes;
	final ArrayList<Edge>[] edges;
	final HashMap<Point, Integer> indexs;
	
	public Graph(ArrayList<Point> nodes, ArrayList<Edge>[] edges, HashMap<Point, Integer> indexs) {
		this.nodes = nodes;
		this.edges = edges;
		this.indexs = indexs;
	}

	public ArrayList<Point> getNodes() {
		return nodes;
	}

	public ArrayList<Edge>[] getEdges() {
		return edges;
	}
	
	public HashMap<Point, Integer> getIndexs() {
		return indexs;
	}
}