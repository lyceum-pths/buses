package ru.ioffe.school.buses.randomTownGenerator;

import ru.ioffe.school.buses.data.House;
import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Road;

public class Town {
	final Road[] roads;
	final House[] houses;
	final InterestingPoint[] interestingPoints;
	
	public Town(Road[] roads, House[] houses,
			InterestingPoint[] interestingPoints) {
		this.roads = roads;
		this.houses = houses;
		this.interestingPoints = interestingPoints;
	}

	public Road[] getRoads() {
		return roads;
	}

	public House[] getHouses() {
		return houses;
	}

	public InterestingPoint[] getInterestingPoints() {
		return interestingPoints;
	}
}
