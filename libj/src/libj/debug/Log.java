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

import libj.error.Throw;
import libj.utils.Cal;
import libj.utils.Text;

@SuppressWarnings("serial")
public class Log implements Serializable {

	// constants
	public static final int FATAL = 1; // the most serious
	public static final int ERROR = 2;
	public static final int WARN = 3;
	public static final int INFO = 4;
	public static final int DEBUG = 5;
	public static final int TRACE = 6; // the least serious
	public static final int DEFAULT = INFO;
	public static final int FORMAT_NONE = 0;
	public static final int FORMAT_BRIEF = 1;
	public static final int FORMAT_FULL = 2;
	public static final int DEFAULT_FORMAT = FORMAT_BRIEF;

	private static final String[] LEVEL_NAMES = { "NONE", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE" };
	private static final String loggerJNDI = Log.class.getName();

	// logger instance
	private static volatile Log INSTANCE;

	// private variables
	private int level = DEFAULT;
	private int outFormat = FORMAT_BRIEF;
	private int fileFormat = FORMAT_FULL;
	private String fileName;

	// streams
	transient private PrintStream outStream;
	transient private PrintStream errStream;
	transient private PrintStream fileStream;

	public static Log getInstance() {

		if (INSTANCE == null) {
			createInstance();
		}

		return INSTANCE;
	}

	private static synchronized void createInstance() {

		INSTANCE = lookup();

		if (INSTANCE == null) {
			INSTANCE = new Log();
		}

		reconf(); // apply configuration
	}

	private static Log lookup() {

		try {

			InitialContext ctx = new InitialContext();
			return (Log) ctx.lookup(loggerJNDI);

		} catch (Exception e) {
			/*suppress*/
		}

		return null;
	}

	public static synchronized void ctxbind() throws NamingException {

		InitialContext ctx = new InitialContext();

		try {

			ctx.bind(loggerJNDI, getInstance());

		} catch (NameAlreadyBoundException e) {

			ctx.rebind(loggerJNDI, getInstance());
		}
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

	public static int getLevel() {

		return getInstance().level;
	}

	public static void setLevel(int level) {

		if (getInstance().level != level) {

			getInstance().level = level;

			info("Logging level was changed to '%s'", LEVEL_NAMES[level]);
		}
	}

	public static boolean isLevel(int level) {

		return level <= getInstance().level;
	}

	public static PrintStream getOutStream() {

		return getInstance().outStream;
	}

	public static void setOutStream(PrintStream outStream) {

		Log log = getInstance();

		// outStream
		log.outStream = outStream;

		// errStream
		if (log.errStream == log.outStream) {
			log.errStream = null;
		}
	}

	public static void setOutStream(PrintStream outStream, PrintStream errStream) {

		Log log = getInstance();

		// outStream
		log.outStream = outStream;

		// errStream
		if (errStream != log.outStream) {
			log.errStream = errStream;
		} else {
			log.errStream = null;
		}
	}

	public static int getOutFormat() {

		return getInstance().outFormat;
	}

	public static void setOutFormat(int format) {

		getInstance().outFormat = format;
	}

	public static int getFileFormat() {

		return getInstance().fileFormat;
	}

	public static void setFileFormat(int format) {

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

	private static void print(PrintStream stream, String text) {

		if (stream != null) {

			try {
				stream.println(text);
			} catch (Exception e) {
				/*suppress*/
			}
		}
	}

	private static String format(int format, int level, StackTraceElement trace, String text) {

		switch (format) {
		case FORMAT_NONE: {

			return text;
		}
		case FORMAT_BRIEF: {

			return Text.printf("%s: %s", LEVEL_NAMES[level], text);
		}
		case FORMAT_FULL: {

			// full format logging
			String dateString = Cal.formatDate(Cal.now(), "yyyy-MM-dd HH:mm:ss:SSS");
			String source = Text.printf("%s:%d", trace.getClassName(), trace.getLineNumber());

			// last part of class name
			if (source.contains(".")) {
				source = Text.substr(source, source.lastIndexOf(".") + 2);
			}

			return Text.printf("%-5s %s %-13.13s %s", LEVEL_NAMES[level], dateString, source, text);
		}

		default:
			Throw.runtimeException("[%d] Unknown format", format);
		}

		return null;
	}

	private static void log(int level, StackTraceElement trace, String text) {

		// if text not empty
		if (text != null && !text.isEmpty()) {

			// get instance
			Log log = getInstance();

			// appropriate level?
			if (level <= log.level) {

				String outLine = format(log.outFormat, level, trace, text);

				// stdout
				print(log.outStream, outLine);

				// stderr
				if (log.errStream != null && level <= ERROR) {

					if (log.errStream != log.outStream) {
						print(log.errStream, outLine);
					}
				}

				// file
				if (log.fileStream != null) {

					String fileline = outLine;

					if (log.fileFormat != log.outFormat) {

						fileline = format(log.fileFormat, level, trace, text);
					}

					print(log.fileStream, fileline);
				}
			}
		}
	}

	private static void log(int level, StackTraceElement trace, String format, Object... args) {

		log(level, trace, Text.printf(format, args));
	}

	private static void log(int level, StackTraceElement trace, Throwable e) {

		log(level, trace, Error.getTextWithTrace(e));
	}

	public static void log(int level, String text) {

		log(level, Stack.prevTrace(), text);
	}

	public static void log(int level, Throwable e) {

		log(level, Stack.prevTrace(), e);
	}

	public static void log(int level, String format, Object... args) {

		log(level, Stack.prevTrace(), format, args);
	}

	public static void print(String text) {

		print(getInstance().outStream, text);
	}

	public static void print(String format, Object... args) {

		print(Text.sprintf(format, args));
	}

	public static void print(Throwable e) {

		print(Error.getTextWithTrace(e));
	}

	public static void fatal(String text) {

		log(FATAL, Stack.prevTrace(), text);
	}

	public static void fatal(String format, Object... args) {

		log(FATAL, Stack.prevTrace(), format, args);
	}

	public static void fatal(Throwable e) {

		log(FATAL, Stack.prevTrace(), e);
	}

	public static void error(String text) {

		log(ERROR, Stack.prevTrace(), text);
	}

	public static void error(String format, Object... args) {

		log(ERROR, Stack.prevTrace(), format, args);
	}

	public static void error(Throwable e) {

		log(ERROR, Stack.prevTrace(), e);
	}

	public static void warn(String text) {

		log(WARN, Stack.prevTrace(), text);
	}

	public static void warn(String format, Object... args) {

		log(WARN, Stack.prevTrace(), format, args);
	}

	public static void warn(Throwable e) {

		log(WARN, Stack.prevTrace(), e);
	}

	public static void info(String text) {

		log(INFO, Stack.prevTrace(), text);
	}

	public static void info(String format, Object... args) {

		log(INFO, Stack.prevTrace(), format, args);
	}

	public static void info(int value) {

		log(INFO, Stack.prevTrace(), String.valueOf(value));
	}

	public static void info(Throwable e) {

		log(INFO, Stack.prevTrace(), e);
	}

	public static void debug(String text) {

		log(DEBUG, Stack.prevTrace(), text);
	}

	public static void debug(String format, Object... args) {

		log(DEBUG, Stack.prevTrace(), format, args);
	}

	public static void debug(Throwable e) {

		log(DEBUG, Stack.prevTrace(), e);
	}

	public static void trace(String text) {

		log(TRACE, Stack.prevTrace(), text);
	}

	public static void trace(String format, Object... args) {

		log(TRACE, Stack.prevTrace(), format, args);
	}

	public static void trace(Throwable e) {

		log(TRACE, Stack.prevTrace(), e);
	}

	public static void trace(StackTraceElement trace, String text) {

		log(TRACE, trace, text);
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
