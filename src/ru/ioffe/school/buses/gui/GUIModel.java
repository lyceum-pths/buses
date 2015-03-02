package ru.ioffe.school.buses.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Station;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.routeGeneration.BusGenerator;

public class GUIModel {
	private ArrayList<Road> roads;
	ArrayList<Road> roadsInBB;
	int totalGUIWidth, totalGUIHeight, controlPanelHeight;
	double right, left, up, down;
	double minX, minY, maxX, maxY;
	private double globalZoomMin, globalZoomMax;
	Bus bus;
	BusGenerator generator;
	double currentTime, timeSpeed, maxTime;
	boolean timePaused;
	
	public GUIModel(File roadsFile) throws IOException {
		roads = new ArrayList<>();
		roadsInBB = new ArrayList<>();
		currentTime = 2;
		timeSpeed = 1;
		maxTime = 43200;
		timePaused = false;
		getRoads(roadsFile);
		updateWHRatio();
		getMaxSizes();
		updateRoadsInBB();
		Road[] roadsForManager = new Road[roads.size()];
		for (int i = 0; i < roads.size(); i++) {
			roadsForManager[i] = roads.get(i);
		}
		RoadManager manager = new RoadManager(roadsForManager);
		Station[] stations = {new Station(roads.get(10423).from), new Station(roads.get(10423).to)};
		generator = new BusGenerator(manager, stations);
		bus = generator.generateBus(2, true, true, 1, maxTime);
	}

	public void setTime(int time) {
		this.currentTime = time;
	}
	
	public void setTimeSpeed(int speed) {
		this.timeSpeed = speed;
	}
	
	public void updateWHRatio() {
		double ratio = (double) totalGUIWidth / (double) (totalGUIHeight - controlPanelHeight);
		up = down + (right - left) / ratio;
		updateRoadsInBB();
	}
	
	public void updateTotalSizes(int width, int heigth, int panelHeight) {
		totalGUIWidth = width;
		totalGUIHeight = heigth;
		controlPanelHeight = panelHeight;
	}

	public void moveVert(int percent) {
		double per = (double) percent / 100;
		double totalH = up - down;
		if (up + totalH * per - totalH / 2 > maxY)
			return;
		up += totalH * per;
		down += totalH * per;
		updateRoadsInBB();
	}
	
	public void moveVertPx(int px) {
		double totalH = up - down;
		double pxSize = totalH / totalGUIHeight;
		if (up + pxSize * px - totalH / 2 > maxY)
			return;
		up += pxSize * px;
		down += pxSize * px;
		updateRoadsInBB();
	}
	
	public void moveHoriz(int percent) {
		double per = (double) percent / 100;
		double totalW = right - left;
		if (right + totalW * per - totalW / 2 > maxX)
			return;
		right += totalW * per;
		left += totalW * per;
		updateRoadsInBB();
	}

	public void moveHorizPx(int px) {
		double totalW = right - left;
		double pxSize = totalW / totalGUIWidth;
		if (right + pxSize * px - totalW / 2 > maxX)
			return;
		right += pxSize * px;
		left += pxSize * px;
		updateRoadsInBB();
	}
	
	public void zoom(int percent) {
		double per = (double) percent / 200;
		double totalW = right - left;
		double totalH = up - down;
		if (percent > 0 && Math.min(totalH, totalW) < globalZoomMin)
			return;
		if (percent < 0 && Math.max(totalH, totalW) > globalZoomMax)
			return;
		right -= totalW * per;
		left += totalW * per;
		up -= totalH * per;
		down += totalH * per;
		updateRoadsInBB();
	}
	
	private void getRoads(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				roads.add((Road) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
	}

	private void getMaxSizes() {
		left = Double.MAX_VALUE;
		down = Double.MAX_VALUE;
		right = Double.MIN_VALUE;
		up = Double.MIN_VALUE;
		for (Road r : roads) {
			Point p = r.from;
			if (p.getX() < left)
				left = p.getX();
			if (p.getX() > right)
				right = p.getX();
			if (p.getY() < down)
				down = p.getY();
			if (p.getY() > up)
				up = p.getY();
			p = r.to;
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
		globalZoomMax = Math.max(3 * (maxX - minX), 3 * (maxY - minY));
		globalZoomMin = Math.min((maxX - minX) / 50, (maxY - minY) / 50);
	}
	
	private void updateRoadsInBB() {
		roadsInBB.clear();
		for (Road r : roads) {
			Point from = r.from;
			Point to = r.to;
			int cnt = 0;
			if (from.getY() < down || from.getY() > up ||
					from.getX() < left || from.getX() > right)
				cnt++;
			if (to.getY() < down || to.getY() > up ||
					to.getX() < left || to.getX() > right)
				cnt++;
			if (cnt < 2)
				roadsInBB.add(r);
		}
//		roadsInBB = (ArrayList<Road>) roads.clone();
	}
}