package libj.utils;

/**
 * Text functions
 * 
 * @author Taras Lyuklyanchuk
 * 
 */

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import libj.error.Throw;

public class Text {

	public static final String EMPTY_STRING = new String();
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static String nvl(String s0, String s1) {

		if (s0 != null && !s0.isEmpty()) {
			return s0;
		} else if (s1 != null) {
			return s1;
		} else {
			return EMPTY_STRING;
		}
	}

	public static String format(String format, Object... args) {

		return String.format(format, args);
	}

	@Deprecated
	public static void printf(String format, Object... args) {

		System.out.print(format(format, args));
	}

	@Deprecated
	public static void printlnf(String format, Object... args) {

		System.out.println(format(format, args));
	}

	public static String sprintf(String format, Object... args) {

		return format(format, args);
	}

	public static String toUpperCamelCase(String text) {

		if (text == null || text.length() == 0) {
			return text;
		}

		char[] chars = text.toCharArray();

		for (int i = 0; i < chars.length; i++) {

			if (!Character.isLetterOrDigit(chars[i])) {
				chars[i] = '-';
			}
		}

		text = new String(chars);

		String[] w = text.split("-"); // split on "-"

		ArrayList<String> words = new ArrayList<String>();

		for (int i = 0; i < w.length; i++) {

			if (w[i].length() != 0) {
				words.add(w[i]);
			}
		}

		StringBuilder sb = new StringBuilder(words.size());

		for (int i = 0; i < words.size(); i++) // skip first
		{

			sb.append(words.get(i).substring(0, 1).toUpperCase());

			if (words.get(0).length() > 1) {
				sb.append(words.get(i).substring(1));
			}
		}

		return sb.toString(); // join
	}

	public static String toLowerCamelCase(String text) {

		if (text == null || text.length() == 0) {
			return text;
		}

		char[] result = toUpperCamelCase(text).toCharArray();
		result[0] = Character.toLowerCase(result[0]);

		return new String(result);
	}

	public static String substr(String line, Integer pos) {

		try {

			Integer beginIndex = 0;

			if (pos > 0) {
				beginIndex = pos - 1;
			} else if (pos < 0) {
				beginIndex = line.length() + pos;
			} else {
				Throw.runtimeException("Invalid argument value");
			}

			Integer endIndex = line.length();

			if (beginIndex > endIndex) {
				Throw.runtimeException("Invalid argument value");
			}

			return line.substring(beginIndex, endIndex);

		} catch (Exception e) {
			return new String();
		}
	}

	public static String substr(String line, Integer pos, Integer len) {

		try {

			Integer beginIndex = pos - 1;
			Integer endIndex = pos + len - 1;

			if (endIndex > line.length()) {
				endIndex = line.length();
			}

			return line.substring(beginIndex, endIndex);

		} catch (Exception e) {
			return new String();
		}
	}

	public static String substr2(String line, Integer pos, Integer len) {
		return substr(line, pos, len).trim();
	}

	public static String crop(String line, Integer len) {
		return line.substring(0, len);
	}

	public static byte[] getBytes(String text) {
		return text.getBytes();
	}

	public static byte[] getBytes(String text, String charset) {
		return text.getBytes(Charset.forName(charset));
	}

	public static String getHexString(byte[] bytes) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {

			if (i != 0 && i % 16 == 0) {
				sb.append("\n");
			}

			sb.append(format("%2x ", bytes[i]).toUpperCase());
		}

