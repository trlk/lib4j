package libj.debug;

public class Debug {

	private static boolean debug = false;
	private static int THIS_IDX = 1;
	private static int PREV_IDX = 2;

	public static boolean isEnabled() {
		return debug;
	}

	public static boolean isDisabled() {
		return !debug;
	}

	public static void setDebug(boolean value) {

		debug = value;

		if (debug) {
			Log.debug("Debugging is enabled");
		} else {
			Log.info("Debugging is disabled");
		}
	}

	public static void enable() {
		setDebug(true);
	}

	public static void disable() {
		setDebug(false);
	}

	public static String thisClassName() {

		return new Exception().getStackTrace()[THIS_IDX].getClassName();
	}

	public static String thisMethodName() {

		return new Exception().getStackTrace()[THIS_IDX].getMethodName();
	}

	public static String thisMethodTrace() {

		return new Exception().getStackTrace()[THIS_IDX].toString();
	}

	public static StackTraceElement thisTraceElement() {

		return new Exception().getStackTrace()[THIS_IDX];
	}

	public static String thisFileName() {

		return new Exception().getStackTrace()[THIS_IDX].getFileName();
	}

	public static int thisLineNumber() {

		return new Exception().getStackTrace()[THIS_IDX].getLineNumber();
	}

	public static String prevClassName() {

		return new Exception().getStackTrace()[PREV_IDX].getClassName();
	}

	public static String prevMethodName() {

		return new Exception().getStackTrace()[PREV_IDX].getMethodName();
	}

	public static String prevMethodTrace() {

		return new Exception().getStackTrace()[PREV_IDX].toString();
	}

	public static StackTraceElement prevTraceElement() {

		return new Exception().getStackTrace()[PREV_IDX];
	}

	public static String prevFileName() {

		return new Exception().getStackTrace()[PREV_IDX].getFileName();
	}

	public static int prevLineNumber() {

		return new Exception().getStackTrace()[PREV_IDX].getLineNumber();
	}

}
