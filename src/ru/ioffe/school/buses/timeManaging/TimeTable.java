package ru.ioffe.school.buses.timeManaging;

import java.util.ArrayList;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;

public class TimeTable {
	final Bus[] buses;

	public TimeTable(Bus[] buses) {
		this.buses = buses;
	}

	public ArrayList<Point> getPosition(int bus, double time) {
		return buses[bus].getPosition(time);
	}
	
	public ArrayList<Point> getBusesPositions(double time) {
		ArrayList<Point> ans = new ArrayList<>();
		for (int i = 0; i < buses.length; i++) 
			ans.addAll(buses[i].getPosition(time));
		return ans;
	}
	
	public ArrayList<Transfer> getTransfers() {
		ArrayList<Transfer> transfers = new ArrayList<>();
		for (Bus bus : buses) 
			for (Transfer transfer : bus.getTransfers())
				transfers.add(transfer);
		return transfers;
	}
}
