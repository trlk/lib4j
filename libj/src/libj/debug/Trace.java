package libj.debug;

import libj.utils.Text;

public class Trace {

	// constants
	public static final int PRINT = 0;
	public static final int HELLO = 1;
	public static final int BEGIN = 2;
	public static final int END = 3;
	public static final int LOOP = 4;
<<<<<<< HEAD
	private static final String[] EVENT_NAMES = { "PRINT", "HELLO", "BEGIN", "END", "LOOP" };
=======
	private static final String[] EVENT_NAMES = { null, "HELLO", "BEGIN", "END", "LOOP" };
>>>>>>> 5c2954c8f4320c562c80aa07fc721aaf76764c38

	// variables
	private static boolean isEnabled = Log.getLevel() == Log.TRACE;

	public static void enable() {

		isEnabled = true;
	}

	public static void disable() {

		isEnabled = false;
	}

	public static boolean isEnabled() {

		return isEnabled;
	}

	private static String getSource(StackTraceElement trace) {
		return Text.printf("%s.%s:%s", trace.getClassName(), trace.getMethodName(), trace.getLineNumber());
	}

	private static void trace(int event, StackTraceElement trace) {

		if (isEnabled) {
			Log.trace("%-5s %s", EVENT_NAMES[event], getSource(trace));
		}
	}

	private static void trace(int event, StackTraceElement trace, String text) {

		if (isEnabled) {
			Log.trace("%-5s %s (%s)", EVENT_NAMES[event], getSource(trace), text);
		}
	}

<<<<<<< HEAD
	public static void print(String text) {
		trace(PRINT, Debug.prevTraceElement(), text);
	}

	public static void print(String format, Object... args) {
		trace(PRINT, Debug.prevTraceElement(), Text.printf(format, args));
	}

	public static void hello() {

		trace(HELLO, Debug.prevTraceElement());
	}

	public static void hello(String text) {
		trace(HELLO, Debug.prevTraceElement(), text);
	}

=======
	public static void hello() {

		trace(HELLO, Debug.prevTraceElement());
	}

	public static void hello(String text) {
		trace(HELLO, Debug.prevTraceElement(), text);
	}

>>>>>>> 5c2954c8f4320c562c80aa07fc721aaf76764c38
	public static void hello(String format, Object... args) {
		trace(HELLO, Debug.prevTraceElement(), Text.printf(format, args));
	}

	public static void begin() {

		trace(BEGIN, Debug.prevTraceElement());
	}

	public static void begin(String text) {

		trace(BEGIN, Debug.prevTraceElement(), text);
	}

	public static void begin(String format, Object... args) {

		trace(BEGIN, Debug.prevTraceElement(), Text.printf(format, args));
	}

	public static void end() {

		trace(END, Debug.prevTraceElement());
	}

	public static void end(String text) {

		trace(END, Debug.prevTraceElement(), text);
	}

	public static void end(String format, Object... args) {

		trace(END, Debug.prevTraceElement(), Text.printf(format, args));
	}

	public static void loop() {

		trace(LOOP, Debug.prevTraceElement());
	}

	public static void loop(String text) {

		trace(LOOP, Debug.prevTraceElement(), text);
	}

	public static void loop(String format, Object... args) {

		trace(LOOP, Debug.prevTraceElement(), Text.printf(format, args));
	}

}
