package ru.ioffe.school.buses.gui;

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
import ru.ioffe.school.buses.data.Station;
import ru.ioffe.school.buses.emulation.Emulator;
import ru.ioffe.school.buses.emulation.Report;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.routeGeneration.BusGenerator;
import ru.ioffe.school.buses.timeManaging.Transfer;

public class Test {
	
	static ArrayList<Bus> buses;
	static ArrayList<Road> roads;
	
	
	private static void getRoads(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				roads.add((Road) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
	}
	
	public static void main(String[] args) throws IOException {
		int minTime = 1, maxTime = 43200;
		roads = new ArrayList<>(); 
		getRoads(new File("data/generated/roads.data"));
		buses = new ArrayList<>();
		ArrayList<Road> r = new ArrayList<>();
		for (int i = 0; i < roads.size(); i++) {
			r.add(roads.get(i));
//			if (!roads.get(i).isOneway)
				r.add(roads.get(i).invert());
		}
		Road[] roadsForManager = new Road[r.size()];
		for (int i = 0; i < r.size(); i++) {
			roadsForManager[i] = r.get(i);			
		}
		RoadManager manager = new RoadManager(roadsForManager);
		Random rnd = new Random();
		ArrayList<Station> stat = new ArrayList<>();
		int numofst = 10;
		for (int i = 0; i < numofst; i++) {
			stat.add(new Station(roads.get(rnd.nextInt(roads.size())).to));
		}
		Station[] stations = new Station[stat.size()];
		for (int i = 0; i < stat.size(); i++) {
			stations[i] = stat.get(i);
		}
		BusGenerator generator = new BusGenerator(manager, stations);
		int cnt = 0;
		int busesNumber = 50;
		for (int i = 0; i < busesNumber; i++) {
			try {
				buses.add(generator.generateBus(rnd.nextInt(numofst - 2) + 2, true, true, 1, maxTime));			
			} catch (Exception e) {
				System.out.println("Due to some problems with data graph is not connected");
				cnt++;
			}				
		}
		System.out.println(cnt + " buses out of " + busesNumber + " failed");
		ArrayList<Transfer> tr = new ArrayList<>();
		for (Bus bus : buses) {
			tr.addAll(bus.getTransfers());
		}
		Transfer[] transfer = new Transfer[tr.size()];
		for (int i = 0; i < tr.size(); i++) {
			transfer[i] = tr.get(i);
		}
		Emulator emul = new Emulator(stations, 0.01, transfer);
		Person[] persons = new Person[100];
		for (int i = 0; i < 100; i++) {
			persons[i] = new Person(roads.get(rnd.nextInt(roads.size())).to, 
					roads.get(rnd.nextInt(roads.size())).to, 8);
		}
		Night night = new Night(persons);
		System.out.println("Starting emulation");
		Report rep = emul.startEmulation(night, 10);
		System.out.println("Ended emulation");
		System.out.println(rep.toString());
	}
}
