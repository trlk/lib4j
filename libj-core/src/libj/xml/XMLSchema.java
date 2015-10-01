package libj.xml;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import libj.debug.Log;
import libj.error.RuntimeError;
import libj.utils.Cal;
import libj.utils.Xml;
import spring.util.LinkedCaseInsensitiveMap;

public class XMLSchema {

	// namespace
	public static final String NS = "http://www.w3.org/2001/XMLSchema".intern();

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
	public static final String TIME = "time".intern();
	public static final String DATETIME = "dateTime".intern();

	@SuppressWarnings("rawtypes")
	private static final Map<String, Class> types;

	static {

		@SuppressWarnings("rawtypes")
		Map<String, Class> map = new LinkedCaseInsensitiveMap<Class>();

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

	@SuppressWarnings("rawtypes")
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
