package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;


public class Night {
	Person[] persons;
	
	public Night(Person... persons) {
		this.persons = persons;
	}
	
	public Person[] getPersons() {
		return this.persons;
	}
}
