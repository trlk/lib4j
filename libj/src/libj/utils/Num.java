package libj.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import libj.error.RuntimeError;

public class Num {

	private static final int DEFAULT_SCALE = 2;

	public static int nvl(Integer arg0, Integer arg1) {

		if (arg0 != null) {
			return arg0;
		} else {
			return arg1;
		}
	}

	public static long nvl(Long arg0, Long arg1) {

		if (arg0 != null) {
			return arg0;
		} else {
			return arg1;
		}
	}

	public static float nvl(Float arg0, Float arg1) {

		if (arg0 != null) {
			return arg0;
		} else {
			return arg1;
		}
	}

	public static double nvl(Double arg0, Double arg1) {

		if (arg0 != null) {
			return arg0;
		} else {
			return arg1;
		}
	}

	public static int min(int arg0, int arg1) {

		if (arg0 < arg1)
			return arg0;
		else
			return arg1;
	}

	public static long min(long arg0, long arg1) {

		if (arg0 < arg1)
			return arg0;
		else
			return arg1;
	}

	public static float min(float arg0, float arg1) {

		if (arg0 < arg1)
			return arg0;
		else
			return arg1;
	}

	public static double min(double arg0, double arg1) {

		if (arg0 < arg1)
			return arg0;
		else
			return arg1;
	}

	public static int max(int arg0, int arg1) {

		if (arg0 > arg1)
			return arg0;
		else
			return arg1;
	}

	public static long max(long arg0, long arg1) {

		if (arg0 > arg1)
			return arg0;
		else
			return arg1;
	}

	public static float max(float arg0, float arg1) {

		if (arg0 > arg1)
			return arg0;
		else
			return arg1;
	}

	public static double max(double arg0, double arg1) {

		if (arg0 > arg1)
			return arg0;
		else
			return arg1;
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

	public static int toInt(String value) {

		return Integer.parseInt(value);
	}

	public static Integer toInteger(String value) {

		if (Text.isEmpty(value)) {
			return null;
		}

		return toInt(value);
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

	public static BigDecimal toBigDecimal(Float value, int scale) {

		if (value == null) {
			return null;
		}

		BigDecimal bdValue = new BigDecimal(value);

		return bdValue.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal toBigDecimal(Float value) {

		if (value == null) {
			return null;
		}

		return toBigDecimal(value, DEFAULT_SCALE);
	}

	public static BigDecimal toBigDecimal(Double value, int scale) {

		if (value == null) {
			return null;
		}

		BigDecimal bdAmount = new BigDecimal(value);

		return bdAmount.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal toBigDecimal(Double value) {

		if (value == null) {
			return null;
		}

		return toBigDecimal(value, DEFAULT_SCALE);
	}

	public static String toString(float value) {

		if (value == (long) value) {
			return Text.printf("%d", (long) value);
		} else {
			return Text.printf("%s", value);
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
			return Text.printf("%d", (long) value);
		} else {
			return Text.printf("%s", value);
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

		String format = Text.printf("%%.%df", scale);

		return Text.printf(format, value);
	}

}
