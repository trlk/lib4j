package libj.debug;

public class Debug {

	public static boolean isEnabled() {
		return Log.isLoggable(Log.DEBUG);
	}

	public static boolean isDisabled() {
		return !isEnabled();
	}

	public static void setDebug(boolean debugState) {

		if (debugState) {

			if (!isEnabled()) {
				Log.setLevel(Log.DEBUG);
				Log.debug("Debugging is enabled");
			}

		} else {

			if (isEnabled()) {
				Log.debug("Debugging is disabled");
				Log.setLevel(Log.DEFAULT_LEVEL);
			}
		}
	}

	public static void enable() {
		setDebug(true);
	}

	public static void disable() {
		setDebug(false);
	}

}