		return sb.toString();
	}

	public static String[] split(String text, String expr) {

		return text.split(expr);
	}

	public static String[] caseSplit(String text) {

		ArrayList<String> result = new ArrayList<String>();

		int prevIndex = 0;
		boolean prevCase = false;

		for (int i = 0; i < text.length(); i++) {

			String symbol = text.substring(i, i + 1);
			boolean symbolCase = symbol.equals(symbol.toUpperCase());

			if (i > 0 && !prevCase && symbolCase) {
				result.add(text.substring(prevIndex, i));
				prevIndex = i;
			}

			prevCase = symbolCase;
		}

		if (prevIndex < text.length()) {
			result.add(text.substring(prevIndex, text.length()));
		}

		return result.toArray(new String[result.size()]);
	}

	public static String formatDouble(Double value, String format) {

		if (format != null && format.length() != 0) {

			NumberFormat formatter = new DecimalFormat(format);

			return formatter.format(value);

		} else {
			return value.toString();
		}
	}

	public static String join(String[] strArray) {

		StringBuilder sb = new StringBuilder();

		for (String str : strArray) {

			if (str != null) {
				sb.append(str);
			}
		}

		return sb.toString();
	}

	public static String join(String[] strArray, String delimiter) {

		if (strArray.length > 0) {

			String[] joinArray = new String[strArray.length * 2];

			int i = 0;
			joinArray[i++] = strArray[0];

			for (int x = 1; i < strArray.length; i++) {

				joinArray[i++] = delimiter;
				joinArray[i++] = strArray[x];
			}

			return join(joinArray);

		} else {
			return EMPTY_STRING;
		}
	}

	public static String join(String arg0, String... args) {

		StringBuilder sb = new StringBuilder();

		if (arg0 != null) {
			sb.append(arg0);
		}

		for (String argN : args) {

			if (argN != null) {
				sb.append(argN);
			}
		}

		return sb.toString();
	}

	/**
	 * Replace the oldString by the newString in the line and returns the
	 * result.
	 * 
	 * @param line
	 *            the line to replace.
	 * @param oldString
	 *            old token to replace.
	 * @param newString
	 *            new token to replace.
	 */
	public static final String replaceAll(String line, String oldString, String newString) {

		int i = 0;

		if ((i = line.indexOf(oldString, i)) >= 0) {

			char line2[] = line.toCharArray();
			char newString2[] = newString.toCharArray();
			int oLength = oldString.length();
			StringBuilder buf = new StringBuilder(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;

			int j;
			for (j = i; (i = line.indexOf(oldString, i)) > 0; j = i) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
			}

			buf.append(line2, j, line2.length - j);
			return buf.toString();

		} else {
			return line;
		}
	}

	/**
	 * Checks if a String is empty ("") or null.
	 * 
	 * isEmpty(null) = true
	 * isEmpty("") = true
	 * isEmpty(" ") = false
	 * isEmpty("bob") = false
	 * isEmpty(" bob ") = false
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return true if the String is empty or null
	 */
	public static boolean isEmpty(String str) {

		return str == null || str.length() == 0;
	}

	/**
	 * Checks if a String is not empty ("") and not null.
	 * 
	 * isNotEmpty(null) = false
	 * isNotEmpty("") = false
	 * isNotEmpty(" ") = true
	 * isNotEmpty("bob") = true
	 * isNotEmpty(" bob ") = true
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return true if the String is not empty and not null
	 */
	public static boolean isNotEmpty(String str) {

		return str != null && str.length() > 0;
	}

	public static boolean isEqualsAny(char what, char... args) {

		for (char arg : args) {

			if (what == arg) {
				return true;
			}
		}

		return false;
	}

	public static boolean isEqualsAny(String what, String... args) {

		if (what != null) {

			for (String arg : args) {

				if (what.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static String fill(String str, int length) {

		String result = new String(str);

		while (result.length() < length) {
			result = result.concat(str);
		}

		return result.substring(0, length);
	}

	public static String repeat(String str, int times) {

		String result = new String();

		for (int i = 0; i < times; i++) {
			result = result.concat(str);
		}

		return result;
	}

	public static String repeat(String str, String delim, int times) {

		String result = new String();

		for (int i = 0; i < times; i++) {

			if (result.isEmpty()) {
				result = result.concat(str);
			} else {
				result = result.concat(delim).concat(str);
			}
		}

		return result;
	}

	public static String onlyNumbers(String text) {

		String result = new String();

		final String DIGITS = "1234567890";

		if (text != null) {

			char[] arr = text.toCharArray();

			for (int i = 0; i < arr.length; i++) {

				if (DIGITS.indexOf(arr[i]) >= 0) {
					result += arr[i];
				}
			}
		}

		return result;
	}

	public static String concat(Object... args) {

		String format = repeat("%s", args.length);

		return format(format, args);
	}

	public static String concat(char delim, Object... args) {

		String format = repeat("%s", String.valueOf(delim), args.length);

		return format(format, args);
	}

	public static List<String> createList(String... args) {

		List<String> list = new ArrayList<String>();

		for (int i = 0; i < args.length; i++) {
			list.add(args[i]);
		}

		return list;
	}

	public static String[] createArray(List<String> list) {

		String[] arr = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i);
		}

		return arr;
	}

}
