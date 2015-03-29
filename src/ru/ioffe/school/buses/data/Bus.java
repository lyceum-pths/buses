package ru.ioffe.school.buses.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.PositionReport;
import ru.ioffe.school.buses.timeManaging.PositionIndicator;
import ru.ioffe.school.buses.timeManaging.Transfer;


public class Bus implements Serializable {
	
	private static final long serialVersionUID = 699402716610762006L;
	final Route route;
	//int number;  just like id. Do we need for it?
	final double[] begins;
	final Transfer[] transfers;

	public Bus(Route route, double[] begins) {
		this.route = route;
		this.begins = begins;
		this.transfers = generateTransfers();
	}
	
	/**
	 * Find (later or before) voyage which begins closest to {@time}
	 * @param time
	 * @return number of nearest voyage
	 */
	
	public int findNearestVoyage(double time) {
		int L = -1, R = begins.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (begins[M] > time) 
				R = M;
			else
				L = M;
		}
		double minDist = Double.POSITIVE_INFINITY, dT;
		int bestVoyage = -1;
		for (int i = Math.max(0, L); i <= Math.min(R, begins.length - 1); i++) {
			dT = Math.abs(begins[i] - time);
			if (dT < minDist) {
				minDist = dT;
				bestVoyage = i;
			}
		}
		return bestVoyage;
	}
	
	private int findVoyage(double time) {
		int L = -1, R = begins.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (begins[M] + route.totalTime >= time) 
				R = M;
			else
				L = M;
		}
		return R;
	}

	public ArrayList<Point> getPosition(double time) {
		ArrayList<Point> points = new ArrayList<>();
		for (int i = findVoyage(time); i < begins.length && begins[i] <= time; i++)
			points.add(route.getPosition(time - begins[i]));
		return points;
	}
	
	/** This method tell you information about positions of active buses at {@time}. 
	 * 	Report also contains number of active voyage and link on this bus. 
	 * 
	 * @param time
	 * @return info about positions of active voyages and its numbers
	 */
	
	public ArrayList<PositionReport> getPositionReports(double time) {
		ArrayList<PositionReport> points = new ArrayList<>();
		for (int i = findVoyage(time); i < begins.length && begins[i] <= time; i++)
			points.add(new PositionReport(route.getPosition(time - begins[i]), this, i));
		return points;
	}
	
	public Point getPosition(double time, int voyage) {
		return route.getPosition(time - begins[voyage]);
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
		for(Segment segment : route.getSegments()) {
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