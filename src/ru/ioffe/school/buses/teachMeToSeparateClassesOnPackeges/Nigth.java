package ru.ioffe.school.buses.teachMeToSeparateClassesOnPackeges;


public class Nigth {
	Person[] persons;
	
	public Nigth(Person... persons) {
		this.persons = persons;
	}
	
	public Person[] getPersons() {
		return this.persons;
	}
}
