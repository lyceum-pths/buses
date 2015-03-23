package ru.ioffe.school.buses.data;

import java.util.ArrayList;
import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.PositionIndicator;
import ru.ioffe.school.buses.timeManaging.Transfer;


public class Bus {
	final Route route;
	//int number;  just like id. Do we need for it?
	final double[] begins;
	final Transfer[] transfers;

	public Bus(Route route, double[] begins) {
		this.route = route;
		this.begins = begins;
		this.transfers = generateTransfers();
	}

	public ArrayList<Point> getPosition(double time) {
		int L = -1, R = begins.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (begins[M] + route.totalTime >= time) 
				R = M;
			else
				L = M;
		}
		ArrayList<Point> points = new ArrayList<>();
		for (int i = R; i < begins.length && begins[i] <= time; i++)
			points.add(route.getPosition(time - begins[i]));
		return points;
	}

	public Route getRoute() {
		return route;
	}

	public double[] getDepartures() {
		return begins;
	}
	
	private Transfer[] generateTransfers() {
		ArrayList<Transfer> transfers = new ArrayList<>();
		double[] departures = begins.clone();
		for(Segment segment : route.getRoute()) {
			transfers.add(new Transfer(this, segment.getStart(), 
					segment.getEnd(), segment.getTimeEnd() - segment.getTimeStart(), segment.getTimeStart(), departures.clone()));
			for (int i = 0; i < departures.length; i++)
				departures[i] += segment.getTimeEnd() - segment.getTimeStart();
		}
		return transfers.toArray(new Transfer[transfers.size()]);
	}

	public Transfer[] getTransfers() {
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
		StringBuilder builder = new StringBuilder();
		builder.append("Bus: " + System.lineSeparator() +
				"   Route: " + route + System.lineSeparator() +
				"   Departure:  " + Arrays.toString(begins) + System.lineSeparator() + 
				"   Time for one cicle: " + route.getTotalTime() + System.lineSeparator() +
				"   Transfers: " + System.lineSeparator());
		for (Transfer transfer : transfers)
			builder.append("      " + transfer + System.lineSeparator());
		return builder.toString(); 
	}
}