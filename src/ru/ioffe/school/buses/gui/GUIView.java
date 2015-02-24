package ru.ioffe.school.buses.gui;

import java.awt.Graphics;

import javax.swing.JFrame;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;

@SuppressWarnings("serial")
public class GUIView extends JFrame {
	GUIModel model;
	
	public GUIView(GUIModel model) {
		this.model = model;
	}
	
	public void drawMap(Graphics g) {
		double pxSize = model.totalGUIWidth /
				(model.right - model.left);
		g.clearRect(0, 0, model.totalGUIWidth, model.totalGUIHeight - model.controlPanelHeight);
		for (Road road : model.roadsInBB) {
			Point p1 = road.from;
			Point p2 = road.to;
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
