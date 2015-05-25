package libj.debug;

import libj.utils.Text;

public class Debug {

	private static int THIS_IDX = 1;
	private static int PREV_IDX = 2;

	public static boolean isEnabled() {
		return Log.isLevel(Log.DEBUG);
	}

	public static boolean isDisabled() {
		return !isEnabled();
	}

	public static void setDebug(boolean isEnabled) {

		if (isEnabled) {

			if (!Log.isLevel(Log.DEBUG)) {
				Log.setLevel(Log.DEBUG);
			}

			Log.debug("Debugging is enabled");

		} else {

			if (Log.isLevel(Log.DEBUG)) {
				Log.setLevel(Log.DEFAULT_LEVEL);
			}

			Log.info("Debugging is disabled");
		}
	}

	public static void enable() {
		setDebug(true);
	}

	public static void disable() {
		setDebug(false);
	}

	@SuppressWarnings("rawtypes")
	public static Class thisClass() {

		try {
			return Class.forName(new Exception().getStackTrace()[THIS_IDX].getClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static String thisClassName() {

		return new Exception().getStackTrace()[THIS_IDX].getClassName();
	}

	public static String thisMethodName() {

		return new Exception().getStackTrace()[THIS_IDX].getMethodName();
	}

	public static String thisMethodFullName() {

		StackTraceElement thisTrace = new Exception().getStackTrace()[THIS_IDX];

		return Text.printf("%s.%s", thisTrace.getClassName(), thisTrace.getMethodName());
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

	@SuppressWarnings("rawtypes")
	public static Class prevClass() {

		try {
			return Class.forName(new Exception().getStackTrace()[PREV_IDX].getClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
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
