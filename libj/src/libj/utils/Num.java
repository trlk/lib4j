package libj.utils;

import java.math.BigDecimal;

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

		return toInt(value);
	}

	public static float toFloat(String value) {

		try {
			return new Float(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable float: %s", value);
		}
	}

	public static float toFloat(double value) {

		try {
			return new Float(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable float: %d", value);
		}
	}

	public static float toFloat(BigDecimal bigDecimal) {

		return bigDecimal.floatValue();
	}

	public static double toDouble(String value) {

		try {
			return new Double(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable double: %s", value);
		}
	}

	public static double toDouble(float value) {

		try {
			return new Double(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable double: %d", value);
		}
	}

	public static double toDouble(BigDecimal bigDecimal) {

		return bigDecimal.doubleValue();
	}

	public static BigDecimal toBigDecimal(String value) {

		try {
			return new BigDecimal(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable bigDecimal: %s", value);
		}
	}

	public static BigDecimal toBigDecimal(float value, int scale) {

		BigDecimal bdValue = new BigDecimal(value);

		return bdValue.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal toBigDecimal(float value) {

		return toBigDecimal(value, DEFAULT_SCALE);
	}

	public static BigDecimal toBigDecimal(double value, int scale) {

		BigDecimal bdAmount = new BigDecimal(value);

		return bdAmount.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal toBigDecimal(double value) {

		return toBigDecimal(value, DEFAULT_SCALE);
	}

	public static String format(float value) {

		if (value == (long) value) {
			return String.format("%d", (long) value);
		} else {
			return String.format("%s", value);
		}
	}

	public static String format(double value) {

		if (value == (long) value) {
			return String.format("%d", (long) value);
		} else {
			return String.format("%s", value);
		}
	}

}
