package libj.debug;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import libj.error.RuntimeError;
import libj.utils.Cal;
import libj.utils.Text;

@SuppressWarnings("serial")
public class Log implements Serializable {

	public enum Level {
		NONE, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, DTRACE
	}

	public enum Format {
		NONE, BRIEF, FULL
	};

	// constants
	public static final Level DEFAULT_LEVEL = Level.INFO;
	public static final Format DEFAULT_FORMAT = Format.BRIEF;
	public static final String DEFAULT_INSTANCE_NAME = "DefaultInstance";

	// logger instance
	private static volatile Log INSTANCE;
	private static volatile String instanceName = DEFAULT_INSTANCE_NAME;

	// private variables
	private Level level = DEFAULT_LEVEL;
	private Format outFormat = Format.BRIEF;
	private Format fileFormat = Format.FULL;
	private String fileName;
	private boolean forwardLog4j = false;

	// streams
	transient private PrintStream outStream;
	transient private PrintStream errStream;
	transient private PrintStream fileStream;

	public static Log getInstance() {

		if (INSTANCE == null) {
			create();
		}

		return INSTANCE;
	}

	public static String getInstanceName() {
		return instanceName;
	}

	public static String getJNDI() {
		return Text.sprintf(Log.class.getName() + "." + getInstanceName());
	}

	public static synchronized void init(String instanceName) {

		Log.instanceName = instanceName;
		create();
	}

	private static synchronized void create() {

		INSTANCE = lookup();

		if (INSTANCE == null) {
			INSTANCE = new Log();
		}

		reconf(); // apply configuration
	}

	private static Log lookup() {

		try {

			InitialContext ctx = new InitialContext();
			return (Log) ctx.lookup(getJNDI());

		} catch (Exception e) {
			/*suppress*/
		}

		return null;
	}

	public static synchronized void ctxbind() throws NamingException {

		InitialContext ctx = new InitialContext();

		try {

			ctx.bind(getJNDI(), getInstance());

		} catch (NameAlreadyBoundException e) {

			ctx.rebind(getJNDI(), getInstance());
		}

		info("Logger bind complete: instanceName:=%s, JNDI=%s", getInstanceName(), getJNDI());
	}

	private static synchronized void reconf() {

		if (INSTANCE.outStream == null) {
			INSTANCE.outStream = System.out;
		}

		if (INSTANCE.errStream == null) {
			INSTANCE.errStream = System.err;
		}

		if (INSTANCE.fileName == null && INSTANCE.fileStream != null) {

			INSTANCE.fileStream.close();
			INSTANCE.fileStream = null;
		}

		if (INSTANCE.fileName != null && INSTANCE.fileStream == null) {

			try {

				info("Logging to file %s", INSTANCE.fileName);
				INSTANCE.fileStream = new PrintStream(new FileOutputStream(INSTANCE.fileName, true));

			} catch (Exception e) {

				INSTANCE.fileName = null;
				INSTANCE.fileStream = null;
				error(e);
			}
		}
	}

	public static Level getLevel() {

		return getInstance().level;
	}

	public static void setLevel(Level level) {

		if (getInstance().level != level) {

			getInstance().level = level;

			info("Logging level was changed to '%s'", level.name());
		}
	}

	public static void setLevel(int level) {

		setLevel(Level.values()[level]);
	}

	public static boolean isLoggable(Level level, Log instance) {

		return level.compareTo(instance.level) <= 0;
	}

	public static boolean isLoggable(Level level) {

		return isLoggable(level, getInstance());
	}

	public static PrintStream getOutStream() {

		return getInstance().outStream;
	}

	public static void setOutStream(PrintStream outStream) {

		Log instance = getInstance();

		// outStream
		instance.outStream = outStream;

		// errStream
		if (instance.errStream == instance.outStream) {
			instance.errStream = null;
		}
	}

	public static void setOutStream(PrintStream outStream, PrintStream errStream) {

		Log instance = getInstance();

		// outStream
		instance.outStream = outStream;

		// errStream
		if (errStream != instance.outStream) {
			instance.errStream = errStream;
		} else {
			instance.errStream = null;
		}
	}

	public static Format getOutFormat() {

		return getInstance().outFormat;
	}

	public static void setOutFormat(Format format) {

		getInstance().outFormat = format;
	}

	public static Format getFileFormat() {

		return getInstance().fileFormat;
	}

	public static void setFileFormat(Format format) {

		getInstance().fileFormat = format;
	}

	public static String getFileName() {

		return getInstance().fileName;
	}

	public static void setFileName(String fileName) {

		if (fileName == null || fileName.isEmpty()) {
			getInstance().fileName = null;
		} else {
			getInstance().fileName = fileName;
		}

		reconf(); // apply configuration
	}

	public static void setForwardLog4j(boolean forwardLog4j) {

		getInstance().forwardLog4j = forwardLog4j;
		info("Forwading to Log4j is enabled");
	}

	private static void print(PrintStream stream, String text) {

		if (stream != null) {

			try {
				stream.println(text);
			} catch (Exception e) {
				/*suppress*/
			}
		}
	}

	private static void print(PrintStream stream, Object object) {

		if (object != null) {
			print(object.toString());
		}
	}

