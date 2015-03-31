package ru.ioffe.school.buses.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

import ru.ioffe.school.buses.data.Segment;

@SuppressWarnings("serial")
public class GUIControl extends JFrame {
	
	GUIModel model;
	GUIView view;
	ControlAdapter adapter;
	
	DefaultListModel<String> busListModel;
	JList<String> busList;
	JScrollPane busScroller;
	
	int totalWidth, totalHeight;
	int controlPanelHeight, busPanelWidth, busInfoPanelHeigth;
	int percent, fps, timeUpdateDelay;
	int currentX, currentY;
	
	boolean busesInited, selectingRep;
	
	JPanel mapPanel, controlPanel, busPanel;
	JPanel mapControlPanel, infoPanel, timelinePanel;
	JPanel busInfoPanel, busListPanel;
	
	JButton zoomButton, unzoomButton, upButton, downButton, leftButton, rightButton;
	JButton pauseButton, updateSpeedButton;
	JSlider timeSlider, timeSpeedSlider;
	JTextField speedField;
	JLabel actualRoadsNumberLabel, fpsLabel, speedLabel, timeLabel, routesAmountLabel, activeBusesAmountLabel;
	JLabel currentBusNumLabel, currentBusPathLabel, currentBusTimeLabel;
	Timer updateScreenTimer, updateTimeTimer;
	
	JMenuBar menuBar;
	JMenu fileMenu, settingsMenu, showMenu;
	JMenu sizeMenu, busSizeMenu, personSize, crossroadsSize, openMenu;
	JMenuItem openReport, openShortReport;
	JMenuItem showPerson, showBus, showCrossroads, showWay;
	JMenuItem busVeryBig, busBig, busMedium, busSmall, busTiny;
	JMenuItem personVeryBig, personBig, personMedium, personSmall, personTiny;
	JMenuItem crossroadVeryBig, crossroadBig, crossroadMedium, crossroadSmall, crossroadTiny;
	JMenuItem exitItem, emulateItem;
	
	JFileChooser chooser;
	
