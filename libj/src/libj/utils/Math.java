package libj.utils;

import java.math.BigDecimal;

public class Math {

	public static int min(int arg0, int arg1) {
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

	public static int div(int arg0, int arg1) {
		return arg0 / arg1;
	}

	public static int mod(int arg0, int arg1) {
		return arg0 % arg1;
	}

	public static float toFloat(BigDecimal bdAmount) {

		return bdAmount.floatValue();
	}

	public static BigDecimal toBigDecimal(float amount, int scale) {

		BigDecimal bdAmount = new BigDecimal(amount);

		return bdAmount.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal toBigDecimal(float amount) {

		return toBigDecimal(amount, 2);
	}

}
