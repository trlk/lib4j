package libj.error;

import libj.debug.Stack;
import libj.utils.Text;

public class Assert {

	private static void raise(StackTraceElement trace) {

		throw new AssertError(trace.toString());
	}

	private static void raise(StackTraceElement trace, String message) {

		throw new AssertError("%s: %s", trace.toString(), message);
	}

	public static void check(Boolean condition) {

		if (condition != null && !condition) {
			raise(Stack.prevTrace());
		}
	}

	public static void check(Boolean condition, String message, Object... args) {

		if (condition != null && !condition) {
			raise(Stack.prevTrace(), Text.format(message, args));
		}
	}

	public static void isNotNull(Object object) {

		if (object == null) {
			raise(Stack.prevTrace(), "Object is null");
		}
	}

	public static void isNotNull(Object object, String message, Object... args) {

		if (object == null) {
			raise(Stack.prevTrace(), Text.format(message, args));
		}
	}

}
