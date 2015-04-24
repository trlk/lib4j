package libj.debug;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import libj.utils.Text;

public class Error {

	public static String getStackTrace(Throwable e) {

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		e.printStackTrace(new PrintStream(byteStream));

		return byteStream.toString();
	}

	public static String getMessage(Throwable e) {

		return e.getMessage();
	}

	public static String getTextWithTrace(Throwable e) {

		return Text.printf("%s\n%s", getMessage(e), getStackTrace(e));
	}

	public static void print(Throwable e, PrintStream stream) {

		stream.println(getMessage(e));
		stream.println(getStackTrace(e));
	}

	public static void print(Throwable e) {

		print(e, System.err);
	}
}
