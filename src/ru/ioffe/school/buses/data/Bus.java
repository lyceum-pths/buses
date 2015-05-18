package ru.ioffe.school.buses.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.PositionReport;
import ru.ioffe.school.buses.timeManaging.Transfer;


public class Bus implements Serializable {

	private static final long serialVersionUID = 699402716610762006L;
	private static final double eps = 0.0000001; // 10^(-7)
	
	final Route route;
	final double[] begins;
	final Transfer[] transfers;

	public Bus(Route route, double[] begins) {
		this.route = route;
		this.begins = begins;
		this.transfers = generateTransfers();
		validate();
	}
	
	private void validate() {
		double cicleTime = route.getTotalTime();
		for (int i = 0; i < begins.length; i++)
			begins[i] %= cicleTime;
		for (int i = 0; i < begins.length; i++)
			if (begins[i] < 0)
				begins[i] += cicleTime;
		Arrays.sort(begins);
	}

	/**
	 * Find (later or before) voyage which begins closest to {@time}
	 * @param time
	 * @return number of nearest voyage
	 */

	public int findNearestVoyage(double time) {
		double cycleTime = route.getTotalTime();
		int approximation = (int) (Math.abs(time) / cycleTime) * begins.length;
		int error = 2 * begins.length + 1;
		int L = approximation - error, R = approximation + error, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (getDeparture(M) >= time) {
				R = M;
			} else {
				L = M;
			}
		}
		double minDist = Double.POSITIVE_INFINITY, dT;
		int bestVoyage = -1;
		for (int i = L - 1; i <= R + 1; i++) {
			dT = Math.abs(getDeparture(i) - time);
			if (dT < minDist) {
				minDist = dT;
				bestVoyage = i;
			}
		}
		return bestVoyage;
	}

//	private int findVoyage(double time) {
//		int L = -1, R = begins.length, M;
//		while (R - L > 1) {
//			M = (R + L) >> 1;
//		if (begins[M] + route.totalTime >= time) 
//			R = M;
//		else
//			L = M;
//		}
//		return R;
//	}
	
	public double nextDeparture(double time) {
		double cycleTime = route.getTotalTime();
		int approximation = (int) (Math.abs(time) / cycleTime) * begins.length;
		int error = 2 * begins.length + 1; // its enough
		int L = approximation - error, R = approximation + error, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (getDeparture(M) >= time) {
				R = M;
			} else {
				L = M;
			}
		}
		if ((time - getDeparture(L)) < eps * Math.abs(time)) // there is possible doubles fail
			return getDeparture(L);
		return getDeparture(R);
	}
	
	private double getDeparture(int number) {
		int voyageNumber = number % begins.length;
		if (voyageNumber < 0)
			voyageNumber += begins.length;
		number -= voyageNumber;
		number /= begins.length;
		return route.getTotalTime() * number + begins[voyageNumber];
	}

	public ArrayList<Point> getPosition(double time) {
		ArrayList<Point> points = new ArrayList<>();
		time %= route.getTotalTime();
		if (time < 0)
			time += route.getTotalTime();
		double localTime;
		for (int i = 0; i < begins.length; i++) {
			localTime = time - begins[i];
			if (localTime < 0)
				localTime += route.getTotalTime();
			points.add(route.getPosition(localTime));
		}
		return points;
	}

	/** This method tell you information about positions of buses at {@time}. 
	 * 	Report also contains number of active voyage and link on this bus. 
	 * 
	 * @param time
	 * @return info about positions of voyages and its numbers
	 */

	public ArrayList<PositionReport> getPositionReports(double time) {
		ArrayList<PositionReport> points = new ArrayList<>();
		for (int i = 0; i < begins.length; i++)
			points.add(new PositionReport(route.getPosition(time - begins[i]), this, i));
		return points;
	}

	public Point getPosition(double time, int voyage) {
		voyage %= begins.length;
		if (voyage < 0)
			voyage += begins.length;
		time -= begins[voyage];
		time %= route.getTotalTime();
		if (time < 0)
			time += route.getTotalTime();
		return route.getPosition(time);
	}

	public Route getRoute() {
		return route;
	}

	public double[] getDepartures() {
		return begins;
	}

	private Transfer[] generateTransfers() {
		ArrayList<Transfer> transfersList = new ArrayList<>();
		for(Segment segment : route.getSegments()) {
			transfersList.add(new Transfer(this, segment.getStart(), 
					segment.getEnd(), segment.getTimeEnd() - segment.getTimeStart(), segment.getTimeStart()));
		}
		return transfersList.toArray(new Transfer[transfersList.size()]);
	}

	public Transfer[] getTransfers() {
		return transfers;
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