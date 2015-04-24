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

import libj.utils.Cal;
import libj.utils.Text;

@SuppressWarnings("serial")
public class Log implements Serializable {

	// constants
	public static final int FATAL = 0; // the most serious
	public static final int ERROR = 1;
	public static final int WARN = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;
	public static final int TRACE = 5; // the least serious
	private static final String[] LEVEL_NAMES = { "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE" };
	private static final String loggerJNDI = Log.class.getName();

	// logger instance
	private static volatile Log INSTANCE;

	// private variables
	private int level = INFO;
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

			info("Logging level changed to '%s'", LEVEL_NAMES[level]);
		}
	}

	public static PrintStream getOutStream() {
		return getInstance().outStream;
	}

	public static void setOutStream(PrintStream outStream) {

		getInstance().outStream = outStream;
	}

	public static PrintStream getErrStream() {

		return getInstance().errStream;
	}

	public static void setErrStream(PrintStream errStream) {

		Log log = getInstance();

		if (errStream != log.outStream) {
			log.errStream = errStream;
		} else {
			log.errStream = null;
		}
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

	private static void print(PrintStream stream, String line) {

		if (stream != null) {

			try {
				stream.println(line);
			} catch (Exception e) {
				/*suppress*/
			}
		}
	}

	private static void log(int level, StackTraceElement trace, String text) {

		// get instance
		Log log = getInstance();

		// appropriate level?
		if (level <= log.level) {

			String line = Text.printf("%s: %s", LEVEL_NAMES[level], text);

			// stdout
			print(log.outStream, line);

			// stderr
			if (log.errStream != null && level <= ERROR) {

				if (log.errStream != log.outStream) {
					print(log.errStream, line);
				}
			}

			// file
			if (log.fileStream != null) {

				// full format logging
				String dateString = Cal.formatDate(Cal.now(), "yyyy-MM-dd HH:mm:ss:SSS");
				String source = trace.getClassName();

				// last part of class name
				if (source.contains(".")) {

					source = Text.substr(source, source.lastIndexOf(".") + 2);
				}

				String fileLine = Text.printf("%-5s %s %-13.13s %s", LEVEL_NAMES[level], dateString, source, text);

				print(log.fileStream, fileLine);
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

		log(level, Debug.prevTraceElement(), text);
	}

	public static void log(int level, Throwable e) {

		log(level, Debug.prevTraceElement(), e);
	}

	public static void log(int level, String format, Object... args) {

		log(level, Debug.prevTraceElement(), format, args);
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

		log(FATAL, Debug.prevTraceElement(), text);
	}

	public static void fatal(String format, Object... args) {

		log(FATAL, Debug.prevTraceElement(), format, args);
	}

	public static void fatal(Throwable e) {

		log(FATAL, Debug.prevTraceElement(), e);
	}

	public static void error(String text) {

		log(ERROR, Debug.prevTraceElement(), text);
	}

	public static void error(String format, Object... args) {

		log(ERROR, Debug.prevTraceElement(), format, args);
	}

	public static void error(Throwable e) {

		log(ERROR, Debug.prevTraceElement(), e);
	}

	public static void warn(String text) {

		log(WARN, Debug.prevTraceElement(), text);
	}

	public static void warn(String format, Object... args) {

		log(WARN, Debug.prevTraceElement(), format, args);
	}

	public static void warn(Throwable e) {

		log(WARN, Debug.prevTraceElement(), e);
	}

	public static void info(String text) {

		log(INFO, Debug.prevTraceElement(), text);
	}

	public static void info(String format, Object... args) {

		log(INFO, Debug.prevTraceElement(), format, args);
	}

	public static void info(int value) {

		log(INFO, Debug.prevTraceElement(), String.valueOf(value));
	}

	public static void info(Throwable e) {

		log(INFO, Debug.prevTraceElement(), e);
	}

	public static void debug(String text) {

		log(DEBUG, Debug.prevTraceElement(), text);
	}

	public static void debug(String format, Object... args) {

		log(DEBUG, Debug.prevTraceElement(), format, args);
	}

	public static void debug(Throwable e) {

		log(DEBUG, Debug.prevTraceElement(), e);
	}

	public static void trace(String text) {

		log(TRACE, Debug.prevTraceElement(), text);
	}

	public static void trace(String format, Object... args) {

		log(TRACE, Debug.prevTraceElement(), format, args);
	}

	public static void trace(Throwable e) {

		log(TRACE, Debug.prevTraceElement(), e);
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
