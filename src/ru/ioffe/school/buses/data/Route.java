package ru.ioffe.school.buses.data;

import java.io.Serializable;
import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.PositionIndicator;
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

	public PositionIndicator getPositionIndicator(double startTime) {
		return new RouteIndicator(startTime);
	}

	class RouteIndicator implements PositionIndicator {
		double currentTime;
		int currentSegment;

		public RouteIndicator(double startTime) {
			currentTime = startTime;
			int L = 0, R = segments.length, M;
			while (R - L > 1) {
				M = (R + L) >> 1;
				if (segments[M].getTimeStart() > currentTime) {
					R = M;
				} else {
					L = M;
				}
			}
			currentSegment = L;
		}

		@Override
		public Point getPosition() {
			if (currentSegment == -1 || currentSegment == segments.length)
				return null;
			return segments[currentSegment].getPosition(currentTime);
		}

		private boolean isReady() {
			return currentSegment == segments.length || segments[currentSegment].getTimeEnd() >= currentTime;
		}

		@Override
		public void skipTime(double time) {
			if (time < 0)
				throw new IllegalArgumentException("Value of time to skip can't be negative");
			currentTime += time;
			while (isReady())
				currentSegment++;
		}

		@Override
		public double getCurrentTime() {
			return currentTime;
		}

		@Override
		public void setTime(double time) {
			currentTime = time;
			int L = 0, R = segments.length, M;
			while (R - L > 1) {
				M = (R + L) >> 1;
				if (segments[M].getTimeStart() > currentTime) {
					R = M;
				} else {
					L = M;
				}
			}
			currentSegment = L;
		}
	}
}
