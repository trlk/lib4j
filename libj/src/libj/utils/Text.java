package libj.utils;

/**
 * @author Taras Lyuklyanchuk
 * 
 * Text functions
 */

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import libj.error.Raise;

public class Text {

	public static final String EMPTY_STRING = new String();
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static String printf(String format, Object... args) {

		return String.format(format, args);
	}

	public static String sprintf(String format, Object... args) {

		return printf(format, args);
	}

	public static String toUpperCamelCase(String text) {

		if (text == null || text.length() == 0)
			return text;

		char[] chars = text.toCharArray();

		for (int i = 0; i < chars.length; i++)
			if (!Character.isLetterOrDigit(chars[i]))
				chars[i] = '-';

		text = new String(chars);

		String[] w = text.split("-"); // split on "-"

		ArrayList<String> words = new ArrayList<String>();
		for (int i = 0; i < w.length; i++)
			if (w[i].length() != 0)
				words.add(w[i]);

		StringBuilder sb = new StringBuilder(words.size());

		for (int i = 0; i < words.size(); i++) // skip first
		{
			sb.append(words.get(i).substring(0, 1).toUpperCase());
			if (words.get(0).length() > 1)
				sb.append(words.get(i).substring(1));
		}

		return sb.toString(); // join
	}

	public static String toLowerCamelCase(String text) {

		if (text == null || text.length() == 0)
			return text;

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
			} else
				Raise.runtimeException("Invalid argument value");

			Integer endIndex = line.length();

			if (beginIndex > endIndex)
				Raise.runtimeException("Invalid argument value");

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

			if (i != 0 && i % 16 == 0)
				sb.append(printf("\n"));

			sb.append(printf("%2x ", bytes[i]).toUpperCase());
		}

		return sb.toString();
	}

	public static String[] caseSplit(String text) {

		ArrayList<String> result = new ArrayList<String>();

		boolean prevCase = false;
		int prevIndex = 0;
		for (int i = 0; i < text.length(); i++) {

			String symbol = text.substring(i, i + 1);
			boolean symbolCase = symbol.equals(symbol.toUpperCase());

			if (i > 0 && !prevCase && symbolCase) {
				result.add(text.substring(prevIndex, i));
				prevIndex = i;
			}

			prevCase = symbolCase;
		}

		if (prevIndex < text.length())
			result.add(text.substring(prevIndex, text.length()));

		return result.toArray(new String[result.size()]);
	}

	public static String formatDouble(Double value, String format) {

		if (format != null && format.length() != 0) {

			NumberFormat formatter = new DecimalFormat(format);

			return formatter.format(value);

		} else
			return value.toString();

	}

	public static String join(String[] strArray) {

		StringBuilder sb = new StringBuilder();

		for (String str : strArray) {
			sb.append(str);
		}

		return sb.toString();
	}

	public static String join(String[] strArray, String delimiter) {

		if (strArray.length > 0) {

			String[] joinArray = new String[strArray.length - 1];

			int i = 0;
			joinArray[i++] = strArray[0];

			for (int x = 1; i < strArray.length; i++) {

				joinArray[i++] = delimiter;
				joinArray[i++] = strArray[x];
			}

			return join(joinArray);

		} else
			return EMPTY_STRING;
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
	public static final String replaceAll(String line, String oldString,
			String newString) {
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
	 * StringUtils.isEmpty(null) = true StringUtils.isEmpty(&quot;&quot;) = true
	 * StringUtils.isEmpty(&quot; &quot;) = false
	 * StringUtils.isEmpty(&quot;bob&quot;) = false
	 * StringUtils.isEmpty(&quot; bob &quot;) = false
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
	 * StringUtils.isNotEmpty(null) = false
	 * StringUtils.isNotEmpty(&quot;&quot;) = false
	 * StringUtils.isNotEmpty(&quot; &quot;) = true
	 * StringUtils.isNotEmpty(&quot;bob&quot;) = true
	 * StringUtils.isNotEmpty(&quot; bob &quot;) = true
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return true if the String is not empty and not null
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

}
