package teachMeToSeparateClassesOnPackeges;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class FileManager {
	String URL;

	public FileManager(String URL) {
		this.URL = URL;
	}

	public void save(String name, Nigth nigth) {
		try (PrintWriter out = new PrintWriter(new File(URL + name + ".nigth"))) {
			out.println(nigth.getPersons().length);
			for (Person person : nigth.getPersons())
				out.println(convert(person));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cant in this file");
		} 
	}
	
	private String convert(Point p) {
		return p.getX() + " " + p.getY();
	}
	
	private String convert(Person p) {
		return convert(p.getFrom()) + " " + convert(p.getTo()) + " " + p.getTime(); 
	}

	public Object load(String URL) {
//		try (BufferedReader reader = new BufferedReader(new FileReader(new File(URL)))) {
		try (FastScanner reader = new FastScanner()) {
			if (URL.endsWith(".nigth"))
				return reader.nextNigth();
			if (URL.endsWith(".point"))
				return reader.nextPoint();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cant read this file");
		}
		throw new IllegalArgumentException("Cant load object");
	}
}
