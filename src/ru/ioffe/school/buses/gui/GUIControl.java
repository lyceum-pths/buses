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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class GUIControl extends JFrame implements KeyListener, ActionListener {
	
	GUIModel model;
	GUIView view;
	int totalWidth, totalHeight;
	int controlPanelHeight;
	int percent, fps, timeUpdateDelay;
	JPanel mapPanel, controlPanel;
	JPanel mapControlPanel, infoPanel, timelinePanel;
	JButton zoomButton, unzoomButton, upButton, downButton, leftButton, rightButton;
	JButton pauseButton, updateSpeedButton;
	JSlider timeSlider, timeSpeedSlider;
	JTextField speedField;
	JLabel actualRoadsNumberLabel, fpsLabel, speedLabel, timeLabel;
	Timer updateScreenTimer, updateTimeTimer;
	
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
		fps = 30;
		timeUpdateDelay = 25;
		updateScreenTimer = new Timer(1000 / fps, this);
		controlPanelHeight = 200;
		totalHeight = d.height * 2 / 3;
		totalWidth = d.width * 2 / 3;
		int minimumWidth = Math.max(d.width / 2, 5 * controlPanelHeight);
		int minimumHeight = 3 * controlPanelHeight;
		model.updateTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		view.updateMapInBB();
		actualRoadsNumberLabel = new JLabel();
		fpsLabel = new JLabel();
		speedLabel = new JLabel();
		timeLabel = new JLabel();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Map GUI v0.2");
		this.setPreferredSize(new Dimension(totalWidth, totalHeight));
		this.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		this.pack();
		this.setLayout(null);
		updateTimeTimer = new Timer(timeUpdateDelay, this);
		setPanels();
		updateInfoLabels();
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateSizes();
			}
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		updateScreenTimer.start();
		updateTimeTimer.start();
		updateSizes();
	}
	
	private void updateSizes() {
		totalWidth = this.getWidth();
		totalHeight = this.getHeight();
		mapPanel.setBounds(0, controlPanelHeight, totalWidth, totalHeight);
		timelinePanel.setBounds(2 * controlPanelHeight, 0, totalWidth - 
				2 * controlPanelHeight, controlPanelHeight);
		controlPanel.setBounds(0, 0, totalWidth, controlPanelHeight);
		timeSlider.setBounds(10, 150, totalWidth - 2 * controlPanelHeight - 20, 30);
		model.updateTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		model.updateWHRatio();
		view.updateMapInBB();
	}
	
	private void updateInfoLabels() {		
		actualRoadsNumberLabel.setText("Roads on the map: " + model.roadsInBB.size());
		fpsLabel.setText("fps: " + fps);
		speedLabel.setText("Time speed: " + model.timeSpeed);
		timeLabel.setText("Current time: " + (int) model.currentTime + " secs");
		infoPanel.repaint();
	}
	
	private void setPanels() {
		mapPanel = new JPanel() {
			@Override
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
		infoPanel.add(fpsLabel);
		infoPanel.add(speedLabel);
		infoPanel.add(timeLabel);
		timelinePanel.setBounds(2 * controlPanelHeight, 0, totalWidth - 
				2 * controlPanelHeight, controlPanelHeight);
		timelinePanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		timelinePanel.setLayout(null);
		setButtons();
	}
	
	private void setButtons() {
		zoomButton = new JButton("+");
		mapControlPanel.add(zoomButton);
		zoomButton.setBounds(10, 10, 50, 50);
		zoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.zoom(percent);
				view.updateMapInBB();
			}
		});
		zoomButton.addKeyListener(this);
		
		unzoomButton = new JButton("-");
		mapControlPanel.add(unzoomButton);
		unzoomButton.setBounds(140, 10, 50, 50);
		unzoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.zoom(-percent);
				view.updateMapInBB();
			}
		});
		unzoomButton.addKeyListener(this);
		
		upButton = new JButton("Up");
		mapControlPanel.add(upButton);
		upButton.setBounds(60, 70, 80, 30);
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.moveUp(percent);
				view.updateMapInBB();
			}
		});
		upButton.addKeyListener(this);
		
		downButton = new JButton("Down");
		mapControlPanel.add(downButton);
		downButton.setBounds(60, 150, 80, 30);
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.moveDown(percent);
				view.updateMapInBB();
			}
		});
		downButton.addKeyListener(this);
		
		rightButton = new JButton("Right");
		mapControlPanel.add(rightButton);
		rightButton.setBounds(110, 110, 80, 30);
		rightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.moveRight(percent);
				view.updateMapInBB();
			}
		});
		rightButton.addKeyListener(this);
		
		leftButton = new JButton("Left");
		mapControlPanel.add(leftButton);
		leftButton.setBounds(10, 110, 80, 30);
		leftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.moveLeft(percent);
				view.updateMapInBB();
			}
		});
		leftButton.addKeyListener(this);
		
		pauseButton = new JButton("Pause");
		pauseButton.setFocusable(false);
		timelinePanel.add(pauseButton);
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (model.timePaused) {
					model.timePaused = false;
					pauseButton.setText("Pause");
					updateTimeTimer.start();
				} else {
					model.timePaused = true;
					pauseButton.setText("Continue");
					updateTimeTimer.stop();
				}
			}
		});
		pauseButton.setBounds(10, 10, 100, 30);
		
		JLabel tssTipLabel = new JLabel("Change time speed:");
		timelinePanel.add(tssTipLabel);
		tssTipLabel.setBounds(10, 50, 200, 30);
		
		timeSpeedSlider = new JSlider(1, (int) model.maxTime / 7);
		timeSpeedSlider.setFocusable(false);
		timelinePanel.add(timeSpeedSlider);
		timeSpeedSlider.setBounds(10, 80, 2 * controlPanelHeight, 30);
		timeSpeedSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				model.timeSpeed = timeSpeedSlider.getValue();
			}
		});
		
		JLabel tsTipLabel = new JLabel("Change time:");
		timelinePanel.add(tsTipLabel);
		tsTipLabel.setBounds(10, 120, 200, 30);
		
		timeSlider = new JSlider(0, (int) model.maxTime);
		timeSlider.setFocusable(false);
		timelinePanel.add(timeSlider);
		timeSlider.setBounds(10, 150, totalWidth - 2 * controlPanelHeight - 20, 30);
		timeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				model.currentTime = timeSlider.getValue();
			}
		});
		timeSlider.setValue((int) model.currentTime);
		timeSpeedSlider.setValue((int) model.timeSpeed);
		
		JLabel updateTipLabel = new JLabel("Insert time speed:");
		timelinePanel.add(updateTipLabel);
		updateTipLabel.setBounds(150, 10, 130, 30);
		
		speedField = new JTextField();
		timelinePanel.add(speedField);
		speedField.setBounds(280, 10, 50, 30);
		
		updateSpeedButton = new JButton("Update");
		updateSpeedButton.addKeyListener(this);
		timelinePanel.add(updateSpeedButton);
		updateSpeedButton.setBounds(340, 10, 100, 30);
		updateSpeedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String speed = speedField.getText().trim();
				try {
					double sp = Double.parseDouble(speed);
					if (sp <= 0)
						return;
					model.timeSpeed = sp;
				} catch (NumberFormatException exept) {
				}
			}
		});
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
		} else if (e.getKeyCode() == KeyEvent.VK_P) {
			try {
				pauseButton.getActionListeners()[0].actionPerformed(null);				
			} catch (ArrayIndexOutOfBoundsException exept) {
				System.out.println("No listeners attached to pause button");
			}
		}
		mapPanel.repaint();
		updateInfoLabels();
		view.updateMapInBB();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateScreenTimer) {
			mapPanel.repaint();
			updateInfoLabels();
			infoPanel.repaint();
		} else if (e.getSource() == updateTimeTimer) {
			model.currentTime += model.timeSpeed * timeUpdateDelay / 1000;
			timeSlider.setValue((int) model.currentTime);
			timeSpeedSlider.setValue((int) model.timeSpeed);
			if (model.currentTime > model.maxTime)
				updateTimeTimer.stop();
		}
	}
}
