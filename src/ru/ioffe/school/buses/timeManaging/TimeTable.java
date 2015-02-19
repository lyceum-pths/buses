package ru.ioffe.school.buses.timeManaging;

import ru.ioffe.school.buses.data.Point;
import ru.ioffe.school.buses.data.Route;

public class TimeTable {
	final Route[] routes;
	final double[][] begins;

	public TimeTable(Route[] routes, double[][] begins) {
		if (routes.length != begins.length)
			throw new IllegalArgumentException("Size of arrays \"routes\" and \"begins\" must be equal");
		this.routes = routes;
		this.begins = begins;
	}

	public Point getPosition(int bus, double time) {
		int L = -1, R = begins[bus].length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (begins[bus][M] > time) 
				R = M;
			else 
				L = M;
		}
		if (L == -1) 
			return null;
		return routes[bus].getPosition(time - begins[bus][L]);
	}
	
	public BusIndicator getBusIndicator(int bus, double startTime) {
		return new BusIndicator(bus, startTime);
	}
	
	public class BusIndicator implements PositionIndicator {
		
		PositionIndicator indicator;
		double currentTime;
		int currentPassage;
		int bus;
		
		private BusIndicator(int bus, double startTime) {
			indicator = routes[bus].getPositionIndicator(startTime);
			setTime(startTime);
		}
		
		@Override
		public Point getPosition() {
			return indicator.getPosition();
		}
		
		private boolean isReady() {
			return currentPassage == begins[bus].length || currentTime <= begins[bus][currentPassage] + routes[bus].getTotalTime();
		}

		@Override
		public void skipTime(double time) { // O(n*log(n)) summary
			currentTime += time;
			if (isReady()) {
				indicator.skipTime(time);
				return;
			}
			while (!isReady())
				currentPassage++;
			indicator.setTime(currentTime - (currentPassage < begins[bus].length? begins[bus][currentPassage] : Double.NEGATIVE_INFINITY));
		}
		
		public void setTime(double time) {
			currentTime = time;
			int L = 0, R = begins[bus].length, M;
			while (R - L > 1) {
				M = (R + L) >> 1;
				if (begins[bus][M] > currentTime) {
					R = M;
				} else {
					L = M;
				}
			}
			currentPassage = L;
			indicator.setTime(time - begins[bus][currentPassage]);
		}

		@Override
		public double getCurrentTime() {
			return currentTime;
		}
	}
}
