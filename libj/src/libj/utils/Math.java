package libj.utils;

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

}
