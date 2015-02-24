package ru.ioffe.school.buses.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GUIControl extends JFrame implements KeyListener {
	
	GUIModel model;
	GUIView view;
	int totalWidth, totalHeight;
	int controlPanelHeight;
	int percent;
	JPanel mapPanel, controlPanel;
	JPanel mapControlPanel, infoPanel, timelinePanel;
	JButton zoomButton, unzoomButton, upButton, downButton, leftButton, rightButton;
	JLabel actualRoadsNumberLabel; 
	
	{
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws IOException {
		model = new GUIModel(new File("roads.txt"));
		view = new GUIView(model);
		Dimension d = getToolkit().getScreenSize();
		percent = 20;
		controlPanelHeight = 200;
		totalHeight = d.height * 2 / 3;
		totalWidth = d.width * 2 / 3;
		int minimumWidth = Math.max(d.width / 2, 3 * controlPanelHeight);
		int minimumHeight = controlPanelHeight;
		model.updateTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		actualRoadsNumberLabel = new JLabel();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Map GUI v0.2");
		this.setPreferredSize(new Dimension(totalWidth, totalHeight));
		this.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		this.pack();
		this.setLayout(null);
		setPanels();
		updateInfoLabels();
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
		timelinePanel.setBounds(2 * controlPanelHeight, 0, totalWidth - 
				2 * controlPanelHeight, controlPanelHeight);
		controlPanel.setBounds(0, 0, totalWidth, controlPanelHeight);
		model.updateTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		model.updateWHRatio();
	}
	
	private void updateInfoLabels() {		
		actualRoadsNumberLabel.setText("Roads on the map: " + model.roadsInBB.size());
		infoPanel.repaint();
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
		mapControlPanel = new JPanel();
		infoPanel = new JPanel();
		timelinePanel = new JPanel();
		controlPanel.add(mapControlPanel);
		controlPanel.add(infoPanel);
		controlPanel.add(timelinePanel);
		mapControlPanel.setBounds(0, 0, controlPanelHeight, controlPanelHeight);
		mapControlPanel.setLayout(null);
		mapControlPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		infoPanel.setBounds(controlPanelHeight, 0, controlPanelHeight, controlPanelHeight);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
		infoPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		infoPanel.add(actualRoadsNumberLabel);
		timelinePanel.setBounds(2 * controlPanelHeight, 0, totalWidth - 
				2 * controlPanelHeight, controlPanelHeight);
		timelinePanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		setButtons();
	}
	
	private void setButtons() {
		zoomButton = new JButton("+");
		mapControlPanel.add(zoomButton);
		zoomButton.setBounds(10, 10, 50, 50);
		zoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.zoom(percent);
				mapPanel.repaint();
				updateInfoLabels();
			}
		});
		zoomButton.addKeyListener(this);
		unzoomButton = new JButton("-");
		mapControlPanel.add(unzoomButton);
		unzoomButton.setBounds(140, 10, 50, 50);
		unzoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.zoom(-percent);
				mapPanel.repaint();
				updateInfoLabels();
			}
		});
		unzoomButton.addKeyListener(this);
		upButton = new JButton("Up");
		mapControlPanel.add(upButton);
		upButton.setBounds(60, 70, 80, 30);
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveUp(percent);
				mapPanel.repaint();
				updateInfoLabels();
			}
		});
		upButton.addKeyListener(this);
		downButton = new JButton("Down");
		mapControlPanel.add(downButton);
		downButton.setBounds(60, 150, 80, 30);
		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveDown(percent);
				mapPanel.repaint();
				updateInfoLabels();
			}
		});
		downButton.addKeyListener(this);
		rightButton = new JButton("Right");
		mapControlPanel.add(rightButton);
		rightButton.setBounds(110, 110, 80, 30);
		rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveRight(percent);
				mapPanel.repaint();
				updateInfoLabels();
			}
		});
		rightButton.addKeyListener(this);
		leftButton = new JButton("Left");
		mapControlPanel.add(leftButton);
		leftButton.setBounds(10, 110, 80, 30);
		leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.moveLeft(percent);
				mapPanel.repaint();
				updateInfoLabels();
			}
		});
		leftButton.addKeyListener(this);
	}
	
	public static void main(String[] args) {
		new GUIControl().setVisible(true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			model.moveUp(percent);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			model.moveDown(percent);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			model.moveLeft(percent);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			model.moveRight(percent);
		} else if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
			model.zoom(percent);
		} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			model.zoom(-percent);
		}
		mapPanel.repaint();
		updateInfoLabels();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}
}
