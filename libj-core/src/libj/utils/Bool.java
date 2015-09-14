package libj.utils;

import libj.error.RuntimeError;

/**
 * Bool functions
 * 
 * @author Taras Lyuklyanchuk
 * 
 */

public class Bool {

	public static final Boolean TRUE = new Boolean(true);
	public static final Boolean FALSE = new Boolean(false);
	public static final Integer INT_TRUE = 1;
	public static final Integer INT_FALSE = 0;
	public static final String TXT_TRUE = TRUE.toString().intern();
	public static final String TXT_FALSE = FALSE.toString().intern();

	public static Boolean toBoolean(boolean value) {

		if (value) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	public static Boolean toBoolean(String value) {

		if (value == null) {
			return null;
		} else if (value.equalsIgnoreCase(TXT_TRUE) || value.equals(INT_TRUE.toString())) {
			return TRUE;
		} else if (value.equalsIgnoreCase(TXT_FALSE) || value.equals(INT_FALSE.toString())) {
			return FALSE;
		} else {
			throw new RuntimeError("Unparseable boolean: %s", value);
		}
	}

	public static Boolean toBoolean(Integer value) {

		if (value == null) {
			return null;
		} else {
			return toBoolean(value != 0);
		}
	}

	public static Integer toInteger(Boolean value) {

		if (value == null) {
			return null;
		} else if (value) {
			return INT_TRUE;
		} else {
			return INT_FALSE;
		}
	}

}
