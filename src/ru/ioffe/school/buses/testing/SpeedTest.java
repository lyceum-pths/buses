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

public class SpeedTest {
	ArrayList<Road> roads;
	ArrayList<Route> peopleRoutes;
	int maxTime;
	int numOfBuses, numOfPeople;
	Random rnd;
	
	public void test() {
		roads = new ArrayList<>();
		peopleRoutes = new ArrayList<>();
		rnd = new Random();
		numOfBuses = 20;
		numOfPeople = 5000;
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
		
		int numOfTests = 10;
		long time1 = 0, time2 = 0;
		for (int i = 0; i < numOfTests; i++) {
			System.out.println("Starting test " + (i + 1) + " out of " + numOfTests);
			Bus[] buses = generateBuses(generator);
			ArrayList<Transfer> tr = new ArrayList<>();
			for (Bus bus : buses) {
				for (Transfer transfer : bus.getTransfers())
					tr.add(transfer);
			}
			Transfer[] transfer = new Transfer[tr.size()];
			for (int j = 0; j < tr.size(); j++) {
				transfer[j] = tr.get(i);
			}
			Emulator emul = new Emulator(5, new TimeTable(buses), roads);
			Person[] persons = new Person[numOfPeople];
			for (int j = 0; j < numOfPeople; j++) {
				persons[j] = new Person(roads.get(rnd.nextInt(roads.size())).to,
						roads.get(rnd.nextInt(roads.size())).to, 1000);
			}
			Night night = new Night(persons);
			long start = System.currentTimeMillis();
			emul.startEmulation(night, 100);
			time1 += System.currentTimeMillis() - start;
			start = System.currentTimeMillis();
			emul.startFastEmulation(night, 100);
			time2 += System.currentTimeMillis() - start;
		}
		time1 /= numOfTests;
		time2 /= numOfTests;
		System.out.println();
		System.out.println(numOfTests + " test processed with " + numOfPeople + " people and " + numOfBuses + " buses");
		System.out.println("average time for usual emulation = " + time1 + " ms");
		System.out.println("average time for fast emulation = " + time2 + " ms");
		System.out.println("average difference = " + (time1 - time2) + " ms");
	}
	
	public Bus[] generateBuses(BusGenerator gen) {
		ArrayList<Bus> buses = new ArrayList<>();
		while (buses.size() < numOfBuses) {
			try {
				buses.add(gen.generateBus(1, maxTime, 20, 0));
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
		new SpeedTest().test();
	}
}