	private static String format(Format format, Level level, StackTraceElement trace, String text) {

		switch (format) {

		case NONE: {

			return text;
		}

		case BRIEF: {

			return Text.printf("%s: %s", level.name(), text);
		}

		case FULL: {

			// full format logging
			String dateString = Cal.formatDate(Cal.now(), "yyyy-MM-dd HH:mm:ss:SSS");
			String source = Text.printf("%s:%d", trace.getClassName(), trace.getLineNumber());

			// last part of class name
			if (source.contains(".")) {
				source = Text.substr(source, source.lastIndexOf(".") + 2);
			}

			return Text.printf("%-5s %s %-13.13s %s", level.name(), dateString, source, text);
		}

		default:
			throw new RuntimeError("[%d] Unknown format", format);
		}
	}

	private static void log(Level level, StackTraceElement trace, Object object) {

		// if text not empty
		if (object != null) {

			String text = object.toString();

			if (!text.isEmpty()) {

				// get instance
				Log instance = getInstance();

				// appropriate level?
				if (isLoggable(level, instance)) {

					String formatedLine = format(instance.outFormat, level, trace, text);

					// stdout
					print(instance.outStream, formatedLine);

					// stderr
					if (instance.errStream != instance.outStream && level.compareTo(Level.ERROR) <= 0) {

						print(instance.errStream, formatedLine);
					}

					// file
					if (instance.fileStream != null) {

						if (instance.fileFormat != instance.outFormat) {

							formatedLine = format(instance.fileFormat, level, trace, text);
						}

						print(instance.fileStream, formatedLine);
					}

					// log4j
					if (instance.forwardLog4j) {

						try {

							Log4j.proxy(Log.class, trace, level, text);

						} catch (Exception e) {

							instance.forwardLog4j = false;
							error(e);
						}
					}
				}
			}
		}
	}

	private static void log(Level level, StackTraceElement trace, String format, Object... args) {

		log(level, trace, Text.printf(format, args));
	}

	private static void log(Level level, StackTraceElement trace, Throwable e) {

		log(level, trace, Error.getTextWithTrace(e));
	}

	public static void log(Level level, Object object) {

		log(level, Stack.prevTrace(), object);
	}

	public static void log(Level level, Throwable e) {

		log(level, Stack.prevTrace(), e);
	}

	public static void log(Level level, String format, Object... args) {

		log(level, Stack.prevTrace(), format, args);
	}

	public static void print(Object object) {

		print(getInstance().outStream, object);
	}

	public static void print(String format, Object... args) {

		print(Text.sprintf(format, args));
	}

	public static void print(Throwable e) {

		print(Error.getTextWithTrace(e));
	}

	public static void fatal(Object object) {

		log(Level.FATAL, Stack.prevTrace(), object);
	}

	public static void fatal(String format, Object... args) {

		log(Level.FATAL, Stack.prevTrace(), format, args);
	}

	public static void fatal(Throwable e) {

		log(Level.FATAL, Stack.prevTrace(), e);
	}

	public static void error(Object object) {

		log(Level.ERROR, Stack.prevTrace(), object);
	}

	public static void error(String format, Object... args) {

		log(Level.ERROR, Stack.prevTrace(), format, args);
	}

	public static void error(Throwable e) {

		log(Level.ERROR, Stack.prevTrace(), e);
	}

	public static void warn(Object object) {

		log(Level.WARN, Stack.prevTrace(), object);
	}

	public static void warn(String format, Object... args) {

		log(Level.WARN, Stack.prevTrace(), format, args);
	}

	public static void warn(Throwable e) {

		log(Level.WARN, Stack.prevTrace(), e);
	}

	public static void info(Object object) {

		log(Level.INFO, Stack.prevTrace(), object);
	}

	public static void info(String format, Object... args) {

		log(Level.INFO, Stack.prevTrace(), format, args);
	}

	public static void info(Throwable e) {

		log(Level.INFO, Stack.prevTrace(), e);
	}

	public static void debug(Object object) {

		log(Level.DEBUG, Stack.prevTrace(), object);
	}

	public static void debug(String format, Object... args) {

		log(Level.DEBUG, Stack.prevTrace(), format, args);
	}

	public static void debug(Throwable e) {

		log(Level.DEBUG, Stack.prevTrace(), e);
	}

	public static void trace(Object object) {

		log(Level.TRACE, Stack.prevTrace(), object);
	}

	public static void trace(String format, Object... args) {

		log(Level.TRACE, Stack.prevTrace(), format, args);
	}

	public static void trace(Throwable e) {

		log(Level.TRACE, Stack.prevTrace(), e);
	}

	public static void trace(StackTraceElement trace, Object object) {

		log(Level.TRACE, trace, object);
	}

	public static void dtrace(Object object) {

		log(Level.DTRACE, Stack.prevTrace(), object);
	}

	public static void dtrace(String format, Object... args) {

		log(Level.DTRACE, Stack.prevTrace(), format, args);
	}

	public static void dtrace(Throwable e) {

		log(Level.DTRACE, Stack.prevTrace(), e);
	}

	@SuppressWarnings("all")
	public static void printMap(Map map) {

		// sort data map
		SortedSet<String> sortedKeys = new TreeSet<String>(map.keySet());

		for (String key : sortedKeys) {
			info("  %s=%s", key.toString(), map.get(key).toString());
		}
	}

	@SuppressWarnings("all")
	public static void printMapList(ArrayList<Map> list) {

		Log.info("mapList (size=%d):", list.size());

		for (int i = 0; i < list.size(); i++) {
			info("item[%d]:", i);
			printMap(list.get(i));
		}
	}

}
