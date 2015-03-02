package ru.ioffe.school.buses.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JFrame;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;

@SuppressWarnings("serial")
public class GUIView extends JFrame {
	GUIModel model;
	BufferedImage mapInBB;
	
	public GUIView(GUIModel model) {
		this.model = model;
		mapInBB = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	}
	
	public void updateMap() {
		mapInBB = new BufferedImage(model.totalGUIWidth, model.totalGUIHeight
				- model.controlPanelHeight, BufferedImage.TYPE_INT_RGB);
		Graphics g = mapInBB.getGraphics();
		g.fillRect(0, 0, mapInBB.getWidth(), mapInBB.getHeight());
		g.setColor(Color.black);
		double pxSize = model.totalGUIWidth /
				(model.right - model.left);
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
	
	public void drawMap(Graphics g) {
		g.clearRect(0, 0, model.totalGUIWidth, model.totalGUIHeight - model.controlPanelHeight);
		g.drawImage(mapInBB, 0, 0, model.totalGUIWidth, 
				model.totalGUIHeight - model.controlPanelHeight, null);
		g.setColor(Color.red);
		double pxSize = model.totalGUIWidth / (model.right - model.left);
		Point p = model.bus.getPosition(model.currentTime);
		try {
			PrintWriter out = new PrintWriter(new File("log.txt"));
			if (p != null) {
				double difX = p.getX() - model.left;
				double difY = - p.getY() + model.up;
				int x = (int) (difX * pxSize);
				int y = (int) (difY * pxSize);
				g.fillOval(x, y, 7, 7);
				out.println("time = " + model.currentTime + "; coord = (" + p.getX() + "; " + p.getY() + ")");
				System.out.println("time = " + model.currentTime + "; coord = (" + p.getX() + "; " + p.getY() + ")");
			}
			out.close();
		} catch (Exception e) {}
	}
}