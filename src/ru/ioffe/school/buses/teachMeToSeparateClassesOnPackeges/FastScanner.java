package teachMeToSeparateClassesOnPackeges;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.StringTokenizer;


public class FastScanner implements AutoCloseable {
	BufferedReader reader;
	StringTokenizer tokens;

	public FastScanner() {
		Locale.setDefault(Locale.US);
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	public FastScanner(String URL) throws FileNotFoundException {
		Locale.setDefault(Locale.US);
		reader = new BufferedReader(new FileReader(new File(URL)));
	}

	public String next() {
		try {
			while (tokens == null || !tokens.hasMoreTokens())
				tokens = new StringTokenizer(reader.readLine());
		} catch (Exception e) {
			return null;
		}
		return tokens.nextToken();
	}

	public int nextInt() {
		return Integer.parseInt(next());
	}

	public long nextLong() {
		return Long.parseLong(next());
	}

	public double nextDouble() {
		return Double.parseDouble(next());
	}

	public Point nextPoint() {
		return new Point(nextDouble(), nextDouble());
	}

	public Person nextPerson() {
		return new Person(nextPoint(), nextPoint(), nextInt());
	}

	public Nigth nextNigth() {
		Person[] persons = new Person[nextInt()];
		for (int i = 0; i < persons.length; i++)
			persons[i] = nextPerson();
		return new Nigth(persons);
	}

	public void close() {
		try {
			reader.close();
		} catch (Exception e) {}
	}
}