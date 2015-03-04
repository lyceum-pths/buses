package ru.ioffe.school.buses.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

import ru.ioffe.school.buses.data.Bus;

@SuppressWarnings("serial")
public class GUIControl extends JFrame {
	
	GUIModel model;
	GUIView view;
	ControlAdapter adapter;
	Bus currentBus;
	DefaultListModel<String> busListModel;
	JList<String> busList;
	JScrollPane busScroller;
	int totalWidth, totalHeight;
	int controlPanelHeight, busPanelWidth, busInfoPanelHeigth;
	int percent, fps, timeUpdateDelay;
	JPanel mapPanel, controlPanel, busPanel;
	JPanel mapControlPanel, infoPanel, timelinePanel;
	JPanel busInfoPanel, busListPanel;
	JButton zoomButton, unzoomButton, upButton, downButton, leftButton, rightButton;
	JButton pauseButton, updateSpeedButton;
	JSlider timeSlider, timeSpeedSlider;
	JTextField speedField;
	JLabel actualRoadsNumberLabel, fpsLabel, speedLabel, timeLabel, busesAmountLabel, activeBusesAmountLabel;
	JLabel currentBusNumLabel, currentBusPathLabel, currentBusTimeLabel;
	Timer updateScreenTimer, updateTimeTimer;
	int currentX, currentY;
	
