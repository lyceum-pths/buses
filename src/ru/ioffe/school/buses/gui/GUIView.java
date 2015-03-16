package ru.ioffe.school.buses.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.Segment;

@SuppressWarnings("serial")
public class GUIView extends JFrame {

	public static final int VERY_BIG_SIZE = 12;
	public static final int BIG_SIZE = 10;
	public static final int MEDIUM_SIZE = 8;
	public static final int SMALL_SIZE = 6;
	public static final int TINY_SIZE = 4;

	GUIModel model;
	BufferedImage mapInBB;
	int busSize;
	int personSize;
	int crossroadSize;
	boolean showBus;
	boolean showPerson;
	boolean showCrossroads;
	boolean showWay;


	public GUIView(GUIModel model) {
		this.model = model;
		mapInBB = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		// default setting
		busSize = MEDIUM_SIZE;
		personSize = TINY_SIZE;
		crossroadSize = MEDIUM_SIZE;
		showBus = true;
		showPerson = true;
		showCrossroads = false;
		showWay = true;
	}

	public void updateMap() {
		mapInBB = new BufferedImage(model.totalGUIWidth, model.totalGUIHeight
				- model.controlPanelHeight, BufferedImage.TYPE_INT_RGB);
		Graphics g = mapInBB.getGraphics();
		g.fillRect(0, 0, mapInBB.getWidth(), mapInBB.getHeight());
		g.setColor(Color.black);
		double pxSize = model.totalGUIWidth /
				(model.right - model.left);
		for (Road road : model.roads) {
			Point p1 = road.from;
			Point p2 = road.to;
			if (showCrossroads) {
				g.setColor(Color.BLUE);
				double difX = p1.getX() - model.left;
				double difY = - p1.getY() + model.up;
				int x = (int) (difX * pxSize);
				int y = (int) (difY * pxSize);
				g.fillOval(x - crossroadSize / 2, y - crossroadSize / 2, crossroadSize, crossroadSize);
				difX = p1.getX() - model.left;
				difY = - p1.getY() + model.up;
				x = (int) (difX * pxSize);
				y = (int) (difY * pxSize);
				g.fillOval(x - crossroadSize / 2, y - crossroadSize / 2, crossroadSize, crossroadSize);
				g.setColor(Color.BLACK);
			}
			double difX = p1.getX() - model.left;
			double difY = - p1.getY() + model.up;
			int x1 = (int) (difX * pxSize);
			int y1 = (int) (difY * pxSize);
			difX = p2.getX() - model.left;
			difY = - p2.getY() + model.up;
			int x2 = (int) (difX * pxSize);
			int y2 = (int) (difY * pxSize);
			g.drawLine(x1, y1, x2, y2);
		}
	}

	public void drawMap(Graphics g) {
		g.clearRect(0, 0, model.totalGUIWidth, model.totalGUIHeight - model.controlPanelHeight);
		g.drawImage(mapInBB, 0, 0, model.totalGUIWidth, 
				model.totalGUIHeight - model.controlPanelHeight, null);
		double pxSize = model.totalGUIWidth / (model.right - model.left);
		if (showWay) {
			g.setColor(Color.MAGENTA);
			Route r = model.currentBus.getRoute();
			for (Segment s : r.getRoute()) {
				Point p1 = s.getStart();
				Point p2 = s.getEnd();
				double difX = p1.getX() - model.left;
				double difY = - p1.getY() + model.up;
				int x1 = (int) (difX * pxSize);
				int y1 = (int) (difY * pxSize);
				difX = p2.getX() - model.left;
				difY = - p2.getY() + model.up;
				int x2 = (int) (difX * pxSize);
				int y2 = (int) (difY * pxSize);
				g.drawLine(x1, y1, x2, y2);
				g.drawLine(x1, y1 + 1, x2, y2 + 1);
			}
		}
		g.setColor(Color.RED);
		int activeBusesCnt = 0;
		for (Bus bus : model.buses) {
			Point p = bus.getPosition(model.currentTime);
			if (p != null) {
				activeBusesCnt++;
				if (showBus) {
					double difX = p.getX() - model.left;
					double difY = - p.getY() + model.up;
					int x = (int) (difX * pxSize);
					int y = (int) (difY * pxSize);
					g.fillOval(x - busSize / 2, y - busSize / 2, busSize, busSize);
				}
			}			
		}
		model.activeBuses = activeBusesCnt;
		if (showPerson) {
			g.setColor(Color.GREEN);
			for (Route route : model.peopleRoutes) {
				Point p = route.getPosition(model.currentTime);
				if (p != null) {
					double difX = p.getX() - model.left;
					double difY = - p.getY() + model.up;
					int x = (int) (difX * pxSize);
					int y = (int) (difY * pxSize);
					g.fillOval(x - personSize / 2, y - personSize / 2, personSize, personSize);
				}
			}
		}
		g.setColor(Color.BLACK);
	}

	public void setMapInBB(BufferedImage mapInBB) {
		this.mapInBB = mapInBB;
	}

	public void setBusSize(int busSize) {
		this.busSize = busSize;
	}

	public void setCrossroadSize(int crossroadSize) {
		this.crossroadSize = crossroadSize;
	}

	public void setShowBus(boolean showBus) {
		this.showBus = showBus;
	}

	public void setShowPerson(boolean showPerson) {
		this.showPerson = showPerson;
	}

	public void setShowCrossroads(boolean showCrossroads) {
		this.showCrossroads = showCrossroads;
	}

	public void setShowWay(boolean showWay) {
		this.showWay = showWay;
	}

	public int getPersonSize() {
		return personSize;
	}

	public void setPersonSize(int personSize) {
		this.personSize = personSize;
	}

	public int getBusSize() {
		return busSize;
	}

	public int getCrossroadSize() {
		return crossroadSize;
	}

	public boolean isShowBus() {
		return showBus;
	}

	public boolean isShowPerson() {
		return showPerson;
	}

	public boolean isShowCrossroads() {
		return showCrossroads;
	}

	public boolean isShowWay() {
		return showWay;
	}
}
