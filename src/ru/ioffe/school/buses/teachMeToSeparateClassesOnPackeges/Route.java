package teachMeToSeparateClassesOnPackeges;

import java.util.Arrays;
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
	
	@Override
	public String toString() {
		return Arrays.toString(route);
	}
}