	{
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws IOException {
		String inFileName = System.getProperty("user.dir") + "/data/generated/roads.data";
		model = new GUIModel(new File(inFileName));
		view = new GUIView(model);
		adapter = new ControlAdapter(this);
		Dimension d = getToolkit().getScreenSize();
		percent = 20;
		fps = 30;
		timeUpdateDelay = 25;
		updateScreenTimer = new Timer(1000 / fps, adapter);
		controlPanelHeight = 200;
		busInfoPanelHeigth = 100;
		busPanelWidth = 250;
		totalHeight = d.height * 2 / 3;
		totalWidth = d.width * 2 / 3;
		int minimumWidth = Math.max(d.width / 2, 5 * controlPanelHeight);
		int minimumHeight = 3 * controlPanelHeight;
		model.updateTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		view.updateMap();
		if (model.buses.size() > 0)
			currentBus = model.buses.get(0);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Map GUI v0.2");
		this.setPreferredSize(new Dimension(totalWidth, totalHeight));
		this.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		this.pack();
		this.setLayout(null);
		updateTimeTimer = new Timer(timeUpdateDelay, adapter);
		setPanels();
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateSizes();
			}
		});
		updateScreenTimer.start();
		updateTimeTimer.start();
		updateSizes();
	}
	
	private void setPanels() {
		mapPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				view.drawMap(g);
			}
		};
		controlPanel = new JPanel();
		busPanel = new JPanel();
		
		this.add(mapPanel);
		mapPanel.addMouseListener(adapter);
		mapPanel.addMouseMotionListener(adapter);
		mapPanel.setBounds(0, controlPanelHeight, totalWidth - busPanelWidth, totalHeight);
		mapPanel.setLayout(null);
		
		this.add(busPanel);
		busPanel.setBounds(totalWidth - busPanelWidth, controlPanelHeight,
				busPanelWidth, totalHeight);
		busPanel.setLayout(null);
		busPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		busInfoPanel = new JPanel();
		busListPanel = new JPanel();
		busPanel.add(busInfoPanel);
		busPanel.add(busListPanel);
		
		busInfoPanel.setBounds(0, 0, busPanelWidth, 100);
		busInfoPanel.setLayout(new BoxLayout(busInfoPanel, BoxLayout.PAGE_AXIS));
		busInfoPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		currentBusNumLabel = new JLabel();
		currentBusPathLabel = new JLabel();
		currentBusTimeLabel = new JLabel();
		busInfoPanel.add(currentBusNumLabel);
		busInfoPanel.add(currentBusPathLabel);
		busInfoPanel.add(currentBusTimeLabel);
		
		busListPanel.setBounds(0, busInfoPanelHeigth, busPanelWidth, totalHeight
				- controlPanelHeight - busInfoPanelHeigth - 20);
		busListPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		busListPanel.setLayout(null);
		busListModel = new DefaultListModel<>();
		for (int i = 0; i < model.buses.size(); i++) {
			busListModel.addElement("bus " + (i + 1));
		}
		busList = new JList<String>(busListModel);
		busList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		busList.setLayoutOrientation(JList.VERTICAL);
		busList.setVisibleRowCount(-1);
		busList.setFocusable(false);
		busList.addKeyListener(adapter);
		busScroller = new JScrollPane(busList);
		busListPanel.add(busScroller);
		busScroller.setBounds(0, 0, busPanelWidth, busListPanel.getHeight());
		
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
		actualRoadsNumberLabel = new JLabel();
		fpsLabel = new JLabel();
		speedLabel = new JLabel();
		timeLabel = new JLabel();
		busesAmountLabel = new JLabel();
		activeBusesAmountLabel = new JLabel();
		infoPanel.add(actualRoadsNumberLabel);
		infoPanel.add(fpsLabel);
		infoPanel.add(speedLabel);
		infoPanel.add(timeLabel);
		infoPanel.add(busesAmountLabel);
		infoPanel.add(activeBusesAmountLabel);
		
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
		zoomButton.addActionListener(adapter);
		zoomButton.addKeyListener(adapter);
		
		unzoomButton = new JButton("-");
		mapControlPanel.add(unzoomButton);
		unzoomButton.setBounds(140, 10, 50, 50);
		unzoomButton.addActionListener(adapter);
		unzoomButton.addKeyListener(adapter);
		
		upButton = new JButton("Up");
		mapControlPanel.add(upButton);
		upButton.setBounds(60, 70, 80, 30);
		upButton.addActionListener(adapter);
		upButton.addKeyListener(adapter);
		
		downButton = new JButton("Down");
		mapControlPanel.add(downButton);
		downButton.setBounds(60, 150, 80, 30);
		downButton.addActionListener(adapter);
		downButton.addKeyListener(adapter);
		
		rightButton = new JButton("Right");
		mapControlPanel.add(rightButton);
		rightButton.setBounds(110, 110, 80, 30);
		rightButton.addActionListener(adapter);
		rightButton.addKeyListener(adapter);
		
		leftButton = new JButton("Left");
		mapControlPanel.add(leftButton);
		leftButton.setBounds(10, 110, 80, 30);
		leftButton.addActionListener(adapter);
		leftButton.addKeyListener(adapter);
		
		pauseButton = new JButton("Pause");
		pauseButton.setFocusable(false);
		timelinePanel.add(pauseButton);
		pauseButton.addActionListener(adapter);
		pauseButton.setBounds(10, 10, 100, 30);
		
		JLabel tssTipLabel = new JLabel("Change time speed:");
		timelinePanel.add(tssTipLabel);
		tssTipLabel.setBounds(10, 50, 200, 30);
		
		timeSpeedSlider = new JSlider(1, (int) model.maxTime / 7);
		timeSpeedSlider.setFocusable(false);
		timelinePanel.add(timeSpeedSlider);
		timeSpeedSlider.setBounds(10, 80, 2 * controlPanelHeight, 30);
		timeSpeedSlider.addChangeListener(adapter);
		
		JLabel tsTipLabel = new JLabel("Change time:");
		timelinePanel.add(tsTipLabel);
		tsTipLabel.setBounds(10, 120, 200, 30);
		
		timeSlider = new JSlider(0, (int) model.maxTime);
		timeSlider.setFocusable(false);
		timelinePanel.add(timeSlider);
		timeSlider.setBounds(10, 150, totalWidth - 2 * controlPanelHeight - 20, 30);
		timeSlider.addChangeListener(adapter);
		timeSlider.setValue((int) model.currentTime);
		timeSpeedSlider.setValue((int) model.timeSpeed);
		
		JLabel updateTipLabel = new JLabel("Insert time speed:");
		timelinePanel.add(updateTipLabel);
		updateTipLabel.setBounds(150, 10, 130, 30);
		
		speedField = new JTextField();
		timelinePanel.add(speedField);
		speedField.setBounds(280, 10, 50, 30);
		
		updateSpeedButton = new JButton("Update");
		updateSpeedButton.addKeyListener(adapter);
		timelinePanel.add(updateSpeedButton);
		updateSpeedButton.setBounds(340, 10, 100, 30);
		updateSpeedButton.addActionListener(adapter);
	}
	
	private void updateSizes() {
		totalWidth = this.getWidth();
		totalHeight = this.getHeight();
		mapPanel.setBounds(0, controlPanelHeight, totalWidth - busPanelWidth, totalHeight);
		busPanel.setBounds(totalWidth - busPanelWidth, controlPanelHeight,
				busPanelWidth, totalHeight);
		timelinePanel.setBounds(2 * controlPanelHeight, 0, totalWidth - 
				2 * controlPanelHeight, controlPanelHeight);
		controlPanel.setBounds(0, 0, totalWidth, controlPanelHeight);
		busListPanel.setBounds(0, busInfoPanelHeigth, busPanelWidth, totalHeight
				- controlPanelHeight - busInfoPanelHeigth - 20);
		busScroller.setBounds(0, 0, busPanelWidth, busListPanel.getHeight());
		timeSlider.setBounds(10, 150, totalWidth - 2 * controlPanelHeight - 20, 30);
		model.updateTotalSizes(totalWidth, totalHeight, controlPanelHeight);
		model.updateWHRatio();
		view.updateMap();
	}
	
	public void updateInfoLabels() {
		actualRoadsNumberLabel.setText("Roads on the map: " + model.countRoadsInBB());
		fpsLabel.setText("fps: " + fps);
		speedLabel.setText("Time speed: " + model.timeSpeed);
		timeLabel.setText("Current time: " + (int) model.currentTime + " secs");
		busesAmountLabel.setText("All buses: " + model.buses.size());
		activeBusesAmountLabel.setText("Active buses: ");
		updateBusInfo();
	}
	
	public void updateBusInfo() {
		if (currentBus == null)
			return;
		currentBusNumLabel.setText("Bus number: *will be added*");
		currentBusPathLabel.setText("Route length: *will be added*");
		currentBusTimeLabel.setText("Time on the road: *will be added*");
	}
	
	public void updateScreen() {
		mapPanel.repaint();
		updateInfoLabels();
		infoPanel.repaint();
	}

	public void updateTime() {
		model.currentTime += model.timeSpeed * timeUpdateDelay / 1000;
		timeSlider.setValue((int) model.currentTime);
		timeSpeedSlider.setValue((int) model.timeSpeed);
		if (model.currentTime > model.maxTime)
			updateTimeTimer.stop();
	}

	public void updateSpeed() {
		String speed = speedField.getText().trim();
		try {
			double sp = Double.parseDouble(speed);
			if (Double.isNaN(sp))
				return;
			if (sp <= 0) {
				model.timeSpeed = 1;
				pause();
				return;
			}
			if (sp >= model.maxTime / 7) {
				model.timeSpeed = model.maxTime;
				return;
			}
			model.timeSpeed = sp;
		} catch (NumberFormatException exept) {
		}
	}
	
	public void pause() {
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
	
	public static void main(String[] args) {
		new GUIControl().setVisible(true);
	}

}
