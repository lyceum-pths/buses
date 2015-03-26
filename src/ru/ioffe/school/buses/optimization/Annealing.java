package ru.ioffe.school.buses.optimization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Night;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.emulation.Emulator;
import ru.ioffe.school.buses.emulation.Report;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.routeGeneration.BusGenerator;
import ru.ioffe.school.buses.timeManaging.TimeTable;

public class Annealing {
	
	ArrayList<Road> roads;
	ArrayList<Route> peopleRoutes;
	int maxTime;
	int numOfBuses, numOfPeople;
	Random rnd;
	BusGenerator gen;
	int iter, T;
	int iterations;
	
	void optimize() {
		init();
		Bus[] buses = new Bus[numOfBuses]; // numOfBuses should not be constant, i think
		for (int i = 0; i < numOfBuses; i++)
			buses[i] = generateBus();
		Night night = generateNight();
		int thrNum = 100;
		Report lastRep = new Emulator(5, new TimeTable(buses), roads).
				startEmulation(night, thrNum);
		double start = lastRep.getFitness();
		for (int i = 0; i < iterations; i++) {
			int ind = rnd.nextInt(numOfBuses);
			Bus prev = buses[ind];
			buses[ind] = generateBus();
			Report currRep = new Emulator(5, new TimeTable(buses), roads).
					startEmulation(night, thrNum);
			double diff = lastRep.getFitness() - currRep.getFitness();
			if (diff > T) {
				System.out.println("diff > T"); // why? 
				continue;
			}
			if (T < 100){
				System.out.println("warming up"); // maybe you should save current answer
				T = Integer.MAX_VALUE;					
			}
			if (diff > 0) { // It is very strange
				double p = Math.pow(Math.E, diff / T);
				double rand = rnd.nextDouble();
				if (rand > p)
					buses[ind] = prev;
				else
					lastRep = currRep; // it is very very strange
			} else {
				buses[ind] = prev;
			}
			decrease(); // do you really need to do it like that?
		}
		double end = lastRep.getFitness();
		System.out.println("Ended annealing after " + iterations + " iterations, "
				+ "fitness at start = " + start + "; fitness in the end = " + end);
	}
	
	void decrease() {
		iter++;
		T /= iter;
	}
	
	Night generateNight() { // WHY DO YOU NOT USE NIGTH GENERATOR I WROTE?
		Person[] people = new Person[numOfPeople];
		for (int i = 0; i < numOfPeople; i++) {
			people[i] = new Person(roads.get(rnd.nextInt(roads.size())).to,
					roads.get(rnd.nextInt(roads.size())).to, 1000);
		}
		return new Night(people);
	}
	
	Bus generateBus() {
		Bus bus = null;
		while (bus == null) {
			try {
				bus = gen.generateBus(1, maxTime, 20, 0); // we should change count of buses on route
			} catch (Exception e) {}
		}
		return bus;
	}
	
	void init() {
		rnd = new Random();
		roads = new ArrayList<>();
		peopleRoutes = new ArrayList<>();
		maxTime = 43200;
		numOfBuses = 15;
		numOfPeople = 5000;
		iter = 0;
		iterations = 25;
		T = Integer.MAX_VALUE;
		File roadsFile = new File("data/generated/roads.data");
		try {
			getRoads(roadsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Road> r = new ArrayList<>();
		for (int i = 0; i < roads.size(); i++) {
			r.add(roads.get(i));
			r.add(roads.get(i).invert());
		}
		Road[] roadsForManager = new Road[r.size()];
		for (int i = 0; i < r.size(); i++) {
			roadsForManager[i] = r.get(i);			
		}
		RoadManager manager = new RoadManager(roadsForManager);
		gen = new BusGenerator(manager);
	}
	
	void getRoads(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				roads.add((Road) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
	}
	
	public static void main(String[] args) {
		new Annealing().optimize();
	}
}

