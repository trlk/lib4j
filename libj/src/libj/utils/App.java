package libj.utils;

import java.nio.charset.Charset;
import java.util.Properties;

import libj.debug.Log;

public class App {

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

	public static String thisMethodName() {

		return new Exception().getStackTrace()[1].toString();
		// return Thread.currentThread().getStackTrace()[2].toString();
	}

	public static Charset getDefaultCharset() {
		return Charset.defaultCharset();
	}

	public static String getDefaultCharsetName() {
		return getDefaultCharset().displayName();
	}

	public static void printHello() {
		Log.info("[%s][%s][%s]",
				Date.formatDate(Date.now(), Date.defaultDateFormat),
				Date.formatDate(Date.now(), Date.defaultTimeFormat),
				App.getMainClassName());
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
		sb.append(Text.printf("hostName=%s\n", Net.getHostName()));
		sb.append(Text.printf("hostAddress=%s\n", Net.getHostAddress()));

		Log.info(sb.toString());
	}

}
