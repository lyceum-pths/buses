package ru.ioffe.school.buses.testing;

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
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.routeGeneration.BusGenerator;
import ru.ioffe.school.buses.timeManaging.TimeTable;
import ru.ioffe.school.buses.timeManaging.Transfer;

public class StabilityTest {
	ArrayList<Road> roads;
	ArrayList<Route> peopleRoutes;
	int maxTime;
	int numOfBuses, numOfPeople;
	Random rnd;
	
	public void test() {
		roads = new ArrayList<>();
		peopleRoutes = new ArrayList<>();
		rnd = new Random();
		numOfBuses = 5;
		numOfPeople = 1000;
		maxTime = 43200;
		File roadsFile;
		try {
			roadsFile = new File("data/generated/roads.data");			
			getRoads(roadsFile);
		} catch (Exception e) {
			System.err.println("You need to parse roads first or roads data is corrupted");
		}
		System.out.println("Roads loaded");
		
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
		BusGenerator generator = new BusGenerator(manager);
		
		int numOfTests = 20;
		double[] fit = new double[numOfTests];
		Bus[] buses = generateBuses(generator);
		ArrayList<Transfer> tr = new ArrayList<>();
		for (Bus bus : buses) {
			for (Transfer transfer : bus.getTransfers())
				tr.add(transfer);
		}
		Transfer[] transfer = new Transfer[tr.size()];
		for (int j = 0; j < tr.size(); j++) {
			transfer[j] = tr.get(j);
		}
		Emulator emul = new Emulator(new TimeTable(buses), roads);
		for (int i = 0; i < numOfTests; i++) {
			System.out.println("Starting test " + (i + 1) + " out of " + numOfTests);
			Person[] persons = new Person[numOfPeople];
			for (int j = 0; j < numOfPeople; j++) {
				persons[j] = new Person(roads.get(rnd.nextInt(roads.size())).to,
						roads.get(rnd.nextInt(roads.size())).to, 1000);
			}
			Night night = new Night(persons);
			fit[i] = emul.startQuantumEmulation(night, 100).getFitness();
		}
		double average = 0;
		for (double d : fit)
			average += d;
		average /= numOfTests;
		double error = 0;
		for (double d : fit)
			error += (average - d) * (average - d);
		error /= numOfTests;
		System.out.println("Error = " + Math.sqrt(error));
		double maxdiff = Integer.MIN_VALUE;
		for (double d : fit)
			maxdiff = Math.max(maxdiff, Math.abs(d - average));
		System.out.println("average fitness for " + numOfPeople + " people, " + numOfBuses + 
				" buses during " + numOfTests + " tests = " + 
				average + "; dispersion = " + maxdiff);
	}
	
	public Bus[] generateBuses(BusGenerator gen) {
		ArrayList<Bus> buses = new ArrayList<>();
		while (buses.size() < numOfBuses) {
			try {
				buses.add(gen.generateBus(20, 0));
			} catch (Exception e) {
			}
		}
		Bus[] ans = new Bus[numOfBuses];
		for (int i = 0; i < numOfBuses; i++)
			ans[i] = buses.get(i); 
		
		return ans;
	}
	
	private void getRoads(File file) throws IOException {
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
		new StabilityTest().test();
	}
}
