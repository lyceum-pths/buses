package ru.ioffe.school.buses.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road2;

public class GUIModel {
	private ArrayList<Point> points;
	private ArrayList<Road2> roads;
	ArrayList<Road2> roadsInBB;
	private HashMap<Long, Point> pointsById;
	int totalGUIWidth, totalGUIHeight, controlPanelHeight;
	double right, left, up, down;
	double minX, minY, maxX, maxY;
	
	public GUIModel(File pointsFile, File roadsFile) throws IOException {
		points = new ArrayList<>();
		roads = new ArrayList<>();
		roadsInBB = new ArrayList<>();
		getPoints(pointsFile);
		try {
			getRoads(roadsFile);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getMaxSizes();
		updateRoadsInBB();
		
	}
	
	public void setTotalSizes(int width, int heigth, int panelHeight) {
		totalGUIWidth = width;
		totalGUIHeight = heigth;
		controlPanelHeight = panelHeight;
	}

	public Point getPoint(long id) {
		return pointsById.get(id);
	}
	
	public void moveUp(int percent) {
		double per = (double) percent / 100;
		double totalH = up - down;
		if (up + totalH * per > maxY)
			return;
		up += totalH * per;
		down += totalH * per;
		updateRoadsInBB();
	}
	
	public void moveDown(int percent) {
		double per = (double) percent / 100;
		double totalH = up - down;
		if (down - totalH * per < minY)
			return;
		up -= totalH * per;
		down -= totalH * per;
		updateRoadsInBB();
	}

	public void moveRight(int percent) {
		double per = (double) percent / 100;
		double totalW = right - left;
		if (right + totalW * per > maxX)
			return;
		right += totalW * per;
		left += totalW * per;
		updateRoadsInBB();
	}
	
	public void moveLeft(int percent) {
		double per = (double) percent / 100;
		double totalW = right - left;
		if (left - totalW * per < minX)
			return;
		right -= totalW * per;
		left -= totalW * per;
		updateRoadsInBB();
	}
	
	public void zoom(int percent) {
		double per = (double) percent / 200;
		double totalW = right - left;
		double totalH = up - down;
//		if (roadsInBB.size() < 100 && percent > 0)
//			return;
//		if (roadsInBB.size() == roads.size() && percent < 0)
//			return;
		right -= totalW * per;
		left += totalW * per;
		up -= totalH * per;
		down += totalH * per;
		updateRoadsInBB();
	}
	
	private void getPoints(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				points.add((Point) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
		pointsById = new HashMap<>();
		left = Double.MAX_VALUE;
		down = Double.MAX_VALUE;
		right = Double.MIN_VALUE;
		up = Double.MIN_VALUE;
		for (Point p : points) {
			pointsById.put(p.getID(), p);
		}
	}
	
	private void getRoads(File file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				roads.add((Road2) oin.readObject());
			}
			
		} catch (Exception e) {}
		oin.close();
		System.out.println("num of roads = " + roads.size());
	}

	private void getMaxSizes() {
		for (Road2 r : roads) {
			Point p = pointsById.get(r.getFromId());
			if (p.getX() < left)
				left = p.getX();
			if (p.getX() > right)
				right = p.getX();
			if (p.getY() < down)
				down = p.getY();
			if (p.getY() > up)
				up = p.getY();
			p = pointsById.get(r.getToId());
			if (p.getX() < left)
				left = p.getX();
			if (p.getX() > right)
				right = p.getX();
			if (p.getY() < down)
				down = p.getY();
			if (p.getY() > up)
				up = p.getY();
		}
		minX = left;
		minY = down;
		maxX = right;
		maxY = up;
		right = left + up - down;
	}
	
	private void updateRoadsInBB() {
		roadsInBB.clear();
		for (Road2 r : roads) {
			Point from = pointsById.get(r.getFromId());
			Point to = pointsById.get(r.getToId());
			boolean inside = true;
			if (from.getY() < down || from.getY() > up ||
					from.getX() < left || from.getX() > right)
				inside = false;
			if (to.getY() < down || to.getY() > up ||
					to.getX() < left || to.getX() > right)
				inside = false;
			if (inside)
				roadsInBB.add(r);
		}
	}
}
