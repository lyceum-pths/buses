package ru.ioffe.school.buses.data;



public class Night {
	final Person[] persons;
	
	public Night(Person... persons) {
		this.persons = persons;
	}
	
	public Person[] getPersons() {
		return this.persons;
	}
}
