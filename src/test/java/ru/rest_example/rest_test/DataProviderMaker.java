package ru.rest_example.rest_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;

public class DataProviderMaker {

	static Object[][] loadCvsData(String path) throws IOException {
		List<Object[]> result = new ArrayList<>();

		try (InputStream is = ClassLoader.getSystemResourceAsStream(path);
				Reader rr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(rr)) {
			while (br.ready()) {
				String line = br.readLine();

				if (line != null & !line.isEmpty()) {
					result.add(line.split(";"));
				}
			}
		}

		return result.toArray(new Object[0][0]);
	}

	static Object[][] loadXMLData(String path) throws IOException {
		return null;
	}
}
