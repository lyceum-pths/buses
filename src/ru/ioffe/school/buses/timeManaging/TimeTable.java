package ru.ioffe.school.buses.timeManaging;

import java.util.ArrayList;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;

public class TimeTable {
	final Bus[] buses;

	public TimeTable(Bus[] buses) {
		this.buses = buses;
	}

	public Point getPosition(int bus, double time) {
		return buses[bus].getPosition(time);
	}
	
	public Point[] getBusesPositions(double time) {
		Point[] ans = new Point[buses.length];
		for (int i = 0; i < buses.length; i++) 
			ans[i] = buses[i].getPosition(time);
		return ans;
	}
	
	public ArrayList<Transfer> getTransfers() {
		ArrayList<Transfer> transfers = new ArrayList<>();
		for (Bus bus : buses) 
			transfers.addAll(bus.getTransfers());
		return transfers;
	}
}
