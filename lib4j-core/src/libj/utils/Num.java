package libj.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import libj.error.RuntimeError;

public class Num {

	public static Integer nvl(Integer... args) {

		return (Integer) Obj.nvl((Object[]) args);
	}

	public static Long nvl(Long... args) {

		return (Long) Obj.nvl((Object[]) args);
	}

	public static Float nvl(Float... args) {

		return (Float) Obj.nvl((Object[]) args);
	}

	public static Double nvl(Double... args) {

		return (Double) Obj.nvl((Object[]) args);
	}

	public static Integer min(Integer... args) {

		Integer result = null;

		for (Integer arg : args) {

			if (arg != null) {

				if (result == null || arg < result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Long min(Long... args) {

		Long result = null;

		for (Long arg : args) {

			if (arg != null) {

				if (result == null || arg < result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Float min(Float... args) {

		Float result = null;

		for (Float arg : args) {

			if (arg != null) {

				if (result == null || arg < result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Double min(Double... args) {

		Double result = null;

		for (Double arg : args) {

			if (arg != null) {

				if (result == null || arg < result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Integer max(Integer... args) {

		Integer result = null;

		for (Integer arg : args) {

			if (arg != null) {

				if (result == null || arg > result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Long max(Long... args) {

		Long result = null;

		for (Long arg : args) {

			if (arg != null) {

				if (result == null || arg > result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Float max(Float... args) {

		Float result = null;

		for (Float arg : args) {

			if (arg != null) {

				if (result == null || arg > result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Double max(Double... args) {

		Double result = null;

		for (Double arg : args) {

			if (arg != null) {

				if (result == null || arg > result) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static int div(int arg0, int arg1) {

		return arg0 / arg1;
	}

	public static long div(long arg0, long arg1) {

		return arg0 / arg1;
	}

	public static int mod(int arg0, int arg1) {

		return arg0 % arg1;
	}

	public static long mod(long arg0, long arg1) {

		return arg0 % arg1;
	}

	public static float round(float value) {

		return round(value, 0);
	}

	public static float round(float value, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException();
		}

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, RoundingMode.HALF_UP);

		return bd.floatValue();
	}

	public static double round(double value) {

		return round(value, 0);
	}

	public static double round(double value, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException();
		}

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, RoundingMode.HALF_UP);

		return bd.doubleValue();
	}

	public static boolean isEqualsAny(Integer value, Integer... args) {

		return Obj.isEqualsAny(value, (Object[]) args);
	}

	public static boolean isEqualsAny(Long value, Long... args) {

		return Obj.isEqualsAny(value, (Object[]) args);
	}

	public static boolean isEqualsAny(Float value, Float... args) {

		return Obj.isEqualsAny(value, (Object[]) args);
	}

	public static boolean isEqualsAny(Double value, Double... args) {

		return Obj.isEqualsAny(value, (Object[]) args);
	}

	public static boolean isEqualsAny(BigInteger value, BigInteger... args) {

		return Obj.isEqualsAny(value, (Object[]) args);
	}

	public static boolean isEqualsAny(BigDecimal value, BigDecimal... args) {

		return Obj.isEqualsAny(value, (Object[]) args);
	}

	public static int toInt(String value) {

		try {

			return Integer.parseInt(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable integer: %s", value);
		}
	}

	public static Integer toInteger(String value) {

		if (Text.isEmpty(value)) {
			return null;
		}

		return toInt(value);
	}

	public static Integer toInteger(Float value) {

		if (value == null) {
			return null;
		}

		return Math.round(value);
	}

	public static Long toLong(String value) {

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return Long.parseLong(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable long: %s", value);
		}
	}

	public static Long toLong(Float value) {

		if (value == null) {
			return null;
		}

		return (long) Math.round(value);
	}

	public static Long toLong(Double value) {

		if (value == null) {
			return null;
		}

		return Math.round(value);
	}

	public static Float toFloat(String value) {

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new Float(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable float: %s", value);
		}
	}

	public static Float toFloat(Double value) {

		if (value == null) {
			return null;
		}

		try {

			return new Float(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable float: %d", value);
		}
	}

	public static Float toFloat(BigDecimal value) {

		if (value == null) {
			return null;
		}

		return value.floatValue();
	}

	public static Double toDouble(String value) {

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new Double(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable double: %s", value);
		}
	}

	public static Double toDouble(Float value) {

		if (value == null) {
			return null;
		}

		try {

			return new Double(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable double: %d", value);
		}
	}

	public static Double toDouble(BigDecimal value) {

		if (value == null) {
			return null;
		}

		return value.doubleValue();
	}

	public static BigInteger toBigInteger(String value) {

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new BigInteger(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable BigInteger: %s", value);
		}
	}

	public static BigInteger toBigInteger(Integer value) {

		if (value == null) {
			return null;
		}

		return BigInteger.valueOf(value);
	}

	public static BigInteger toBigInteger(Long value) {

		if (value == null) {
			return null;
		}

		return BigInteger.valueOf(value);
	}

	public static BigDecimal toBigDecimal(String value) {

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new BigDecimal(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable BigDecimal: %s", value);
		}
	}

	public static BigDecimal toBigDecimal(Float value) {

		if (value == null) {
			return null;
		}

		return new BigDecimal(value);
	}

	public static BigDecimal toBigDecimal(Float value, int scale) {

		if (value == null) {
			return null;
		}

		BigDecimal bd = new BigDecimal(value);

		return bd.setScale(scale, RoundingMode.HALF_UP);
	}

	public static BigDecimal toBigDecimal(Double value) {

		if (value == null) {
			return null;
		}

		return new BigDecimal(value);
	}

	public static BigDecimal toBigDecimal(Double value, int scale) {

		if (value == null) {
			return null;
		}

		BigDecimal bd = new BigDecimal(value);

		return bd.setScale(scale, RoundingMode.HALF_UP);
	}

	public static String toString(float value) {

		if (value == (long) value) {
			return Text.sprintf("%d", (long) value);
		} else {
			return Text.sprintf("%s", value);
		}
	}

	public static String toString(Float value) {

		if (value == null) {
			return Text.EMPTY_STRING;
		}

		return toString((float) value);
	}

	public static String toString(double value) {

		if (value == (long) value) {
			return Text.sprintf("%d", (long) value);
		} else {
			return Text.sprintf("%s", value);
		}
	}

	public static String toString(Double value) {

		if (value == null) {
			return Text.EMPTY_STRING;
		}

		return toString((double) value);
	}

	public static String toString(Float value, int scale) {

		return toString(toDouble(value), scale);
	}

	public static String toString(Double value, int scale) {

		if (value == null) {
			return Text.EMPTY_STRING;
		}

		String format = Text.sprintf("%%.%df", scale);

		return Text.format(format, value);
	}

	public static String toString(BigDecimal value, int scale) {

		if (value == null) {
			return Text.EMPTY_STRING;
		}

		return value.setScale(scale, RoundingMode.HALF_UP).toString();
	}

	public static String formatFloat(Float value, String format) {

		return formatDouble(toDouble(value), format);
	}

	public static String formatDouble(Double value, String format) {

		if (format != null && format.length() != 0) {

			NumberFormat formatter = new DecimalFormat(format);

			return formatter.format(value);

		} else {
			return value.toString();
		}
	}

}
