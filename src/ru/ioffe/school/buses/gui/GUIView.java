package ru.ioffe.school.buses.gui;

import java.awt.Graphics;

import javax.swing.JFrame;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road2;

@SuppressWarnings("serial")
public class GUIView extends JFrame {
	GUIModel model;
	
	public GUIView(GUIModel model) {
		this.model = model;
	}
	
	public void drawMap(Graphics g) {
		double pxSize = model.totalGUIWidth /
				(model.right - model.left);
		for (Road2 road : model.roadsInBB) {
			Point p1 = model.getPoint(road.getFromId());
			Point p2 = model.getPoint(road.getToId());
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
}
