package libj.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class File {

	public static BufferedReader getBufferedReader(String fullPath) {

		try {

			return new BufferedReader(new FileReader(fullPath));

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedReader getBufferedReader(String filePath,
			String fileName) {

		return getBufferedReader(filePath.concat("\\").concat(fileName));

	}

	public static BufferedWriter getBufferedWriter(String fullPath) {

		try {

			return new BufferedWriter(new FileWriter(fullPath));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedWriter getBufferedWriter(String filePath,
			String fileName) {

		return getBufferedWriter(filePath.concat("\\").concat(fileName));

	}

}
