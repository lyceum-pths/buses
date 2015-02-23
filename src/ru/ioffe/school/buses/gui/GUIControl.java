package ru.ioffe.school.buses.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		totalWidth = 800;
		totalHeight = 1000;
		controlPanelHeight = 200;
		model.setTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Map GUI");
		this.setPreferredSize(new Dimension(totalWidth, totalHeight));
		this.pack();
		this.setLayout(null);
//		this.setBackground(Color.red); //why that does not work?
		this.setResizable(false);
		setPanels();
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
		JButton zoomButton = new JButton("+");
		controlPanel.add(zoomButton);
		zoomButton.setBounds(10, 10, 100, 30);
		zoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.zoom(20);
				mapPanel.repaint();
			}
		});
		JButton upb = new JButton("Up");
		controlPanel.add(upb);
		upb.setBounds(10, 50, 100, 30);
		upb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveUp(20);
				mapPanel.repaint();
			}
		});
	}
	
	public static void main(String[] args) {
		new GUIControl().setVisible(true);
	}
}
