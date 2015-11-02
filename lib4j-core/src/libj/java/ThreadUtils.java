package libj.java;

public class ThreadUtils {

	public static void sleep(long millis) {

		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

}
