package ru.ioffe.school.buses.emulation;

import java.io.Serializable;
import java.util.Arrays;

import ru.ioffe.school.buses.timeManaging.TimeTable;

public class Report implements Serializable {
	
	private static final long serialVersionUID = 8933468347233520920L;
	final PersonalReport[] report;
	final TimeTable timeTable;
	//here should be some important information
	final double fitness;
	
	public Report(PersonalReport[] reports, TimeTable timeTable) {
		this.report = reports;
		this.timeTable = timeTable;
		double average = 0;
		for (PersonalReport rep : reports) 
			if (rep != null)
			average += rep.getTotalTime();
		average /= reports.length;
		this.fitness = average;
	}
	
	public PersonalReport[] getReports() {
		return report;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public TimeTable getTimeTable() {
		return timeTable;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(report);
	}
}
