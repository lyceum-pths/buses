package ru.ioffe.school.buses.data;

import java.io.Serializable;
import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.PositionReport;
/**
 * This class contain information about position of object in every moment.
 * It should be used in GUI for showing object's movements.
 */
public class Route implements Serializable {

	private static final long serialVersionUID = -530500033605674036L;
	final Segment[] segments;
	final double totalTime;

	public Route(Segment... route) {
		this.segments = route;
		this.totalTime = route.length == 0? 0 : route[route.length - 1].getTimeEnd() - route[0].getTimeStart();
	}

	public Segment[] getSegments() {
		return segments;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public Point getPosition(double time) {
		int L = -1, R = segments.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (segments[M].getTimeStart() > time) {
				R = M;
			} else {
				L = M;
			}
		}
		if (L == -1) 
			return null;
		return segments[L].getPosition(time);
	}

	public PositionReport getPositionReport(double time) {
		int L = -1, R = segments.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (segments[M].getTimeStart() > time) {
				R = M;
			} else {
				L = M;
			}
		}
		if (L == -1) 
			return null;
		return segments[L].getPositionReport (time);
	}

	@Override
	public String toString() {
		return Arrays.toString(segments);
	}
}
