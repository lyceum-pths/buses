package ru.ioffe.school.buses.nightGeneration;

import ru.ioffe.school.buses.data.House;
import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Night;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.randomGeneration.RandomObjectGenerator;

public class NightGenerator {
	double timeStart;
	RandomObjectGenerator<InterestingPoint> interestingPointsGenerator;
	RandomObjectGenerator<House> housesGenerator;
	TimeGenerator timeGenerator;

	public NightGenerator(double timeStart, RandomObjectGenerator<InterestingPoint> interestingPointsGenerator,
			RandomObjectGenerator<House> housesGenerator, TimeGenerator timeGenerator) {
		this.timeStart = timeStart;
		this.interestingPointsGenerator = interestingPointsGenerator;
		this.housesGenerator = housesGenerator;
		this.timeGenerator = timeGenerator;
	}

	public Night generateNigth(int countOfPeople) {
		Person[] persons = new Person[countOfPeople];
		for (int i = 0; i < countOfPeople; i++) {
			persons[i] = new Person(housesGenerator.getRandomObject(),
					interestingPointsGenerator.getRandomObject(), 
					timeGenerator.getRandomTime());
		}
		return new Night(persons);
	}
}
