package ru.ioffe.school.buses.timeManaging;

import java.util.ArrayList;
import java.util.Collection;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;

public class TimeTable {
	final Bus[] buses;

	public TimeTable(Bus[] buses) {
		this.buses = buses;
	}

	public Bus[] getBuses() {
		return buses;
	}

	public TimeTable(Collection<Bus> buses) {
		this(buses.toArray(new Bus[buses.size()]));
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

	public ArrayList<PositionReport> getBusesPositionReports(double time) {
		ArrayList<PositionReport> ans = new ArrayList<>();
		for (int i = 0; i < buses.length; i++) 
			for (PositionReport report : buses[i].getPositionReports(time))
				ans.add(report);
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
