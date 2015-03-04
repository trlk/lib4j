package libj.debug;

public class Debug {

	private static boolean debug = false;

	public static boolean isEnabled() {
		return debug;
	}

	public static boolean isDisabled() {
		return !debug;
	}

	public static void setDebug(boolean value) {

		debug = value;

		if (debug)
			Log.info("Debugging is enabled");
		else
			Log.info("Debugging is disabled");
	}

	public static void enable() {
		setDebug(true);
	}

	public static void disable() {
		setDebug(false);
	}

}
