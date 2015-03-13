package ru.ioffe.school.buses.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Night;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.Station;
import ru.ioffe.school.buses.emulation.Emulator;
import ru.ioffe.school.buses.emulation.PersonalReport;
import ru.ioffe.school.buses.emulation.Report;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.routeGeneration.BusGenerator;
import ru.ioffe.school.buses.timeManaging.Transfer;

public class GUIModel {
	ArrayList<Road> roads;
	ArrayList<Bus> buses;
	ArrayList<Route> peopleRoutes;
	int totalGUIWidth, totalGUIHeight, controlPanelHeight;
	double right, left, up, down;
	double minX, minY, maxX, maxY;
	private double globalZoomMin, globalZoomMax;
	BusGenerator generator;
	Bus currentBus;
	int activeBuses;
	double currentTime, timeSpeed, maxTime;
	boolean timePaused;

	public GUIModel(File roadsFile) throws IOException {
		roads = new ArrayList<>();
		buses = new ArrayList<>();
		peopleRoutes = new ArrayList<>();
		currentTime = 2;
		timeSpeed = 1;
		maxTime = 43200;
		activeBuses = 0;
		timePaused = false;
		getRoads(roadsFile);
		updateWHRatio();
		getMaxSizes();
		{
			ArrayList<Road> r = new ArrayList<>();
			for (int i = 0; i < roads.size(); i++) {
				r.add(roads.get(i));
				//				if (!roads.get(i).isOneway)
				r.add(roads.get(i).invert());
			}
			Road[] roadsForManager = new Road[r.size()];
			for (int i = 0; i < r.size(); i++) {
				roadsForManager[i] = r.get(i);			
			}
			RoadManager manager = new RoadManager(roadsForManager);
			Random rnd = new Random();
			ArrayList<Station> stat = new ArrayList<>();
			int numofst = 50;
			for (int i = 0; i < numofst; i++) {
				stat.add(new Station(roads.get(rnd.nextInt(roads.size())).to));
			}
			Station[] stations = new Station[stat.size()];
			for (int i = 0; i < stat.size(); i++) {
				stations[i] = stat.get(i);
			}
			generator = new BusGenerator(manager, stations);
			int busesNumber = 10;
			System.out.println("Generating buses...");
			System.out.print("Already generated: ");
			boolean shownThatNumber = true;
			while (buses.size() < busesNumber) {
				try {
					buses.add(generator.generateBus(rnd.nextInt((numofst - 2) >> 2) + 2, true, true, 1, maxTime));	
					shownThatNumber = false;
				} catch (Exception e) {

				}
				if (buses.size() % 5 == 0 && !shownThatNumber) {
					System.out.print(buses.size() + " ");
					shownThatNumber = true;
				}
			}
			System.out.println();
			System.out.println(busesNumber + " buses generated");
			ArrayList<Transfer> tr = new ArrayList<>();
			for (Bus bus : buses) {
				tr.addAll(bus.getTransfers());
			}
			Transfer[] transfer = new Transfer[tr.size()];
			for (int i = 0; i < tr.size(); i++) {
				transfer[i] = tr.get(i);
			}
			Emulator emul = new Emulator(5 / Math.sqrt(2), tr, roads);
			int personNumber = 10000;
			Person[] persons = new Person[personNumber];
			for (int i = 0; i < personNumber; i++) {
				persons[i] = new Person(roads.get(rnd.nextInt(roads.size())).to, 
						roads.get(rnd.nextInt(roads.size())).to, 1000);
			}
			Night night = new Night(persons);
			System.out.println("Starting emulation");
			Report rep = emul.startEmulation(night, 100);
			System.out.println("Ended emulation");
			PersonalReport[] routes = rep.getReports();
			int cnt = 0;
			for (int i = 0; i < routes.length; i++) {
				peopleRoutes.add(routes[i].getRoute());
				if (routes[i].getTotalTime() < 42000)//its Wrong!
					cnt++;
			}
			System.out.println(cnt + " out of " + personNumber + " people finally came home");
		}
	}

	public void setCurrentBus(int number) {
		if (number >= buses.size())
			throw new ArrayIndexOutOfBoundsException();
		currentBus = buses.get(number);
	}

	public void setTime(int time) {
		this.currentTime = time;
	}

	public void setTimeSpeed(int speed) {
		this.timeSpeed = speed;
	}

	public void updateWHRatio() {
		double ratio = (double) totalGUIWidth / (double) (totalGUIHeight - controlPanelHeight);
		up = down + (right - left) / ratio;
	}

	public void updateTotalSizes(int width, int heigth, int panelHeight) {
		totalGUIWidth = width;
		totalGUIHeight = heigth;
		controlPanelHeight = panelHeight;
	}

	public void moveVert(int percent) {
		double per = (double) percent / 100;
		double totalH = up - down;
		if (up + totalH * per - totalH / 2 > maxY)
			return;
		up += totalH * per;
		down += totalH * per;
	}

	public void moveVertPx(int px) {
		double totalH = up - down;
		double pxSize = totalH / totalGUIHeight;
		if (up + pxSize * px - totalH / 2 > maxY)
			return;
		up += pxSize * px;
		down += pxSize * px;
	}

	public void moveHoriz(int percent) {
		double per = (double) percent / 100;
		double totalW = right - left;
		if (right + totalW * per - totalW / 2 > maxX)
			return;
		right += totalW * per;
		left += totalW * per;
	}

	public void moveHorizPx(int px) {
		double totalW = right - left;
		double pxSize = totalW / totalGUIWidth;
		if (right + pxSize * px - totalW / 2 > maxX)
			return;
		right += pxSize * px;
		left += pxSize * px;
	}

	public void zoom(int percent) {
		double per = (double) percent / 200;
		double totalW = right - left;
		double totalH = up - down;
		if (percent > 0 && Math.min(totalH, totalW) < globalZoomMin)
			return;
		if (percent < 0 && Math.max(totalH, totalW) > globalZoomMax)
			return;
		right -= totalW * per;
		left += totalW * per;
		up -= totalH * per;
		down += totalH * per;
	}

	public int countRoadsInBB() {
		int num = 0;
		for (Road r : roads) {
			Point from = r.from;
			Point to = r.to;
			int cnt = 0;
			if (from.getY() < down || from.getY() > up ||
					from.getX() < left || from.getX() > right)
				cnt++;
			if (to.getY() < down || to.getY() > up ||
					to.getX() < left || to.getX() > right)
				cnt++;
			if (cnt < 2)
				num++;
		}

		return num;
	}

	private void getRoads(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				roads.add((Road) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
	}

	private void getMaxSizes() {
		left = Double.MAX_VALUE;
		down = Double.MAX_VALUE;
		right = Double.MIN_VALUE;
		up = Double.MIN_VALUE;
		for (Road r : roads) {
			Point p = r.from;
			if (p.getX() < left)
				left = p.getX();
			if (p.getX() > right)
				right = p.getX();
			if (p.getY() < down)
				down = p.getY();
			if (p.getY() > up)
				up = p.getY();
			p = r.to;
			if (p.getX() < left)
				left = p.getX();
			if (p.getX() > right)
				right = p.getX();
			if (p.getY() < down)
				down = p.getY();
			if (p.getY() > up)
				up = p.getY();
		}
		minX = left;
		minY = down;
		maxX = right;
		maxY = up;
		globalZoomMax = Math.max(3 * (maxX - minX), 3 * (maxY - minY));
		globalZoomMin = Math.min((maxX - minX) / 50, (maxY - minY) / 50);
	}

}
