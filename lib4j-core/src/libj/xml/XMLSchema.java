package libj.xml;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import libj.debug.Log;
import libj.error.RuntimeError;
import libj.utils.Cal;
import libj.utils.Xml;
import spring.util.LinkedCaseInsensitiveMap;

@SuppressWarnings("rawtypes")
public class XMLSchema {

	// namespace
	public static final String NAMESPACE = "http://www.w3.org/2001/XMLSchema".intern();

	// xml types 
	public static final String STRING = "string".intern();
	public static final String BOOLEAN = "boolean".intern();
	public static final String BYTE = "byte".intern();
	public static final String INT = "int".intern();
	public static final String INTEGER = "integer".intern();
	public static final String LONG = "long".intern();
	public static final String SHORT = "short".intern();
	public static final String FLOAT = "float".intern();
	public static final String DOUBLE = "double".intern();
	public static final String DECIMAL = "decimal".intern();
	public static final String DATE = "date".intern();
	public static final String DATETIME = "dateTime".intern();
	public static final String TIME = "time".intern();

	private static final Map<String, Class> type2class;
	private static final Map<Class, String> class2type;

	static {

		// type to class map
		Map<String, Class> t2c = new LinkedCaseInsensitiveMap<Class>();

		t2c.put(STRING, String.class);
		t2c.put(BOOLEAN, Boolean.class);
		t2c.put(BYTE, Byte.class);
		t2c.put(INTEGER, Integer.class);
		t2c.put(INT, Integer.class);
		t2c.put(LONG, Long.class);
		t2c.put(SHORT, Short.class);
		t2c.put(FLOAT, Float.class);
		t2c.put(DOUBLE, Double.class);
		t2c.put(DECIMAL, BigDecimal.class);
		t2c.put(DATE, Date.class);
		t2c.put(DATETIME, Date.class);
		t2c.put(TIME, java.sql.Time.class);

		// class to type map
		Map<Class, String> c2t = new HashMap<Class, String>();

		for (Map.Entry<String, Class> e : t2c.entrySet()) {

			c2t.put(e.getValue(), e.getKey());
		}

		c2t.put(java.sql.Date.class, DATE);
		c2t.put(java.sql.Timestamp.class, DATETIME);

		type2class = Collections.unmodifiableMap(t2c);
		class2type = Collections.unmodifiableMap(c2t);
	}

	public static boolean isCompatible(Class clazz) {

		return class2type.containsKey(clazz);
	}

	public static boolean isCompatible(Object object) {

		return isCompatible(getClass(object));
	}

	@Deprecated
	public static String getType(Class clazz) {

		if (class2type.containsKey(clazz)) {
			return class2type.get(clazz);
		} else {
			return STRING;
		}
	}

	public static String getType(Object object) {

		if (object instanceof Date) {

			if (Cal.isTruncated((Date) object)) {
				return DATE;
			} else {
				return DATETIME;
			}

		} else {
			return getType(getClass(object));
		}
	}

	public static Class getClass(String xsType) {

		if (type2class.containsKey(xsType)) {
			return type2class.get(xsType);
		} else {
			return type2class.get(STRING);
		}
	}

	public static Class getClass(Object object) {

		if (object != null) {
			return object.getClass();
		} else {
			throw new RuntimeException("Can't recognize class for NULL object");
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static Object createObject(Class clazz, String value) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

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

	public static Object createObject(String type, String value) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		return createObject(getClass(type), value);
	}

	public static Object createObject(Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			String text = node.getTextContent();

			// xml attributes
			if (node.hasAttributes()) {

				NamedNodeMap attrs = node.getAttributes();

				for (int i = 0; i < attrs.getLength(); i++) {

					Node attr = attrs.item(i);
					String attrName = attr.getNodeName();
					String attrValue = attr.getNodeValue();

					// object with type
					if (attrName.equalsIgnoreCase(Xml.TAG_TYPE)) {

						try {

							return createObject(attrValue, text);

						} catch (Exception e) {
							Log.warn(e.getMessage());
						}
					}
				}
			}

			return text; // return as string

		} else {
			throw new RuntimeError("Invalid node type: %s (%s)", node.getNodeType(), node.toString());
		}
	}

	public static Object createZeroObject(Class clazz) throws InstantiationException, IllegalAccessException {

		if (clazz.equals(Integer.class)) {
			return (int) 0;
		} else if (clazz.equals(Short.class)) {
			return (short) 0;
		} else if (clazz.equals(Long.class)) {
			return (long) 0;
		} else if (clazz.equals(Float.class)) {
			return (float) 0;
		} else if (clazz.equals(Double.class)) {
			return (double) 0;
		} else if (clazz.equals(Byte.class)) {
			return (byte) 0;
		} else if (clazz.equals(BigDecimal.class)) {
			return BigDecimal.valueOf(0);
		} else if (clazz.equals(BigInteger.class)) {
			return BigInteger.valueOf(0);
		} else if (clazz.equals(Date.class)) {
			return new Date(0);
		} else if (clazz.equals(Time.class)) {
			return new Time(0);
		} else if (clazz.equals(Boolean.class)) {
			throw new RuntimeError("Class %s does not have zero value", clazz.getName());
		} else {
			return clazz.newInstance();
		}
	}

	public static Object createZeroObject(String type) throws InstantiationException, IllegalAccessException {

		return createZeroObject(getClass(type));
	}

}
