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
	private static boolean isEnabled = Log.isLoggable(Log.Level.TRACE);

	public static void enable() {

		isEnabled = true;
		Log.setLevel(Log.Level.TRACE);

		trace(TEXT, Stack.prevTrace(), "Tracing is enabled");
	}

	public static void disable() {

		trace(TEXT, Stack.prevTrace(), "Tracing is disabled");

		isEnabled = false;
		if (Log.isLoggable(Log.Level.TRACE)) {
			Log.setLevel(Log.DEFAULT_LEVEL);
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
			trace(event, Stack.prevTrace(), text);
		}
	}

	public static void trace(char event, Object... args) {

		if (isEnabled) {
			trace(event, Stack.prevTrace(), args);
		}
	}

	public static void tracef(char event, String format, Object... args) {

		if (isEnabled) {
			tracef(event, Stack.prevTrace(), format, args);
		}
	}

	public static void print(String text) {

		if (isEnabled) {
			trace(TEXT, Stack.prevTrace(), text);
		}
	}

	public static void printf(String format, Object... args) {

		if (isEnabled) {
			tracef(TEXT, Stack.prevTrace(), format, args);
		}
	}

	public static void point() {

		if (isEnabled) {
			trace(POINT, Stack.prevTrace());
		}
	}

	public static void point(Object... args) {

		if (isEnabled) {
			trace(POINT, Stack.prevTrace(), args);
		}
	}

	public static void point(String text) {

		if (isEnabled) {
			trace(POINT, Stack.prevTrace(), text);
		}
	}

	public static void pointf(String format, Object... args) {

		tracef(POINT, Stack.prevTrace(), format, args);
	}

	public static void stack(StackTraceElement[] stack) {

		trace(STACK, Stack.prevTrace(), Stack.toString(stack));
	}

	public static void stack() {

		StackTraceElement[] stack = new Exception().getStackTrace();

		trace(STACK, Stack.prevTrace(), Stack.toString(stack, 1));
	}

	public static void object(Object o) {

		if (isEnabled) {
			tracef(OBJECT, Stack.prevTrace(), "%s: %s", o.getClass().getSimpleName(), o.toString());
		}
	}

	public static void exception(Throwable e) {

		if (isEnabled) {
			tracef(EXCEPTION, Stack.prevTrace(), Error.getStackTrace(e));
		}
	}

}
