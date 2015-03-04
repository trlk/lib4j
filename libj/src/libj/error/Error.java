package libj.error;

import java.io.PrintStream;

import libj.utils.Text;

public class Error {

	public static String getText(Throwable e) {

		return Text.printf("%s\n%s", e.getMessage(), e.getStackTrace());
	}

	public static void print(Throwable e, PrintStream stream) {

		stream.println(getText(e));
	}

	public static void print(Throwable e) {

		print(e, System.err);
	}
}
