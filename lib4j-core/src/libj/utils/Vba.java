package libj.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vba {

	public static String ChrW(int code) {

		return Character.toString((char) code);
	}

	public static String Left(String text, Integer numberOfChars) {

		return Text.substr(text, 1, numberOfChars);
	}

	public static String Left(String text) {

		return Left(text, 1);
	}

	public static String Right(String text, Integer numberOfChars) {

		return Text.substr(text, -numberOfChars);
	}

	public static String Right(String text) {

		return Left(text, 1);
	}

	public static String Format(Double value, String format) {

		NumberFormat formatter = new DecimalFormat(format);

		return formatter.format(value);
	}

	public static Double CDbl(String text) {

		return Double.valueOf(text);
	}

	public static Integer CInt(String text) {

		return Integer.valueOf(text);
	}

	public static String Mid(String text, Integer startPosition, Integer numberOfChars) {

		return Text.substr(text, startPosition, numberOfChars);
	}

	public static String Mid(String text, Integer startPosition) {

		return Text.substr(text, startPosition);
	}

	public static String IIf(Boolean expr, String truePart, String falsePart) {

		if (expr) {
			return truePart;
		} else {
			return falsePart;
		}

	}

	public static String Replace(String text, String find, String replacement) {

		return text.replace(find, replacement);
	}

	public static String UCase(String text) {

		return text.toUpperCase();
	}

}
