package ru.ioffe.school.buses.timeManaging;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;

public class PositionReport {
	final Point point;
	final Bus bus;
	final int voyageNumber;
	
	public PositionReport(Point point, Bus bus, int voyageNumber) {
		this.point = point;
		this.bus = bus;
		this.voyageNumber = voyageNumber;
	}
}
