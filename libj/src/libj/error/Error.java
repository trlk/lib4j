package libj.error;

import java.io.PrintStream;

@Deprecated
public class Error {

	public static String getText(Throwable e) {

		return libj.debug.Error.getTextWithTrace(e);
	}

	public static void print(Throwable e, PrintStream stream) {

		libj.debug.Error.print(e, stream);
	}

	public static void print(Throwable e) {

		libj.debug.Error.print(e);
	}
}
