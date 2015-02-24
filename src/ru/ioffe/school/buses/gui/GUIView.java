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
		int cnt = 0;
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
//			if (y1 == y2 && x1 == x2) {
//				System.out.println(p1.toString() + " " + p2.toString());
//				System.out.println("left = " + model.left + "; right = " + model.right);
//				System.out.println("pxSize = " + pxSize);
//				difX = p1.getX() - model.left;
//				difY = - p1.getY() + model.up;
//				System.out.println("difX1 = " + difX + "; difY1 = " + difY);
//				System.out.println("x1 = " + difX * pxSize + "; y1 = " + difY * pxSize);
//				difX = p2.getX() - model.left;
//				difY = - p2.getY() + model.up;
//				System.out.println("difX2 = " + difX + "; difY2 = " + difY);
//				System.out.println("x2 = " + difX * pxSize + "; y2 = " + difY * pxSize);
//				System.out.println();
//				break;
//				cnt++;
//			}
			g.drawLine(x1, y1, x2, y2);
		}
		System.out.println();
//		System.out.println(cnt);
	}
}
