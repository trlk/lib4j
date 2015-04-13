package libj.debug;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import libj.utils.App;
import libj.utils.Text;

public class Log {

	public static PrintStream out = System.out;
	public static PrintStream err = System.err;
	public static Logger logger = Logger.getLogger(App.getMainClassName());

	public static void setOut(PrintStream stream) {
		Log.out = stream;
	}

	public static void setErr(PrintStream stream) {
		Log.err = stream;
	}

	public static void info(String text) {

		// log.log(Level.INFO, text);

		out.println(text);
	}

	public static void info(int value) {

		out.println(String.valueOf(value));
	}

	public static void info(String format, Object... args) {

		info(Text.printf(format, args));
	}

	public static void error(String text) {

		// log.log(Level.SEVERE, text);

		out.println(text);
		err.println(text);
	}

	public static void error(String format, Object... args) {

		error(Text.printf(format, args));
	}

	public static void debug(String text) {

		if (Debug.isEnabled()) {
			out.println(text);
		}
	}

	public static void debug(String format, Object... args) {

		if (Debug.isEnabled())
			debug(Text.printf(format, args));
	}

	@SuppressWarnings("all")
	public static void printMap(Map map) {

		// sort data map
		SortedSet<String> sortedKeys = new TreeSet<String>(map.keySet());

		for (String key : sortedKeys) {
			Log.info("  %s=%s", key.toString(), map.get(key).toString());
		}
	}

	@SuppressWarnings("all")
	public static void printMapList(ArrayList<Map> list) {

		Log.info("mapList (size=%d):", list.size());

		for (int i = 0; i < list.size(); i++) {
			Log.info("item[%d]:", i);
			printMap(list.get(i));
		}
	}

	public static void info(Throwable e) {

		info(Error.getText(e));
	}

	public static void debug(Throwable e) {

		if (Debug.isEnabled()) {
			debug(Error.getText(e));
		}
	}

	public static void error(Throwable e) {

		error(Error.getText(e));
	}

}
