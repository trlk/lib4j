package libj.debug;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import libj.utils.Text;

public class Error {

	public static String getMessage(Throwable e) {

		return e.getMessage();
	}

	public static String getStackTrace(Throwable e) {

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		e.printStackTrace(new PrintStream(byteStream));

		return byteStream.toString();
	}

	public static String getTextWithTrace(Throwable e) {

		return Text.sprintf("%s\n%s", getMessage(e), getStackTrace(e));
	}

	public static void print(Throwable e, PrintStream stream) {

		stream.println(getTextWithTrace(e));
	}

	public static void print(Throwable e) {

		Log.print(e);
	}
}
