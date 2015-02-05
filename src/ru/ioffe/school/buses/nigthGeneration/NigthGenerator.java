package ru.ioffe.school.buses.nigthGeneration;

import ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges.*;

public class NigthGenerator {
	int timeStart;
	RandomObjectGenerator<InterestingPoint> interestingPointsGenerator;
	RandomObjectGenerator<House> housesGenerator;
	TimeGenerator timeGenerator;

	public NigthGenerator(int timeStart, RandomObjectGenerator<InterestingPoint> interestingPointsGenerator,
			RandomObjectGenerator<House> housesGenerator, TimeGenerator timeGenerator) {
		this.timeStart = timeStart;
		this.interestingPointsGenerator = interestingPointsGenerator;
		this.housesGenerator = housesGenerator;
		this.timeGenerator = timeGenerator;
	}

	public Nigth generateNigth(int countOfPeople) {
		Person[] persons = new Person[countOfPeople];
		for (int i = 0; i < countOfPeople; i++) {
			persons[i] = new Person(housesGenerator.getRandomObject(),
					interestingPointsGenerator.getRandomObject(), 
					timeGenerator.getRandomTime());
		}
		return new Nigth(persons);
	}
}
