package ru.ioffe.school.buses.nigthGeneration;

import ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges.*;

public class NightGenerator {
	int timeStart;
	RandomObjectGenerator<InterestingPoint> interestingPointsGenerator;
	RandomObjectGenerator<House> housesGenerator;
	TimeGenerator timeGenerator;

	public NightGenerator(int timeStart, RandomObjectGenerator<InterestingPoint> interestingPointsGenerator,
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
