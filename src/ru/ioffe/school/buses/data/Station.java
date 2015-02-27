package ru.ioffe.school.buses.data;

import ru.ioffe.school.buses.randomGeneration.Generateable;

public class Station implements Generateable {
	final int popularity;
	final Point point;
	
	public Station(Point point) {
		this.point = point;
		this.popularity = 1;
	}
	
	public Station(Point point, int popularity) {
		this.point = point;
		this.popularity = popularity;
	}
	
	@Override
	public int getProbability() {
		return popularity;
	}
	
	public Point getPosition() {
		return point;
	}
	
	@Override
	public int hashCode() {
		return point.hashCode() * popularity;
	}
	
	public boolean equals(Station station) {
		return popularity == station.getProbability() && point.equals(station.getPosition());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Station)
			return equals((Station) obj);
		return false;
	}
}
