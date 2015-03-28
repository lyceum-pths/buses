package ru.ioffe.school.buses.emulation;

import java.io.Serializable;

import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.timeManaging.TimeTable;

public class ShortReport implements Serializable {

	private static final long serialVersionUID = 1666009997974973284L;
	final Person[] persons;
	final double[] time;
	final TimeTable timeTable;
	final double fitness;
	
	public ShortReport(Person[] persons, double[] time, TimeTable timeTable) {
		this.persons = persons;
		this.time = time;
		this.timeTable = timeTable;
		double averageTime = 0;
		for (double d : time)
			averageTime += d;
		fitness = averageTime / persons.length;
	}

	public Person[] getPersons() {
		return persons;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public double[] getTime() {
		return time;
	}

	public TimeTable getTimeTable() {
		return timeTable;
	}
	
	@Override
	public ShortReport clone() {
		return new ShortReport(persons, time, timeTable);
	}
}
