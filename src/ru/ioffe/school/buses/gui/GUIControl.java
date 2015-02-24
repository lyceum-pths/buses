package ru.ioffe.school.buses.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GUIControl extends JFrame {
	
	GUIModel model;
	GUIView view;
	int totalWidth, totalHeight;
	int controlPanelHeight;
	JPanel mapPanel, controlPanel;
	JButton zoomButton, unzoomButton, upButton, downButton, leftButton, rightButton;
	
	{
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws IOException {
		model = new GUIModel(new File("points.txt"), new File("roads.txt"));
		view = new GUIView(model);
		Dimension d = getToolkit().getScreenSize();
		totalWidth = 800;
		controlPanelHeight = 200;
		totalHeight = d.height;
		int minimumWidth = totalWidth;
		int minimumHeight = controlPanelHeight;
		model.setTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Map GUI v0.1");
		this.setPreferredSize(new Dimension(totalWidth, totalHeight));
		this.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		this.pack();
		this.setLayout(null);
//		this.setBackground(Color.red); //why that does not work?
//		this.setResizable(false);
		setPanels();
		this.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				updateSizes();
			}
			public void componentShown(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}
	
	private void updateSizes() {
		totalWidth = this.getWidth();
		totalHeight = this.getHeight();
		mapPanel.setBounds(0, controlPanelHeight, totalWidth, totalHeight);
		controlPanel.setBounds(0, 0, totalWidth, controlPanelHeight);
		this.validate();
	}
	
	private void setPanels() {
		mapPanel = new JPanel() {
			public void paint(Graphics g) {
				view.drawMap(g);
			}
		};
		controlPanel = new JPanel();
		this.add(mapPanel);
		mapPanel.setBounds(0, controlPanelHeight, totalWidth, totalHeight);
		mapPanel.setLayout(null);
		this.add(controlPanel);
		controlPanel.setBounds(0, 0, totalWidth, controlPanelHeight);
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		controlPanel.setBackground(Color.white);
		controlPanel.setLayout(null);
		setButtons();
	}
	
	private void setButtons() {
		zoomButton = new JButton("+");
		controlPanel.add(zoomButton);
		zoomButton.setBounds(10, 10, 40, 30);
		zoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.zoom(20);
				mapPanel.repaint();
			}
		});
		unzoomButton = new JButton("-");
		controlPanel.add(unzoomButton);
		unzoomButton.setBounds(60, 10, 40, 30);
		unzoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.zoom(-20);
				mapPanel.repaint();
			}
		});
		upButton = new JButton("Up");
		controlPanel.add(upButton);
		upButton.setBounds(10, 40, 100, 30);
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveUp(20);
				mapPanel.repaint();
			}
		});
		downButton = new JButton("Down");
		controlPanel.add(downButton);
		downButton.setBounds(10, 70, 100, 30);
		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveDown(20);
				mapPanel.repaint();
			}
		});
		rightButton = new JButton("Right");
		controlPanel.add(rightButton);
		rightButton.setBounds(10, 100, 100, 30);
		rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveRight(20);
				mapPanel.repaint();
			}
		});
		leftButton = new JButton("Left");
		controlPanel.add(leftButton);
		leftButton.setBounds(10, 130, 100, 30);
		leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveLeft(20);
				mapPanel.repaint();
			}
		});
	}
	
	public static void main(String[] args) {
		new GUIControl().setVisible(true);
	}
}
