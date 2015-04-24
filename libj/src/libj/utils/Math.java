package libj.utils;

import java.math.BigDecimal;

public class Math {

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

	public static float toFloat(BigDecimal bigDecimal) {

		return bigDecimal.floatValue();
	}

	public static double toDouble(BigDecimal bigDecimal) {

		return bigDecimal.doubleValue();
	}

	public static BigDecimal toBigDecimal(float amount, int scale) {

		BigDecimal bdAmount = new BigDecimal(amount);

		return bdAmount.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal toBigDecimal(float amount) {

		return toBigDecimal(amount, DEFAULT_SCALE);
	}

	public static BigDecimal toBigDecimal(double amount, int scale) {

		BigDecimal bdAmount = new BigDecimal(amount);

		return bdAmount.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal toBigDecimal(double amount) {

		return toBigDecimal(amount, DEFAULT_SCALE);
	}
}