	{
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws IOException {
		String inFileName = "data/generated/roads.data";
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
			model.currentBus = model.buses.get(0);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Map GUI");
		this.setPreferredSize(new Dimension(totalWidth, totalHeight));
		this.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		this.pack();
		this.setLayout(null);
		updateTimeTimer = new Timer(timeUpdateDelay, adapter);
		setPanels();
		setMenuBar();
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
	
	private void setMenuBar() {
        Font font = new Font("Verdana", Font.PLAIN, 13);
		menuBar = new JMenuBar();
        
        fileMenu = new JMenu("File");
        fileMenu.setFont(font);
        
        emulateItem = new JMenuItem("Emulate random");
        emulateItem.setFont(font);
        fileMenu.add(emulateItem);
        emulateItem.addActionListener(adapter);
         
        settingsMenu = new JMenu("Settings");
        settingsMenu.setFont(font);
         
        showMenu = new JMenu("Show");
        showMenu.setFont(font);
        settingsMenu.add(showMenu);
        
        // show menu
         
        showPerson = new JMenuItem((view.isShowPerson()? "Hide" : "Show") +  " persons");
        showPerson.setFont(font);
        showMenu.add(showPerson);
        showPerson.addActionListener(adapter);
        
        showBus = new JMenuItem((view.isShowBus()? "Hide" : "Show") +  " buses");
        showBus.setFont(font);
        showMenu.add(showBus);
        showBus.addActionListener(adapter);
        
        showCrossroads = new JMenuItem((view.isShowCrossroads()? "Hide" : "Show") +  " cross-roads");
        showCrossroads.setFont(font);
        showMenu.add(showCrossroads);
        showCrossroads.addActionListener(adapter);
        
        showWay = new JMenuItem((view.isShowWay()? "Hide" : "Show") +  " route");
        showWay.setFont(font);
        showMenu.add(showWay);
        showWay.addActionListener(adapter);
        
        // size menu
        
        sizeMenu = new JMenu("Size");
        sizeMenu.setFont(font);
        settingsMenu.add(sizeMenu);
         
        busSizeMenu = new JMenu("Buses");
        busSizeMenu.setFont(font);
        sizeMenu.add(busSizeMenu);
        
        busVeryBig = new JMenuItem("Very big");
        busVeryBig.setFont(font);
        busSizeMenu.add(busVeryBig);
        busVeryBig.addActionListener(adapter);
        
        busBig = new JMenuItem("Big");
        busBig.setFont(font);
        busSizeMenu.add(busBig);
        busBig.addActionListener(adapter);
        
        busMedium = new JMenuItem("Medium");
        busMedium.setFont(font);
        busSizeMenu.add(busMedium);
        busMedium.addActionListener(adapter);
        
        busSmall = new JMenuItem("Small");
        busSmall.setFont(font);
        busSizeMenu.add(busSmall);
        busSmall.addActionListener(adapter);
        
        busTiny = new JMenuItem("Tiny");
        busTiny.setFont(font);
        busSizeMenu.add(busTiny);
        busTiny.addActionListener(adapter);
        
        personSize = new JMenu("Persons");
        personSize.setFont(font);
        sizeMenu.add(personSize);
        
        personVeryBig = new JMenuItem("Very big");
        personVeryBig.setFont(font);
        personSize.add(personVeryBig);
        personVeryBig.addActionListener(adapter);
        
        personBig = new JMenuItem("Big");
        personBig.setFont(font);
        personSize.add(personBig);
        personBig.addActionListener(adapter);
        
        personMedium = new JMenuItem("Medium");
        personMedium.setFont(font);
        personSize.add(personMedium);
        personMedium.addActionListener(adapter);
        
        personSmall = new JMenuItem("Small");
        personSmall.setFont(font);
        personSize.add(personSmall);
        personSmall.addActionListener(adapter);
        
        personTiny = new JMenuItem("Tiny");
        personTiny.setFont(font);
        personSize.add(personTiny);
        personTiny.addActionListener(adapter);
        
        crossroadsSize = new JMenu("Crossroads");
        crossroadsSize.setFont(font);
        sizeMenu.add(crossroadsSize);
        
        crossroadVeryBig = new JMenuItem("Very big");
        crossroadVeryBig.setFont(font);
        crossroadsSize.add(crossroadVeryBig);
        crossroadVeryBig.addActionListener(adapter);
        
        crossroadBig = new JMenuItem("Big");
        crossroadBig.setFont(font);
        crossroadsSize.add(crossroadBig);
        crossroadBig.addActionListener(adapter);
        
        crossroadMedium = new JMenuItem("Medium");
        crossroadMedium.setFont(font);
        crossroadsSize.add(crossroadMedium);
        crossroadMedium.addActionListener(adapter);
        
        crossroadSmall = new JMenuItem("Small");
        crossroadSmall.setFont(font);
        crossroadsSize.add(crossroadSmall);
        crossroadSmall.addActionListener(adapter);
        
        crossroadTiny = new JMenuItem("Tiny");
        crossroadTiny.setFont(font);
        crossroadsSize.add(crossroadTiny);
        crossroadTiny.addActionListener(adapter);
        
        openMenu = new JMenu("Open");
        openMenu.setFont(font);
        fileMenu.add(openMenu);
        
        openReport = new JMenuItem("Report");
        openReport.setFont(font);
        openMenu.add(openReport);
        openReport.addActionListener(adapter);
        
        openShortReport = new JMenuItem("Short report");
        openShortReport.setFont(font);
        openMenu.add(openShortReport);
        openShortReport.addActionListener(adapter);
         
        exitItem = new JMenuItem("Exit");
        exitItem.setFont(font);
        fileMenu.add(exitItem);
        exitItem.addActionListener(adapter);
         
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
                 
        this.setJMenuBar(menuBar);
	}
	
	private void setPanels() {
		chooser = new JFileChooser();
		chooser.addActionListener(adapter);
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
		routesAmountLabel = new JLabel();
		activeBusesAmountLabel = new JLabel();
		infoPanel.add(actualRoadsNumberLabel);
		infoPanel.add(fpsLabel);
		infoPanel.add(speedLabel);
		infoPanel.add(timeLabel);
		infoPanel.add(routesAmountLabel);
		infoPanel.add(activeBusesAmountLabel);
		
		timelinePanel.setBounds(2 * controlPanelHeight, 0, totalWidth - 
				2 * controlPanelHeight, controlPanelHeight);
		timelinePanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		timelinePanel.setLayout(null);
		
		setButtons();
	}
	
	public void initBusPanel() {
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
		busList.addListSelectionListener(adapter);
		if (model.buses.size() > 0)
			busList.setSelectedIndex(0);
		busScroller = new JScrollPane(busList);
		busListPanel.add(busScroller);
		busScroller.setBounds(0, 0, busPanelWidth, busListPanel.getHeight());
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
		if (busesInited) {
			busListPanel.setBounds(0, busInfoPanelHeigth, busPanelWidth, totalHeight
					- controlPanelHeight - busInfoPanelHeigth - 20);
			busScroller.setBounds(0, 0, busPanelWidth, busListPanel.getHeight());			
		}
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
		routesAmountLabel.setText("Bus routes: " + model.buses.size());
		activeBusesAmountLabel.setText("Active buses: " + model.activeBuses);
		updateBusInfo();
	}
	
	public void updateBusInfo() {
		if (model.currentBus == null)
			return;
		currentBusNumLabel.setText("Bus number: " + (busList.getSelectedIndex() + 1));
		double len = 0;
		double a, b;
		for (Segment s : model.currentBus.getRoute().getSegments()) {
			a = Math.abs(s.getStart().getX() - s.getEnd().getX());
			b = Math.abs(s.getStart().getY() - s.getEnd().getY());
			len += Math.sqrt(a * a + b * b);
		}
		currentBusPathLabel.setText("Route length: " + ((long) len / 3600) + " km");
		currentBusTimeLabel.setText("Time on the road: " + ((int) model.currentBus.getRoute().getTotalTime()) + " s");
	}
	
	public void updateScreen() {
		mapPanel.repaint();
		updateInfoLabels();
		infoPanel.repaint();
	}

	public void updateTime() {
		if (model.currentTime > model.maxTime) {
			updateTimeTimer.stop();
			return;
		}
		model.currentTime += model.timeSpeed * timeUpdateDelay / 1000;
		timeSlider.setValue((int) model.currentTime);
		timeSpeedSlider.setValue((int) model.timeSpeed);
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
	
	public void setCurrentBus() {
		model.setCurrentBus(busList.getSelectedIndex());
		updateBusInfo();
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
