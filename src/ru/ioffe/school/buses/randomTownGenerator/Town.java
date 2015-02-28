package ru.ioffe.school.buses.randomTownGenerator;

import ru.ioffe.school.buses.data.House;
import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Station;

public class Town {
	final Road[] roads;
	final Station[] stations;
	final House[] houses;
	final InterestingPoint[] interestingPoints;
	
	public Town(Road[] roads, Station[] stations, House[] houses,
			InterestingPoint[] interestingPoints) {
		this.roads = roads;
		this.stations = stations;
		this.houses = houses;
		this.interestingPoints = interestingPoints;
	}

	public Road[] getRoads() {
		return roads;
	}

	public Station[] getStations() {
		return stations;
	}

	public House[] getHouses() {
		return houses;
	}

	public InterestingPoint[] getInterestingPoints() {
		return interestingPoints;
	}
}
