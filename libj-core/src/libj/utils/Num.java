package libj.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import libj.error.RuntimeError;

public class Num {

	public static Integer nvl(Integer... args) {

		for (Integer arg : args) {

			if (arg != null) {
				return arg;
			}
		}

		return null;
	}

	public static Long nvl(Long... args) {

		for (Long arg : args) {

			if (arg != null) {
				return arg;
			}
		}

		return null;
	}

	public static Float nvl(Float... args) {

		for (Float arg : args) {

			if (arg != null) {
				return arg;
			}
		}

		return null;
	}

	public static Double nvl(Double... args) {

		for (Double arg : args) {

			if (arg != null) {
				return arg;
			}
		}

		return null;
	}

	public static Integer min(Integer... args) {

		Integer minValue = null;

		for (Integer arg : args) {

			if (arg != null) {

				if (minValue == null || arg < minValue) {
					minValue = arg;
				}
			}
		}

		return minValue;
	}

	public static Long min(Long... args) {

		Long minValue = null;

		for (Long arg : args) {

			if (arg != null) {

				if (minValue == null || arg < minValue) {
					minValue = arg;
				}
			}
		}

		return minValue;
	}

	public static Float min(Float... args) {

		Float minValue = null;

		for (Float arg : args) {

			if (arg != null) {

				if (minValue == null || arg < minValue) {
					minValue = arg;
				}
			}
		}

		return minValue;
	}

	public static Double min(Double... args) {

		Double minValue = null;

		for (Double arg : args) {

			if (arg != null) {

				if (minValue == null || arg < minValue) {
					minValue = arg;
				}
			}
		}

		return minValue;
	}

	public static Integer max(Integer... args) {

		Integer maxValue = null;

		for (Integer arg : args) {

			if (arg != null) {

				if (maxValue == null || arg > maxValue) {
					maxValue = arg;
				}
			}
		}

		return maxValue;
	}

	public static Long max(Long... args) {

		Long maxValue = null;

		for (Long arg : args) {

			if (arg != null) {

				if (maxValue == null || arg > maxValue) {
					maxValue = arg;
				}
			}
		}

		return maxValue;
	}

	public static Float max(Float... args) {

		Float maxValue = null;

		for (Float arg : args) {

			if (arg != null) {

				if (maxValue == null || arg > maxValue) {
					maxValue = arg;
				}
			}
		}

		return maxValue;
	}

	public static Double max(Double... args) {

		Double maxValue = null;

		for (Double arg : args) {

			if (arg != null) {

				if (maxValue == null || arg > maxValue) {
					maxValue = arg;
				}
			}
		}

		return maxValue;
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

	public static int toInt(String value) {

		try {

			return Integer.parseInt(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable integer: %s", value);
		}
	}

	public static boolean isEqualsAny(Integer value, Integer... args) {

		if (value != null) {

			for (Integer arg : args) {

				if (value.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isEqualsAny(Long value, Long... args) {

		if (value != null) {

			for (Long arg : args) {

				if (value.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isEqualsAny(Float value, Float... args) {

		if (value != null) {

			for (Float arg : args) {

				if (value.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isEqualsAny(Double value, Double... args) {

		if (value != null) {

			for (Double arg : args) {

				if (value.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isEqualsAny(BigInteger value, BigInteger... args) {

		if (value != null) {

			for (BigInteger arg : args) {

				if (value.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isEqualsAny(BigDecimal value, BigDecimal... args) {

		if (value != null) {

			for (BigDecimal arg : args) {

				if (value.equals(arg)) {
					return true;
				}
			}
		}

		return false;
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

}
