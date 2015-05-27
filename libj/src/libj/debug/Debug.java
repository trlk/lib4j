package libj.debug;

import libj.java.JUtils;
import libj.utils.Text;

public class Debug {

	private static final int thisOffset = 1;
	private static final int prevOffset = 2;

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

	public static StackTraceElement[] getStack() {

		StackTraceElement[] stack = new Exception().getStackTrace();
		StackTraceElement[] result = new StackTraceElement[stack.length - 1];
		
		System.arraycopy(stack, 1, result, 0, result.length);
		
		return result;
	}

	@SuppressWarnings("rawtypes")
	private static Class getClass(StackTraceElement trace) {

		try {
			return Class.forName(trace.getClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static int getStackIndex(StackTraceElement[] stack, String className) {

		for (int i = stack.length - 1; i >= 0; i--) {

			if (stack[i].getClassName().equals(Debug.class.getName())) {
				return i;
			}
		}

		return 0;
	}

	private static int selfStackIndex(StackTraceElement[] stack) {

		return getStackIndex(stack, Debug.class.getName());
	}

	public static StackTraceElement getTraceElement(int offset) {

		StackTraceElement[] stack = getStack();

		return stack[selfStackIndex(stack) + offset];
	}

	@Deprecated
	private static String getClassName(StackTraceElement trace) {

		return trace.getClassName();
	}

	private static String getClassFullName(StackTraceElement trace) {

		return trace.getClassName();
	}

	private static String getClassSimpleName(StackTraceElement trace) {

		return JUtils.getClassSimpleName(trace.getClassName());
	}

	private static String getMethodName(StackTraceElement trace) {

		return trace.getMethodName();
	}

	private static String getMethodFullName(StackTraceElement trace) {

		return Text.printf("%s.%s", trace.getClassName(), trace.getMethodName());
	}

	private static String getMethodTrace(StackTraceElement trace) {

		return trace.toString();
	}

	private static String getFileName(StackTraceElement trace) {

		return trace.getFileName();
	}

	private static int getLineNumber(StackTraceElement trace) {

		return trace.getLineNumber();
	}

	public static StackTraceElement thisTrace() {

		return getTraceElement(thisOffset);
	}

	@SuppressWarnings("rawtypes")
	public static Class thisClass() {

		return getClass(thisTrace());
	}

	@Deprecated
	public static String thisClassName() {

		return getClassName(thisTrace());
	}

	public static String thisClassFullName() {

		return getClassFullName(thisTrace());
	}

	public static String thisClassSimpleName() {

		return getClassSimpleName(thisTrace());
	}

	public static String thisMethodName() {

		return getMethodName(thisTrace());
	}

	public static String thisMethodFullName() {

		return getMethodFullName(thisTrace());
	}

	public static String thisMethodTrace() {

		return getMethodTrace(thisTrace());
	}

	public static String thisFileName() {

		return getFileName(thisTrace());
	}

	public static int thisLineNumber() {

		return getLineNumber(thisTrace());
	}

	public static StackTraceElement prevTrace() {

		return getTraceElement(prevOffset);
	}

	@SuppressWarnings("rawtypes")
	public static Class prevClass() {

		return getClass(prevTrace());
	}

	@Deprecated
	public static String prevClassName() {

		return getClassName(prevTrace());
	}

	public static String prevClassFullName() {

		return getClassFullName(prevTrace());
	}

	public static String prevClassSimpleName() {

		return getClassSimpleName(prevTrace());
	}

	public static String prevMethodName() {

		return getMethodName(prevTrace());
	}

	public static String prevMethodFullName() {

		return getMethodFullName(prevTrace());
	}

	public static String prevMethodTrace() {

		return getMethodTrace(prevTrace());
	}

	public static String prevFileName() {

		return getFileName(prevTrace());
	}

	public static int prevLineNumber() {

		return getLineNumber(prevTrace());
	}

	public static String formatStackTrace(StackTraceElement[] traceArray) {

		return formatStackTrace(traceArray, 0);
	}

	public static String formatStackTrace(StackTraceElement[] traceArray, int fromIndex) {

		StringBuilder sb = new StringBuilder();

		sb.append(Text.printf("StackTrace[%d]\n", traceArray.length));

		for (int i = fromIndex; i < traceArray.length; i++) {
			sb.append(Text.printf("\tat %s\n", traceArray[i].toString()));
		}

		return sb.toString();
	}

	public static void printStackTrace(StackTraceElement[] traceArray) {

		Log.print(formatStackTrace(traceArray));
	}

	public static void printStackTrace(StackTraceElement[] traceArray, int fromIndex) {

		Log.print(formatStackTrace(traceArray, fromIndex));
	}

}
