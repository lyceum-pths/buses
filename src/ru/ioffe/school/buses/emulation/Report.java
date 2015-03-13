package ru.ioffe.school.buses.emulation;

import java.util.Arrays;

public class Report {
	final PersonalReport[] report;
	//here should be some important information
	final double fitness;
	
	public Report(PersonalReport[] reports) {
		this.report = reports;
		double average = 0;
		for (PersonalReport report : reports) 
			if (report != null)
			average += report.getTotalTime();
		average /= reports.length;
		this.fitness = average;
	}
	
	public PersonalReport[] getReports() {
		return report;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(report);
	}
}
