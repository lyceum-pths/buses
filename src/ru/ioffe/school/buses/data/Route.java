package ru.ioffe.school.buses.data;

import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.PositionIndicator;
/**
 * This class contain information about position of object in every moment.
 * It should be used in GUI for showing object's movements.
 */
public class Route {
	Segment[] route;
	int totalTime;

	public Route(Segment[] route) {
		this.route = route;
		totalTime = 0;
		for (Segment s : route)
			totalTime += s.getTimeEnd() - s.getTimeStart();
	}

	public Segment[] getRoute() {
		return route;
	}

	public int getTotalTime() {
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

		@Override
		public void skipTime(double time) {
			if (time < 0)
				throw new IllegalArgumentException("Value of time to skip can't be negative");
			currentTime += time;
			for (; currentSegment < route.length && 
					route[currentSegment].getTimeEnd() < currentTime; currentSegment++);
		}
	}
}
