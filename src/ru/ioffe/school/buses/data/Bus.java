package ru.ioffe.school.buses.data;

import java.util.ArrayList;
import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.PositionIndicator;
import ru.ioffe.school.buses.timeManaging.Transfer;


public class Bus {
	final Route route;
	//int number;  just like id. Do we need for it?
	final double[] begins;
	final Station[] busStations;
	final double[] time;

	public Bus(Route route, Station[] stations, double[] time, double[] begins) {
		this.route = route;
		this.begins = begins;
		this.busStations = stations;
		this.time = time;
	}

	public Point getPosition(double time) {
		int L = -1, R = begins.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (begins[M] > time) 
				R = M;
			else 
				L = M;
		}
		if (L == -1) 
			return null;
		return route.getPosition(time - begins[L]);
	}

	public Route getRoute() {
		return route;
	}

	public double[] getDepartures() {
		return begins;
	}

	public ArrayList<Transfer> getTransfers() {
		ArrayList<Transfer> transfers = new ArrayList<>();
		for (int j = 0; j < busStations.length - 1; j++) {
			double[] departures = new double[begins.length];
			for (int i = 0; i < departures.length; i++) 
				departures[i] = begins[i] + time[j];
			transfers.add(new Transfer(this, busStations[j].getPosition(), 
					busStations[j + 1].getPosition(), time[j + 1] - time[j], departures));
		}
		return transfers;
	}
	

	public BusIndicator getBusIndicator(double startTime) {
		return new BusIndicator(startTime);
	}

	public class BusIndicator implements PositionIndicator {

		PositionIndicator indicator;
		double currentTime;
		int currentPassage;

		private BusIndicator(double startTime) {
			indicator = route.getPositionIndicator(startTime);
			setTime(startTime);
		}

		@Override
		public Point getPosition() {
			return indicator.getPosition();
		}

		private boolean isReady() {
			return currentPassage == begins.length || currentTime <= begins[currentPassage] + route.getTotalTime();
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
			indicator.setTime(currentTime - (currentPassage < begins.length? begins[currentPassage] : Double.NEGATIVE_INFINITY));
		}

		@Override
		public void setTime(double time) {
			currentTime = time;
			int L = 0, R = begins.length, M;
			while (R - L > 1) {
				M = (R + L) >> 1;
			if (begins[M] > currentTime) {
				R = M;
			} else {
				L = M;
			}
			}
			currentPassage = L;
			indicator.setTime(time - begins[currentPassage]);
		}

		@Override
		public double getCurrentTime() {
			return currentTime;
		}
	}
	
	@Override
	public String toString() {
		return "Bus: " + System.lineSeparator() +
				"   Route: " + route + System.lineSeparator() +
				"   Departure:  " + Arrays.toString(begins) + System.lineSeparator() + 
				"   Time for one cicle: " + route.getTotalTime() + System.lineSeparator() + 
				"   Stations: " + Arrays.deepToString(busStations) + System.lineSeparator() + 
				"   at times: " + Arrays.toString(time);
	}
}