package ru.ioffe.school.buses.emulation;

import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.timeManaging.TimeTable;

public class ShortReport {
	final Person[] persons;
	final double[] time;
	final TimeTable timeTable;
	
	public ShortReport(Person[] persons, double[] time, TimeTable timeTable) {
		this.persons = persons;
		this.time = time;
		this.timeTable = timeTable;
	}

	public Person[] getPersons() {
		return persons;
	}

	public double[] getTime() {
		return time;
	}

	public TimeTable getTimeTable() {
		return timeTable;
	}
}
