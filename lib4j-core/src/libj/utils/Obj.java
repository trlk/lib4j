package libj.utils;

import java.util.ArrayList;
import java.util.List;

public class Obj {

	public static Object nvl(Object... args) {

		for (Object arg : args) {

			if (arg != null) {
				return arg;
			}
		}

		return null;
	}

	public static Object nvl2(Object arg0, Object arg1, Object arg2) {

		if (arg0 != null) {
			return arg1;
		} else {
			return arg2;
		}
	}

	public static Object iif(Boolean expr, Object arg0, Object arg1) {

		if (expr != null && expr) {
			return arg0;
		} else {
			return arg1;
		}
	}

	public static boolean isNullAny(Object... args) {

		for (Object arg : args) {

			if (arg == null) {
				return true;
			}
		}

		return false;
	}

	public static boolean isNullAll(Object... args) {

		for (Object arg : args) {

			if (arg != null) {
				return false;
			}
		}

		return true;
	}

	public static boolean isEqualsAny(Object object, Object... args) {

		if (object != null) {

			for (Object arg : args) {

				if (arg != null && object.equals(arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static List<Object> createList(Object... args) {

		List<Object> list = new ArrayList<Object>();

		for (Object object : args) {
			list.add(object);
		}

		return list;
	}

	public static Object[] createArray(Object... args) {

		return args;
	}

}
