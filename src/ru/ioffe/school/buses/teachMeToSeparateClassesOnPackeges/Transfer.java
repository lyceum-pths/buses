package teachMeToSeparateClassesOnPackeges;

import java.util.Arrays;

public class Transfer {
	int[] departure;
	int continuance; 
	Point to;
	Point from;
	int busNumber;
	
	public Transfer(int busNumber, Point from, Point to, int continuance, int... departure) {
		this.busNumber = busNumber;
		this.from = from;
		this.to = to;
		this.continuance = continuance;
		Arrays.sort(departure);
		this.departure = departure;
	}
	
	public int getNextDeparture(int current) {
		if (current > departure[departure.length - 1]) 
			return Integer.MAX_VALUE; // infinity
		int L = -1, R = departure.length - 1, M; // departure[L] < current, departure[R] >= current
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (departure[M] < current) {
				L = M;
			} else {
				R = M;
			}
		}
		return departure[R];
	}

	public int[] getDeparture() {
		return departure;
	}

	public int getContinuance() {
		return continuance;
	}
	
	public Point getFrom() {
		return from;
	}

	public Point getTo() {
		return to;
	}

	public int getBusNumber() {
		return busNumber;
	}
}
