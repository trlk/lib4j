package libj.debug;

import libj.utils.Text;

public class Trace {

	// trace events
	public static final char TEXT = 'I';
	public static final char POINT = 'P';
	public static final char STACK = 'S';
	public static final char OBJECT = 'O';
	public static final char EXCEPTION = 'E';

	// enable flag
	private static boolean isEnabled = Log.getLevel() == Log.TRACE;

	public static void enable() {

		isEnabled = true;
		Log.setLevel(Log.TRACE);

		trace(TEXT, Debug.prevTrace(), "Tracing is enabled");
	}

	public static void disable() {

		trace(TEXT, Debug.prevTrace(), "Tracing is disabled");

		isEnabled = false;
		if (Log.isLevel(Log.TRACE)) {
			Log.setLevel(Log.DEFAULT);
		}
	}

	public static boolean isEnabled() {

		return isEnabled;
	}

	private static String getSource(StackTraceElement trace) {

		try {

			return Text.printf("%s.%s:%s", Class.forName(trace.getClassName()).getSimpleName(), trace.getMethodName(),
					trace.getLineNumber());

		} catch (Exception e) {
			return null;
		}
	}

	private static void trace(char event, StackTraceElement trace, String text) {

		if (isEnabled) {
			Log.trace(trace, Text.printf("%s %-20s %s", event, getSource(trace), text));
		}
	}

	private static void trace(char event, StackTraceElement trace) {

		if (isEnabled) {
			trace(event, trace, Text.EMPTY_STRING);
		}
	}

	private static void trace(char event, StackTraceElement trace, Object... args) {

		if (isEnabled) {
			String argFormat = "(" + Text.repeat("%s", ", ", args.length) + ")";
			trace(event, trace, Text.printf(argFormat, args));
		}
	}

	private static void tracef(char event, StackTraceElement trace, String format, Object... args) {

		if (isEnabled) {
			trace(event, trace, Text.printf(format, args));
		}
	}

	public static void trace(char event, String text) {

		if (isEnabled) {
			trace(event, Debug.prevTrace(), text);
		}
	}

	public static void trace(char event, Object... args) {

		if (isEnabled) {
			trace(event, Debug.prevTrace(), args);
		}
	}

	public static void tracef(char event, String format, Object... args) {

		if (isEnabled) {
			tracef(event, Debug.prevTrace(), format, args);
		}
	}

	public static void print(String text) {

		if (isEnabled) {
			trace(TEXT, Debug.prevTrace(), text);
		}
	}

	public static void printf(String format, Object... args) {

		if (isEnabled) {
			tracef(TEXT, Debug.prevTrace(), format, args);
		}
	}

	public static void point() {

		if (isEnabled) {
			trace(POINT, Debug.prevTrace());
		}
	}

	public static void point(Object... args) {

		if (isEnabled) {
			trace(POINT, Debug.prevTrace(), args);
		}
	}

	public static void point(String text) {

		if (isEnabled) {
			trace(POINT, Debug.prevTrace(), text);
		}
	}

	public static void pointf(String format, Object... args) {

		tracef(POINT, Debug.prevTrace(), format, args);
	}

	public static void stack(StackTraceElement[] stack) {

		trace(STACK, Debug.prevTrace(), Debug.formatStackTrace(stack));
	}

	public static void stack() {

		StackTraceElement[] stack = new Exception().getStackTrace();

		trace(STACK, Debug.prevTrace(), Debug.formatStackTrace(stack, 1));
	}

	public static void object(Object o) {

		if (isEnabled) {
			tracef(OBJECT, Debug.prevTrace(), "%s: %s", o.getClass().getSimpleName(), o.toString());
		}
	}

	public static void exception(Throwable e) {

		if (isEnabled) {
			tracef(EXCEPTION, Debug.prevTrace(), Error.getStackTrace(e));
		}
	}

}
