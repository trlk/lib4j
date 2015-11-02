package libj.utils;

/**
 * Text functions
 * 
 * @author Taras Lyuklyanchuk
 * 
 */

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Text {

	public static final String EMPTY_STRING = new String().intern();
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static String nvl(String... args) {

		for (String arg : args) {

			if (arg != null) {
				return arg;
			}
		}

		return EMPTY_STRING;
	}

	public static String nvl2(Object arg0, Object arg1, Object arg2) {

		return (String) Obj.nvl2(arg0, arg1, arg2);
	}

	public static String clone(String str) {

		if (str == null) {
			return new String();
		} else {
			return new String(str);
		}
	}

	public static String format(String format, Object... args) {

		if (format == null) {
			return EMPTY_STRING;
		}

		return String.format(format, args);
	}

	public static String sprintf(String format, Object... args) {

		return format(format, args);
	}

	public static String toUpperCamelCase(String str) {

		if (str == null) {
			return EMPTY_STRING;
		}

		char[] chars = str.toCharArray();

		for (int i = 0; i < chars.length; i++) {

			if (!Character.isLetterOrDigit(chars[i])) {
				chars[i] = '-';
			}
		}

		str = new String(chars);

		String[] w = str.split("-"); // split on "-"

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

	public static String toLowerCamelCase(String str) {

		if (str == null) {
			return EMPTY_STRING;
		}

		char[] result = toUpperCamelCase(str).toCharArray();
		result[0] = Character.toLowerCase(result[0]);

		return new String(result);
	}

	public static String substr(String str, int pos) {

		if (str == null) {
			return EMPTY_STRING;
		}

		try {

			Integer beginIndex = 0;

			if (pos > 0) {
				beginIndex = pos - 1;
			} else if (pos < 0) {
				beginIndex = str.length() + pos;
			} else {
				throw new IllegalArgumentException();
			}

			Integer endIndex = str.length();

			if (beginIndex > endIndex) {
				throw new IllegalArgumentException();
			}

			return str.substring(beginIndex, endIndex);

		} catch (Exception e) {
			return EMPTY_STRING;
		}
	}

	public static String substr(String str, int pos, int len) {

		if (str == null) {
			return EMPTY_STRING;
		}

		try {

			Integer beginIndex = pos - 1;
			Integer endIndex = pos + len - 1;

			if (endIndex > str.length()) {
				endIndex = str.length();
			}

			return str.substring(beginIndex, endIndex);

		} catch (Exception e) {
			return EMPTY_STRING;
		}
	}

	public static String substr2(String str, int pos, int len) {

		return substr(str, pos, len).trim();
	}

	public static String crop(String str, int len) {

		if (str == null) {
			return EMPTY_STRING;
		}

		return str.trim().substring(0, len);
	}

	public static byte[] getBytes(String str) {

		if (str == null) {
			return null;
		}

		return str.getBytes();
	}

	public static byte[] getBytes(String str, String charset) {

		if (str == null) {
			return null;
		}

		return str.getBytes(Charset.forName(charset));
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

	public static String[] split(String str, String regex) {

		if (str == null) {
			return EMPTY_STRING_ARRAY;
		}

		return str.split(regex);
	}

	public static String[] caseSplit(String str) {

		if (str == null) {
			return EMPTY_STRING_ARRAY;
		}

		ArrayList<String> result = new ArrayList<String>();

		int prevIndex = 0;
		boolean prevCase = false;

		for (int i = 0; i < str.length(); i++) {

			String symbol = str.substring(i, i + 1);
			boolean symbolCase = symbol.equals(symbol.toUpperCase());

			if (i > 0 && !prevCase && symbolCase) {
				result.add(str.substring(prevIndex, i));
				prevIndex = i;
			}

			prevCase = symbolCase;
		}

		if (prevIndex < str.length()) {
			result.add(str.substring(prevIndex, str.length()));
		}

		return result.toArray(new String[result.size()]);
	}

	/**
	 * Replace the oldString by the newString in the line and returns the
	 * result.
	 * 
	 * @param text
	 *            the text to replace.
	 * @param what
	 *            old token to replace.
	 * @param replacement
	 *            new token to replace.
	 */
	public static final String replaceAll(String text, String what, String replacement) {

		if (Obj.isNullAny(text, what, replacement)) {
			return text;
		}

		int i = 0;

		if ((i = text.indexOf(what, i)) >= 0) {

			char line2[] = text.toCharArray();
			char newString2[] = replacement.toCharArray();
			int oLength = what.length();

			StringBuilder buf = new StringBuilder(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;

			int j;
			for (j = i; (i = text.indexOf(what, i)) > 0; j = i) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
			}

			buf.append(line2, j, line2.length - j);

			return buf.toString();

		} else {
			return text;
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

		return !(isEmpty(str));
	}

	public static boolean isEqualsAny(char c, char... args) {

		for (char arg : args) {

			if (c == arg) {
				return true;
			}
		}

		return false;
	}

	public static boolean isEqualsAny(String str, String... args) {

		if (str != null) {

			for (Object arg : args) {

				if (str.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static String fill(String str, int length) {

		if (str == null) {
			return EMPTY_STRING;
		}

		String result = clone(str);

		while (result.length() < length) {
			result = result.concat(str);
		}

		return result.substring(0, length);
	}

	public static String repeat(String str, int times) {

		if (str == null) {
			return EMPTY_STRING;
		}

		String result = new String();

		for (int i = 0; i < times; i++) {
			result = result.concat(str);
		}

		return result;
	}

	public static String repeat(String str, String delim, int times) {

		if (str == null) {
			return EMPTY_STRING;
		}

		String result = new String();

		for (int i = 0; i < times; i++) {

			if (result.isEmpty()) {
				result = result.concat(str);
			} else {
				result = result.concat(nvl(delim, EMPTY_STRING)).concat(str);
			}
		}

		return result;
	}

	public static String onlyNumbers(String str) {

		if (str == null) {
			return EMPTY_STRING;
		}

		String result = new String();

		final String DIGITS = "1234567890";

		if (str != null) {

			char[] arr = str.toCharArray();

			for (int i = 0; i < arr.length; i++) {

				if (DIGITS.indexOf(arr[i]) >= 0) {
					result += arr[i];
				}
			}
		}

		return result;
	}

	public static String concat(char delim, String... args) {

		if (args.length > 0) {

			String[] array = new String[args.length * 2];

			int i = 0;
			array[i++] = args[0];

			for (int x = 1; i < args.length; i++) {

				array[i++] = String.valueOf(delim);
				array[i++] = args[x];
			}

			return concat(array);

		} else {
			return EMPTY_STRING;
		}
	}

	public static String concat(String... args) {

		StringBuilder result = new StringBuilder();

		for (String arg : args) {

			if (arg != null) {
				result.append(arg);
			}
		}

		return result.toString();
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

		String[] array = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}

}
