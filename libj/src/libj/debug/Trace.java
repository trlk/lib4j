package libj.debug;

import libj.utils.Text;

public class Trace {

	// constants
	public static final int PRINT = 0;
	public static final int BEGIN = 1;
	public static final int END = 2;
	public static final int LOOP = 3;
	private static final String[] EVENT_NAMES = { "PRINT", "BEGIN", "END", "LOOP" };

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

	private static void trace(int event, String callerClass, String callerMethod) {

		if (isEnabled) {
			Log.print("%s %s.%s", EVENT_NAMES[event], callerClass, callerMethod);
		}
	}

	private static void trace(int event, String callerClass, String callerMethod, String text) {

		if (isEnabled) {
			Log.print("%s %s.%s: %s", EVENT_NAMES[event], callerClass, callerMethod, text);
		}
	}

	public static void print(String text) {
		trace(PRINT, Debug.prevClassName(), Debug.prevMethodName(), text);
	}

	public static void print(String format, Object... args) {
		trace(PRINT, Debug.prevClassName(), Debug.prevMethodName(), Text.printf(format, args));
	}

	public static void begin() {

		trace(BEGIN, Debug.prevClassName(), Debug.prevMethodName());
	}

	public static void begin(String text) {

		trace(BEGIN, Debug.prevClassName(), Debug.prevMethodName(), text);
	}

	public static void begin(String format, Object... args) {

		trace(BEGIN, Debug.prevClassName(), Debug.prevMethodName(), Text.printf(format, args));
	}

	public static void end() {

		trace(END, Debug.prevClassName(), Debug.prevMethodName());
	}

	public static void end(String text) {

		trace(END, Debug.prevClassName(), Debug.prevMethodName(), text);
	}

	public static void end(String format, Object... args) {

		trace(END, Debug.prevClassName(), Debug.prevMethodName(), Text.printf(format, args));
	}

	public static void loop() {

		trace(LOOP, Debug.prevClassName(), Debug.prevMethodName());
	}

	public static void loop(String text) {

		trace(LOOP, Debug.prevClassName(), Debug.prevMethodName(), text);
	}

	public static void loop(String format, Object... args) {

		trace(LOOP, Debug.prevClassName(), Debug.prevMethodName(), Text.printf(format, args));
	}

}
