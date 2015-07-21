package libj.xml;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import libj.utils.Cal;
import libj.utils.Xml;

public class XMLSchema {

	// namespace
	public static final String NS = "http://www.w3.org/2001/XMLSchema";

	// xml types 
	public static final String STRING = "string";
	public static final String BOOLEAN = "boolean";
	public static final String BYTE = "byte";
	public static final String INT = "int";
	public static final String INTEGER = "integer";
	public static final String LONG = "long";
	public static final String SHORT = "short";
	public static final String FLOAT = "float";
	public static final String DOUBLE = "double";
	public static final String DECIMAL = "decimal";
	public static final String DATE = "date";
	public static final String TIME = "time";
	public static final String DATETIME = "dateTime";

	@SuppressWarnings("rawtypes")
	private static final Map<String, Class> types;

	static {

		@SuppressWarnings("rawtypes")
		Map<String, Class> map = new LinkedHashMap<String, Class>();

		map.put(STRING, String.class);
		map.put(BOOLEAN, Boolean.class);
		map.put(BYTE, Byte.class);
		map.put(INTEGER, Integer.class);
		map.put(INT, Integer.class);
		map.put(LONG, Long.class);
		map.put(SHORT, Short.class);
		map.put(FLOAT, Float.class);
		map.put(DOUBLE, Double.class);
		map.put(DECIMAL, BigDecimal.class);
		map.put(DATE, Date.class);
		map.put(TIME, Time.class);
		map.put(DATETIME, Date.class);

		types = Collections.unmodifiableMap(map);
	}

	@SuppressWarnings("rawtypes")
	public static String getType(Class clazz) {

		for (Map.Entry<String, Class> entry : types.entrySet()) {

			if (entry.getValue().equals(clazz)) {
				return entry.getKey();
			}
		}

		return STRING;
	}

	public static String getType(Object object) {

		return getType(object.getClass());
	}

	@SuppressWarnings("rawtypes")
	public static Class getClass(String xmlType) {

		if (types.containsKey(xmlType)) {
			return types.get(xmlType);
		} else {
			return types.get(STRING);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object createObject(Class clazz, String value) throws Exception {

		Object object = null;

		if (clazz.equals(Date.class)) {

			if (value.length() == Xml.DATE_FORMAT.length()) {
				object = Cal.toDate(value, Xml.DATE_FORMAT);
			} else {
				object = Cal.toDate(value, Xml.DATETIME_FORMAT);
			}

		} else {

			object = clazz.getConstructor(String.class).newInstance(value);
		}

		return object;
	}

	public static Object createObject(String type, String value) throws Exception {
		return createObject(getClass(type), value);
	}

}
