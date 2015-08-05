package libj.debug;

public class Debug {

	public static boolean isEnabled() {
		return Log.isLoggable(Log.Level.DEBUG);
	}

	public static boolean isDisabled() {
		return !isEnabled();
	}

	public static void setDebug(boolean isEnabled) {

		if (isEnabled) {

			if (!Log.isLoggable(Log.Level.DEBUG)) {
				Log.setLevel(Log.Level.DEBUG);
			}

			Log.debug("Debugging is enabled");

		} else {

			if (Log.isLoggable(Log.Level.DEBUG)) {
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

}
