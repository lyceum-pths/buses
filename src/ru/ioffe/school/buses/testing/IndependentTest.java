package ru.ioffe.school.buses.testing;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ru.ioffe.school.buses.data.InterestingPoint;
import ru.ioffe.school.buses.data.Night;
import ru.ioffe.school.buses.data.Person;
import ru.ioffe.school.buses.data.Road;
import ru.ioffe.school.buses.emulation.Emulator;
import ru.ioffe.school.buses.emulation.ShortReport;
import ru.ioffe.school.buses.nightGeneration.TimeGenerator;
import ru.ioffe.school.buses.randomGeneration.RandomObjectGenerator;

public class IndependentTest {
	
	ArrayList<Road> roads;
	double maxTime;
	Random rnd;
	RandomObjectGenerator<InterestingPoint> poiGenerator;

	public IndependentTest(File roads, File poi, double maxTime) throws IOException {
		this.roads = new ArrayList<>();
		ArrayList<InterestingPoint> allPoi = new ArrayList<>();
		this.maxTime = maxTime;
		rnd = new Random();
		getRoads(this.roads, roads);
		getPOI(allPoi, poi);
		poiGenerator = new RandomObjectGenerator<>(allPoi.toArray(new InterestingPoint[allPoi.size()]));
	}

	Night generateNight(int numOfPeople) {
		TimeGenerator timeGenerator = new TimeGenerator(42000, 1.001, rnd.nextLong());
		Person[] people = new Person[numOfPeople];
		for (int i = 0; i < numOfPeople; i++) {
			people[i] = new Person(poiGenerator.getRandomObject(),
					roads.get(rnd.nextInt(roads.size())).to, timeGenerator.getRandomTime());
		}
		return new Night(people);
	}

	static ShortReport readRep(File file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		Object rep = oin.readObject();
		oin.close();
		if (!(rep instanceof ShortReport))
			throw new ClassNotFoundException();
		return (ShortReport) rep;
	}

	void getPOI(ArrayList<InterestingPoint> buffer, File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				buffer.add((InterestingPoint) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
	}
	
	void getRoads(ArrayList<Road> buffer, File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fis);
		try {
			while (true) {
				buffer.add((Road) oin.readObject());
			}
		} catch (Exception e) {}
		oin.close();
	}
	
	public double test(ShortReport shortReport, int numOfPeople, int threadNum) {
		Emulator emulator = new Emulator(5, shortReport.getTimeTable(), roads);
		return emulator.startFastEmulation(generateNight(numOfPeople), threadNum).getFitness();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Scanner in = new Scanner(System.in);
		System.out.println("Insert size of test");
		int persons = in.nextInt();
		System.out.println("Insert name of report to test");
		File f = new File(in.next());
		ShortReport srep = readRep(f);
		System.out.println("Current fitness = " + srep.getFitness());
		IndependentTest test = new IndependentTest(new File("data/generated/roads.data"), new File("data/generated/poi.data"), 42000);
		System.out.println("Fitness = " + test.test(srep, persons, 100));
		in.close();
	}
}

