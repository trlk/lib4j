package libj.debug;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import libj.log.Format;
import libj.log.Level;
import libj.log.Logger;

public class Log {

	// log levels
	public static Level NONE = Level.NONE;
	public static Level FATAL = Level.FATAL;
	public static Level ERROR = Level.ERROR;
	public static Level WARN = Level.WARN;
	public static Level INFO = Level.INFO;
	public static Level DEBUG = Level.DEBUG;
	public static Level TRACE = Level.TRACE;
	public static Level DEVEL = Level.DEVEL;

	// constants
	public static final String DEFAULT_NAME = Logger.DEFAULT_NAME;
	public static final Level DEFAULT_LEVEL = Logger.DEFAULT_LEVEL;

	// logger instance
	private static volatile Logger INSTANCE;

	private Log() {
		/* this is self-create singletone, creation is not allowed */
	}

	private static Logger instance() {

		if (INSTANCE == null) {
			init(DEFAULT_NAME);
		}

		return INSTANCE;
	}

	public static synchronized Logger init(String name) {

		if (Logger.isConfigurable()) {

			INSTANCE = new Logger(name);

		} else {

			// lookup, if has no configuration
			INSTANCE = lookup(name);

			// create one, if not found
			if (INSTANCE == null) {
				INSTANCE = new Logger(name);
			}
		}

		info("Logger instance '%s' has been initialized", getName());

		return INSTANCE;
	}

	public static Logger lookup(String name) {

		String loggerJNDI = Logger.getJNDI(name);

		try {

			InitialContext ctx = new InitialContext();

			Logger instance = (Logger) ctx.lookup(loggerJNDI);

			if (instance == null) {
				throw new NullPointerException();
			}

			instance.configure(); // this is very important
			instance.info("Logger found in context: %s", instance.getJNDI());

			return instance;

		} catch (Exception e) {
			/* suppress */
		}

		return null;
	}

	public static synchronized void ctxbind() throws NamingException {

		if (INSTANCE == null) {
			throw new RuntimeException("Logger is not initialized");
		}

		InitialContext ctx = new InitialContext();

		try {

			ctx.bind(INSTANCE.getJNDI(), INSTANCE);

		} catch (NameAlreadyBoundException e) {

			ctx.rebind(INSTANCE.getJNDI(), INSTANCE);
		}

		info("Logger bind complete: instanceName:=%s, JNDI=%s", INSTANCE.getName(), INSTANCE.getJNDI());
	}

	public static Logger getInstance() {

		return instance();
	}

	public static boolean isInitialized() {

		return INSTANCE != null;
	}

	public static boolean isLoggable(Level level) {

		return instance().isLoggable(level);
	}

	public static String getName() {
		return instance().getName();
	}

	public static Level getLevel() {

		return instance().getLevel();
	}

	public static void setLevel(Level level) {

		instance().setLevel(level);
	}

	public static void setLevel(int level) {

		instance().setLevel(level);
	}

	public static PrintStream getOutStream() {

		return instance().getOutStream();
	}

	public static void setOutStream(PrintStream outStream) {

		instance().setOutStream(outStream);
	}

	public static void setOutStream(PrintStream outStream, PrintStream errStream) {

		instance().setOutStream(outStream, errStream);
	}

	public static Format getOutFormat() {

		return instance().getOutFormat();
	}

	public static void setOutFormat(Format outFormat) {

		instance().setOutFormat(outFormat);
	}

	public static Format getFileFormat() {

		return instance().getFileFormat();
	}

	public static void setFileFormat(Format fileFormat) {

		instance().setOutFormat(fileFormat);
	}

	public static String getFileName() {

		return instance().getFileName();
	}

	public static void setFileName(String fileName) {

		instance().setFileName(fileName);
	}

	public static void setForwardLog4j(boolean forwardLog4j) {

		instance().setForwardLog4j(forwardLog4j);
	}

	public static void log(Level level, Object object) {

		instance().log(level, Stack.prevTrace(), object);
	}

	public static void log(Level level, Throwable e) {

		instance().log(level, Stack.prevTrace(), e);
	}

	public static void log(Level level, String format, Object... args) {

		instance().log(level, Stack.prevTrace(), format, args);
	}

	public static void print(Object object) {

		instance().print(object);
	}

	public static void print(String format, Object... args) {

		instance().print(format, args);
	}

	public static void print(Throwable e) {

		instance().print(e);
	}

	public static void fatal(Object object) {

		instance().log(FATAL, Stack.prevTrace(), object);
	}

	public static void fatal(String format, Object... args) {

		instance().log(FATAL, Stack.prevTrace(), format, args);
	}

	public static void fatal(Throwable e) {

		instance().log(FATAL, Stack.prevTrace(), e);
	}

	public static void fatal(StackTraceElement trace, Object object) {

		instance().log(FATAL, trace, object);
	}

	public static void error(Object object) {

		instance().log(ERROR, Stack.prevTrace(), object);
	}

	public static void error(String format, Object... args) {

		instance().log(ERROR, Stack.prevTrace(), format, args);
	}

	public static void error(Throwable e) {

		instance().log(ERROR, Stack.prevTrace(), e);
	}

	public static void error(StackTraceElement trace, Object object) {

		instance().log(ERROR, trace, object);
	}

	public static void warn(Object object) {

		instance().log(WARN, Stack.prevTrace(), object);
	}

	public static void warn(String format, Object... args) {

		instance().log(WARN, Stack.prevTrace(), format, args);
	}

	public static void warn(Throwable e) {

		instance().log(WARN, Stack.prevTrace(), e);
	}

	public static void warn(StackTraceElement trace, Object object) {

		instance().log(WARN, trace, object);
	}

	public static void info(Object object) {

		instance().log(INFO, Stack.prevTrace(), object);
	}

	public static void info(String format, Object... args) {

		instance().log(INFO, Stack.prevTrace(), format, args);
	}

	public static void info(Throwable e) {

		instance().log(INFO, Stack.prevTrace(), e);
	}

	public static void info(StackTraceElement trace, Object object) {

		instance().log(INFO, trace, object);
	}

	public static void debug(Object object) {

		instance().log(DEBUG, Stack.prevTrace(), object);
	}

	public static void debug(String format, Object... args) {

		instance().log(DEBUG, Stack.prevTrace(), format, args);
	}

	public static void debug(Throwable e) {

		instance().log(DEBUG, Stack.prevTrace(), e);
	}

	public static void debug(StackTraceElement trace, Object object) {

		instance().log(DEBUG, trace, object);
	}

	public static void trace(Object object) {

		instance().log(TRACE, Stack.prevTrace(), object);
	}

	public static void trace(String format, Object... args) {

		instance().log(TRACE, Stack.prevTrace(), format, args);
	}

	public static void trace(Throwable e) {

		instance().log(TRACE, Stack.prevTrace(), e);
	}

	public static void trace(StackTraceElement trace, Object object) {

		instance().log(TRACE, trace, object);
	}

	public static void devel(Object object) {

		instance().log(DEVEL, Stack.prevTrace(), object);
	}

	public static void devel(String format, Object... args) {

		instance().log(DEVEL, Stack.prevTrace(), format, args);
	}

	public static void devel(Throwable e) {

		instance().log(DEVEL, Stack.prevTrace(), e);
	}

	@SuppressWarnings("rawtypes")
	public static void printMap(Level level, Map map) {

		instance().printMap(level, map);
	}

	@SuppressWarnings("rawtypes")
	public static void printMapList(Level level, List<Map> mapList) {

		instance().printMapList(mapList);
	}

}
