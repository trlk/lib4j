package libj.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import libj.debug.Debug;
import libj.debug.Log;

/*
 * addOption("u", "user", true, "User");
 * addOption("p", "password", true, "Password");
 * 
 */

public class Cmd {

	private static CommandLine cmd;
	private static Options options = new Options();
	private static Map<Character, String> defaults = new HashMap<Character, String>();

	public static synchronized void addKey(char opt, String longOpt, String description) {

		Option option = new Option(String.valueOf(opt), longOpt, false,
				description);

		options.addOption(option);
	}

	public static synchronized void addOption(char opt, String longOpt, String description,
			String defaultValue) {

		Option option = new Option(String.valueOf(opt), longOpt, true,
				description);

		option.setRequired(defaultValue == null);

		if (defaultValue != null)
			defaults.put(opt, defaultValue);

		options.addOption(option);
	}

	public static synchronized void addOption(char opt, String longOpt, String description) {

		addOption(opt, longOpt, description, null);
	}

	public static synchronized void parse(String[] args) {

		CommandLineParser parser = new GnuParser();
		// CommandLineParser parser = new PosixParser();

		try {

			// parse
			cmd = parser.parse(options, args);

			Log.debug("command line parsed");

			if (Debug.isEnabled()) {
				printValues();
			}

			// check
			/*
			 * if (check) {
			 * 
			 * for (Option o : cmd.getOptions()) {
			 * 
			 * if (o.hasArg() && o.getValue() == null) {
			 * 
			 * throw new MissingOptionException(Text.printf( "%s not specified",
			 * o.getDescription())); } }
			 * 
			 * Log.debug("command line checked"); }
			 */

		} catch (Exception e) {

			printUsage();
			throw new RuntimeException(e);
		}
	}

	public static void printUsage() {

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(App.getMainClassName(), options);
	}

	public static void printValues() {

		Log.debug("parsed command line:");

		Option[] optionsArr = getOptions();

		for (Option o : optionsArr) {

			if (cmd.hasOption(o.getOpt())) {
				Log.info("%s=%s", o.getLongOpt(), getOptionValue(o));
			} else {
				Log.info("%s=[%s]", o.getLongOpt(), getDefaultValue(o));
			}
		}
	}

	@SuppressWarnings("all")
	public static Option[] getOptions() {

		Collection<Option> opts = options.getOptions();

		return opts.toArray(new Option[opts.size()]);
	}

	public static Option getOption(char opt) {

		return options.getOption(String.valueOf(opt));
	}

	public static Option getOption(String longOpt) {

		Option[] arr = getOptions();

		for (int i = 0; i < arr.length; i++) {

			if (arr[i].getLongOpt().equals(longOpt)) {
				return arr[i];
			}
		}

		Log.info("option not found: %s", longOpt);

		return null;
	}

	public static boolean hasKey(Option o) {

		return cmd.hasOption(o.getOpt());
	}

	public static boolean hasKey(char opt) {

		return cmd.hasOption(opt);
	}

	public static boolean hasKey(String longOpt) {

		return hasKey(getOption(longOpt));
	}

	public static String getDefaultValue(Option o) {

		return defaults.get(o.getOpt().charAt(0));
	}

	public static String getDefaultValue(char opt) {

		return defaults.get(opt);
	}

	public static String getDefaultValue(String longOpt) {

		return getDefaultValue(getOption(longOpt));
	}

	public static String getOptionValue(Option o) {

		return cmd.getOptionValue(o.getOpt(), getDefaultValue(o));
	}

	public static String getOptionValue(char opt) {

		return cmd.getOptionValue(opt, getDefaultValue(opt));
	}

	public static String getOptionValue(String longOpt) {

		return getOptionValue(getOption(longOpt));
	}
}
