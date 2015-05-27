package libj.utils;

import java.nio.charset.Charset;
import java.util.Properties;

import libj.debug.Debug;
import libj.debug.Log;

public class App {

	@Deprecated
	public static String thisClassName() {

		return Debug.prevClassName();
	}

	public static String thisClassFullName() {

		return Debug.prevClassFullName();
	}

	public static String thisClassSimpleName() {

		return Debug.prevClassSimpleName();
	}

	public static String thisMethodName() {

		return Debug.prevMethodName();
	}

	public static String thisMethodTrace() {

		return Debug.prevMethodTrace();
	}

	public static Thread getCurrentThread() {
		return Thread.currentThread();
	}

	public static String getThreadName(Thread thread) {
		return thread.getName();
	}

	public static String getCurrentThreadName() {
		return getThreadName(getCurrentThread());
	}

	public static String getMainClassName(Thread thread) {

		StackTraceElement[] trace = thread.getStackTrace();
		StackTraceElement main = trace[trace.length - 1];

		return main.getClassName();
	}

	public static String getMainClassName() {

		return getMainClassName(getCurrentThread());
	}

	public static StackTraceElement getTraceElement(int i) {

		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		return trace[++i];
	}

	public static Charset getDefaultCharset() {
		return Charset.defaultCharset();
	}

	public static String getDefaultCharsetName() {
		return getDefaultCharset().displayName();
	}

	public static void printHello() {
		Log.info("%s [%s]", Cal.formatDate(Cal.now(), Cal.DEFAULT_DATETIME_FORMAT), App.getMainClassName());
	}

	public static void printEnvInfo() {

		StringBuilder sb = new StringBuilder();

		sb.append("### Application environment ###\n");
		sb.append("System.properties:\n");

		Properties properties = System.getProperties();

		for (Object p : properties.keySet()) {
			sb.append(Text.printf("%s=%s\n", p, properties.get(p)));
		}

		sb.append("\nAdditional parametes:\n");

		sb.append(Text.printf("mainClassName=%s\n", getMainClassName()));
		sb.append(Text.printf("currentThreadName=%s\n", getCurrentThreadName()));
		sb.append(Text.printf("defaultCharset=%s\n", getDefaultCharsetName()));
		sb.append(Text.printf("hostName=%s\n", Net.thisHostName()));
		sb.append(Text.printf("hostAddress=%s\n", Net.thisHostAddress()));

		Log.info(sb.toString());
	}

	public static void printMemInfo() {

		int mb = 1024 * 1024;

		StringBuilder sb = new StringBuilder();

		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		sb.append(Text.printf("### Heap utilization statistics [MB] ###\n"));

		// Print used memory
		sb.append(Text.printf("Used Memory: %d\n", (runtime.totalMemory() - runtime.freeMemory()) / mb));

		// Print free memory
		sb.append(Text.printf("Free Memory: %d\n", runtime.freeMemory() / mb));

		// Print total available memory
		sb.append(Text.printf("Total Memory: %d\n", runtime.totalMemory() / mb));

		// Print Maximum available memory
		sb.append(Text.printf("Max Memory: %d\n", runtime.maxMemory() / mb));

		Log.info(sb.toString());
	}
}
