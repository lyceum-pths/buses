package ru.ioffe.school.buses.optimization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Night;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.data.Route;
import ru.ioffe.school.buses.emulation.Emulator;
import ru.ioffe.school.buses.emulation.ShortReport;
import ru.ioffe.school.buses.graphManaging.Graph;
import ru.ioffe.school.buses.graphManaging.RoadManager;
import ru.ioffe.school.buses.nightGeneration.TimeGenerator;
import ru.ioffe.school.buses.randomGeneration.RandomObjectGenerator;
import ru.ioffe.school.buses.routeGeneration.BusGenerator;
import ru.ioffe.school.buses.timeManaging.TimeTable;

public class Annealing {
	
	public static final long seed = 293874329875698239L;
	
	ArrayList<Road> roads;
	ArrayList<InterestingPoint> poi;
	ArrayList<Route> peopleRoutes; //?
	int maxTime;
	int numOfBuses, numOfPeople;
	int thrNum;
	Random rnd;
	BusGenerator gen;
	RandomObjectGenerator<InterestingPoint> poiGenerator;
	double speedOfConvergence;
	double T;
	int iterations;
	String logpath;
	PrintWriter log;
	Scanner in;

	void optimize() {
		optimize(null);
	}
	
	void optimize(Night night) {
		init();
		if (night == null)
			night = generateNight();
		Bus[] buses = new Bus[numOfBuses];
		for (int i = 0; i < numOfBuses; i++)
			buses[i] = generateBus();
		System.out.println("Insert 0 to start new random annealing or path to one of previous reports " + 
				"\n(ex annlogs/reports/report239.rep) to continue from previous point");
		ShortReport lastRep = null;
		double start = Double.MAX_VALUE;
		double lastFit = Double.MAX_VALUE;
		Graph graph = null;
		while (in.hasNext()) {
			String line = in.next();
			if (line.equals("0")) {
				System.out.println("Starting emulation...");
				Emulator emulator = new Emulator(5, new TimeTable(buses), roads);
				lastRep = emulator.
						startQuantumEmulation(night, thrNum);
				graph = emulator.getGraph();
				start = lastRep.getFitness();
				lastFit = lastRep.getFitness();
				break;
			}
			// this part don't work now
			/*else {
				File f = new File(line);
				if (f.exists() && f.isFile()) {
					try {
						ShortReport rep = readRep(f);
						lastRep = rep;
						start = lastRep.getFitness();
						lastFit = lastRep.getFitness();
						break;
					} catch (ClassNotFoundException ce) {
					} catch (IOException ioe) {						
					}
					System.err.println("File is corrupted");
				}
			} */
		}
		try {
			writeRep(lastRep, "firstReport.srep", logpath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double bestFit = start;
		for (int i = 0; i < iterations; i++) {
			int ind = rnd.nextInt(numOfBuses);
			Bus prev = buses[ind];
			buses[ind] = generateBus();
			ShortReport currRep = new Emulator(new TimeTable(buses), graph).
					startQuantumEmulation(night, thrNum);
			double fit = currRep.getFitness();
			if (fit < bestFit) {
				bestFit = fit;
				try {
					writeRep(currRep, "bestReport.srep", logpath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			double diff = fit - lastFit;
			if (diff > 0) {
				double p = Math.exp(-diff / T);
				double rand = rnd.nextDouble();
				if (rand < p) {
					lastRep = currRep;
					lastFit = fit;
				} else {
					buses[ind] = prev;
				}
			} else {
				lastFit = fit;
				lastRep = currRep;
			}
			System.out.println("Iteration " + (i + 1) + "; T = " + T + "; curr fitness = " + fit);
			log.println("Iteration " + (i + 1) + "; T = " + T + "; curr fitness = " + fit);
			decrease();
			log.flush();
		}
		double end = lastRep.getFitness();
		System.out.println("Ended annealing after " + iterations + " iterations, "
				+ "fitness at start = " + start + "; fitness in the end = " + end);
		log.println("Ended annealing after " + iterations + " iterations, "
				+ "fitness at start = " + start + "; fitness in the end = " + end);
		log.close();
		in.close();
	}

	void decrease() {
		T /= speedOfConvergence;
	}

	Night generateNight() {
		TimeGenerator timeGenerator = new TimeGenerator(42000, 1.001, rnd.nextLong());
		Person[] people = new Person[numOfPeople];
		for (int i = 0; i < numOfPeople; i++) {
			people[i] = new Person(poiGenerator.getRandomObject(),
					roads.get(rnd.nextInt(roads.size())).to, timeGenerator.getRandomTime());
		}
		return new Night(people);
	}

	ShortReport readRep(File file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		Object rep = oin.readObject();
		oin.close();
		if (!(rep instanceof ShortReport))
			throw new ClassNotFoundException();
		return (ShortReport) rep;
	}
	
	void writeRep(ShortReport rep, String name, String path) throws IOException {
		File file = new File(path + name);
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(rep);
		oos.flush();
		oos.close();
	}

	Bus generateBus() {
		Bus bus = null;
		while (bus == null) {
			try {
				bus = gen.generateBus(2, 0);
			} catch (Exception e) {}
		}
		return bus;
	}

	void init() {
		rnd = new Random(seed);
		roads = new ArrayList<>();
		poi = new ArrayList<>();
		peopleRoutes = new ArrayList<>();
		in = new Scanner(System.in);
		maxTime = 43200;
		numOfBuses = 7;
		numOfPeople = 1000;
		speedOfConvergence = 1.008;
		iterations = 2500;
		T = 2000;
		thrNum = 100;
		logpath = "annlogs/reports/";
		File dir = new File(logpath);
		dir.mkdirs();
		try {
			log = new PrintWriter(new File("annlogs/log.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (dir.listFiles().length != 0) {
			System.out.println("You already have some report logs is annlogs/reports/ directory");
			System.out.println("Insert 0 to delete them or path to write logs in annlogs/reports/your_path");
			String s = in.next();
			if (s.equals("0")) {
				for (File f : dir.listFiles())
					f.delete();
				System.out.println("Directory cleared");
			} else {
				logpath += s;
				new File(logpath).mkdirs();
				System.out.println(logpath + " directory created");
			}
		}
		File roadsFile = new File("data/generated/roads.data");
		try {
			getRoads(roadsFile);
			getPOI(new File("data/generated/poi.data"));
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
		gen = new BusGenerator(manager, rnd.nextLong());
		poiGenerator = new RandomObjectGenerator<>(poi.toArray(new InterestingPoint[poi.size()]), rnd.nextLong());
	}

	void getPOI(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				poi.add((InterestingPoint) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
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

