package libj.debug;


public class Debug {

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
				Log.setLevel(Log.DEFAULT);
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
