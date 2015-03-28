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
	final Segment[] route;
	final double totalTime;

	public Route(Segment... route) {
		this.route = route;
		this.totalTime = route.length == 0? 0 : route[route.length - 1].getTimeEnd() - route[0].getTimeStart();
	}

	public Segment[] getRoute() {
		return route;
	}

	public double getTotalTime() {
		return totalTime;
	}
	
	public Point getPosition(double time) {
		int L = -1, R = route.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (route[M].getTimeStart() > time) {
				R = M;
			} else {
				L = M;
			}
		}
		if (L == -1) 
			return null;
		return route[L].getPosition(time);
	}
	
	public PositionReport getPositionReport(double time) {
		int L = -1, R = route.length, M;
		while (R - L > 1) {
			M = (R + L) >> 1;
			if (route[M].getTimeStart() > time) {
				R = M;
			} else {
				L = M;
			}
		}
		if (L == -1) 
			return null;
		return route[L].getPositionReport (time);
	}

	@Override
	public String toString() {
		return Arrays.toString(route);
	}

	public PositionIndicator getPositionIndicator(double startTime) {
		return new RouteIndicator(startTime);
	}

	class RouteIndicator implements PositionIndicator {
		double currentTime;
		int currentSegment;

		public RouteIndicator(double startTime) {
			currentTime = startTime;
			int L = 0, R = route.length, M;
			while (R - L > 1) {
				M = (R + L) >> 1;
				if (route[M].getTimeStart() > currentTime) {
					R = M;
				} else {
					L = M;
				}
			}
			currentSegment = L;
		}

		@Override
		public Point getPosition() {
			if (currentSegment == -1 || currentSegment == route.length)
				return null;
			return route[currentSegment].getPosition(currentTime);
		}
		
		private boolean isReady() {
			return currentSegment == route.length || route[currentSegment].getTimeEnd() >= currentTime;
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
			int L = 0, R = route.length, M;
			while (R - L > 1) {
				M = (R + L) >> 1;
				if (route[M].getTimeStart() > currentTime) {
					R = M;
				} else {
					L = M;
				}
			}
			currentSegment = L;
		}
	}
}
