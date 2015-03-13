package ru.ioffe.school.buses.emulation;

import ru.ioffe.school.buses.data.BusSegment;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.data.Segment;
import ru.ioffe.school.buses.data.StraightSegment;

public class PersonalReport {
	final Route route;
	final Person person;
//	final boolean cameHome;
	final double timeByBus;
	final double timeWalk;
	final double timeWaiting;
	
	public PersonalReport(Person person, Route route) {
		this.route = route;
		this.person = person;
		double busTime = 0;
		double walkTime = 0;
		double timeWaiting = 0;
		for (Segment segment : route.getRoute()) {
			if (segment instanceof BusSegment) {
				busTime += segment.getTimeEnd() - segment.getTimeStart();
			} else if (segment instanceof StraightSegment) {
				walkTime += segment.getTimeEnd() - segment.getTimeStart();
			} else {
				timeWaiting += segment.getTimeEnd() - segment.getTimeStart();
			}
		}
		this.timeByBus = busTime;
		this.timeWalk = walkTime;
		this.timeWaiting = timeWaiting;
	}

	public Route getRoute() {
		return route;
	}

	public Person getPerson() {
		return person;
	}

	public double getBusTime() {
		return timeByBus;
	}

	public double getWalkTime() {
		return timeWalk;
	}

	public double getWaitingTime() {
		return timeWaiting;
	}
	
	public double getTotalTime() {
		return timeByBus + timeWaiting + timeWalk;
	}
	
	public boolean useBus() {
		return timeByBus != 0;
	}
}
