package ru.ioffe.school.buses;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import ru.ioffe.school.buses.gui.GUIControl;
import ru.ioffe.school.buses.parsing.MapParser;

/*
 * That's what you need to do if you've just cloned that project :)
 */

public class Main {
	
	static final int BUFFER = 4096;
	
	static void read(String url, File output) throws IOException {
		URL link = new URL(url);
		BufferedInputStream in = new BufferedInputStream(link.openStream());
		File arc = new File("data/map.zip");
		FileOutputStream fout = new FileOutputStream(arc);
		byte[] data = new byte[BUFFER];
		int available;
		while ((available = in.read(data)) != -1) {
			fout.write(data, 0, available);
		}
		fout.close();
		System.out.println("Download complete");
		
		FileInputStream fis = new FileInputStream(arc);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		BufferedOutputStream dest = null;
		while (zis.getNextEntry() != null) {
			System.out.println("Extracting map");
			data = new byte[BUFFER];
			FileOutputStream fos = new FileOutputStream(output);
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((available = zis.read(data)) != -1) {
				dest.write(data, 0, available);
			}
			dest.flush();
			dest.close();
		}
		zis.close();
		arc.delete();
		System.out.println("Extracted successfully");
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Hello, buses!");
		File dir = new File("data/generated/");
		dir.mkdirs();
		File f = new File("data/map.data");
		if (f.exists()) {
			System.out.println("You already have a map.data file on your computer");
		} else {
			System.out.println("You don't have a map.data file on your computer");
			System.out.println("Insert 1 to download a small map (8.4MB total, 800KB to download)");
			System.out.println("Insert 2 to download full SPb map (354MB total, 38MB to download)");
			String[] urls = {
					"https://www.dropbox.com/s/fdeacgt3ch8tqll/map1.zip?dl=1", 
					"https://www.dropbox.com/s/rcbn8dsn68hjv6a/map2.zip?dl=1"};
			Scanner in = new Scanner(System.in);
			while (in.hasNextInt()) {
				int n = in.nextInt();
				if (n == 1) {
					System.out.println("Downloading small map");
					read(urls[0], f);
					break;
				} else if (n == 2) {
					System.out.println("Downloading large map");
					read(urls[1], f);
					break;
				}
				System.out.println("Invalid input");
			}
			in.close();
		}
		System.out.println("Parsing roads from map.data file...");
		MapParser.getRoads(new File("data/map.data"), true);
		System.out.println("Running GUIControl...");
		GUIControl.main(args);
	}
}
