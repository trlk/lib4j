package libj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import libj.debug.Debug;
import libj.debug.Stack;
import libj.error.RuntimeError;
import libj.error.Throw;
import libj.xml.XMLSchema;

public class Xml {

	// defaults
	@Deprecated
	public static char ATTR_DELIMITER = ':';

	@Deprecated
	public static char ELEMENT_DELIMITER = '.';

	@Deprecated
	public static boolean PUT_EMPTY_NODES = Debug.isEnabled();

	// constants
	public static final String TAG_TYPE = "type".intern();
	public static final String TAG_TEXT = "text".intern();
	public static final String TAG_NAME_ITEM = "item".intern();
	public static final String TAG_NAME_ITEMS = "items".intern();
	public static final String ATTR_NAME_TYPE = "type".intern();
	public static final String ATTR_NAME_INDEX = "index".intern();
	public static final int INDENT_LENGTH = 2;
	public static final char COMMA_DELIMITER = '.';
	public static final char XPATH_DELIMITER = '/';

	// xml date/time format
	public static final String DATE_FORMAT = "yyyy-MM-dd".intern();
	public static final String TIME_FORMAT = "HH:mm:ss".intern();
	public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss".intern();

	// boolean
	public static final String XML_TRUE = Bool.TRUE.toString().intern();
	public static final String XML_FALSE = Bool.FALSE.toString().intern();

	public static Document parse(InputStream inputStream) {

		try {

			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();

			return b.parse(inputStream);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Document parse(String inputString) {

		return parse(Stream.newInputStream(inputString.getBytes()));
	}

	public static Document parse(String inputString, String charsetName) {

		try {

			return parse(Stream.newInputStream(inputString.getBytes(charsetName)));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void serialize(Document document, StreamResult streamResult) {

		try {

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", INDENT_LENGTH);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource domSource = new DOMSource(document);
			transformer.transform(domSource, streamResult);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void serialize(Document document, OutputStream outputStream) {

		StreamResult streamResult = new StreamResult(outputStream);
		serialize(document, streamResult);
	}

	public static String serialize(Document document) {

		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);

		serialize(document, streamResult);

		return stringWriter.toString();
	}

	public static ByteArrayOutputStream serializeToOutputStream(Document doc) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		serialize(doc, outputStream);

		return outputStream;
	}

	public static ByteArrayInputStream serializeToInputStream(Document doc) {

		return Stream.newInputStream(serializeToOutputStream(doc));
	}

	public static String prettyFormat(String input, int indent) {

		try {

			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);

			return xmlOutput.getWriter().toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getNodePath(Node node, char delimiter) {

		if (node != null) {

			if (delimiter == 0) {
				Throw.runtimeException("%s: delimiter cannot be a null", Stack.thisMethodName());
			}

			/*
			Node fromNode;
			
			if (offset == 0) {
				fromNode = null; // до упора
			} else if (offset == 1) {
				fromNode = getRootNode(node); // до корневой ноды
			} else {
				throw new RuntimeError("Unsupported offset: %d", offset);
			}
			*/

			String path = null;

			for (Node n = node; n != null; n = n.getParentNode()) {

				if (n.getNodeType() == Node.ELEMENT_NODE) {

					String nodeName = n.getNodeName();

					// truncate namespace prefix
					String[] nameParts = nodeName.split(":");

					if (nameParts.length == 2) {
						nodeName = nameParts[1];
					}

					// list
					if (nodeName == TAG_NAME_ITEM) {

						// index attribute
						String nodeIndex = getAttrValue(n, ATTR_NAME_INDEX);

						if (nodeIndex != null) {
							nodeName = Text.sprintf("%s[%s]", nodeName, nodeIndex);
						}
					}

					// build path
					if (path == null) {
						path = nodeName;
					} else {
						path = nodeName + delimiter + path;
					}
				}
			}

			if (delimiter == XPATH_DELIMITER) {
				path = delimiter + path;
			}

			return path;

		} else {
			return null;
		}

	}

	public static String getNodePath(Node node) {

		return getNodePath(node, ELEMENT_DELIMITER);
	}

	public static String getNodeXPath(Node node) {

		return getNodePath(node, XPATH_DELIMITER);
	}

	public static Boolean toBool(String value) {

		if (value.equalsIgnoreCase(XML_TRUE)) {
			return true;
		} else if (value.equals(Bool.INT_TRUE)) {
			return true;
		} else if (value.equalsIgnoreCase(XML_FALSE)) {
			return false;
		} else if (value.equals(Bool.INT_FALSE)) {
			return false;
		}

		return null;
	}

	public static Date toDate(String value, String format) {

		return Cal.toDate(value, format);
	}

	public static Date toDate(String value) {

		return toDate(value, DATE_FORMAT);
	}

	public static Date toDateTime(String value) {

		return toDate(value, DATETIME_FORMAT);
	}

	public static Date toDateTime(String value, String format) {

		return toDate(value, format);
	}

	public static Float toFloat(String value) {

		return Num.toFloat(value);
	}

	public static Double toDouble(String value) {

		return Num.toDouble(value);
	}

	public static Integer toInteger(String value) {

		return Num.toInteger(value);
	}

	private static Map<String, Object> createMap(Node node, Boolean putEmptyNodes, char elementDelimiter,
			char attrDelimiter) {

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			String path = getNodePath(node, elementDelimiter);

			if (path != null) {

				if (node.hasChildNodes()) {

					if (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

						Node textNode = node.getFirstChild();

						String text = textNode.getTextContent().trim();

						if (putEmptyNodes || text.length() != 0) {

							Object object = XMLSchema.createObject(node);
							map.put(path, object);

							// printed (human readable) text
							String printedText = getPrintedText(object);

							if (Text.isNotEmpty(printedText)) {
								map.put(path + attrDelimiter + TAG_TEXT, printedText);
							}
						}
					}

				} else if (putEmptyNodes) {
					map.put(path, new String());
				}
			}
		}

		// recursive parse child
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			map.putAll(createMap(child, putEmptyNodes, elementDelimiter, attrDelimiter));
		}

		return map;
	}

	public static Map<String, Object> createMap(Document doc, char elementDelimiter) {

		return createMap(doc.getDocumentElement(), PUT_EMPTY_NODES, elementDelimiter, ATTR_DELIMITER);
	}

	public static Map<String, Object> createMap(Document doc, char elementDelimiter, char attrDelimiter) {

		return createMap(doc.getDocumentElement(), PUT_EMPTY_NODES, elementDelimiter, attrDelimiter);
	}

	public static Map<String, Object> createMap(Document doc) {

		return createMap(doc, PUT_EMPTY_NODES);
	}

	public static Map<String, Object> createMap(Document doc, Boolean putEmptyNodes) {

		return createMap(doc, putEmptyNodes, ELEMENT_DELIMITER, ATTR_DELIMITER);
	}

	public static Map<String, Object> createMap(InputStream inputStream, Boolean putEmptyNodes) {

		try {

			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = b.parse(inputStream);

			return createMap(doc, putEmptyNodes);
		}

		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, Object> createMap(InputStream inputStream) {
		return createMap(inputStream, PUT_EMPTY_NODES);
	}

	@SuppressWarnings("rawtypes")
	public static List<Map> createMapList(Document doc, Boolean putEmptyNodes) {

		List<Map> list = new ArrayList<Map>();

		try {

			// Document doc = b.parse(inputStream);
			Element root = doc.getDocumentElement();

			NodeList items = root.getChildNodes();

			if (items != null && items.getLength() > 0) {

				for (int n = 0; n < items.getLength(); n++) {

					if (items.item(n).getNodeType() == Node.ELEMENT_NODE) {

						Map<String, Object> map = new LinkedHashMap<String, Object>();

						Element item = (Element) items.item(n);

						// parse nodes
						for (Node node = item.getFirstChild(); node != null; node = node.getNextSibling()) {

							if (node.getNodeType() == Node.ELEMENT_NODE) {

								Object object = XMLSchema.createObject(node);
								map.put(node.getNodeName(), object);
							}
						}

						list.add(map);
					}
				}
			}

			return list;
		}

		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Node createNode(Document doc, String nodeName) {

		Node node = doc.createElement(nodeName);

		return node;
	}

	public static Node createChild(Node parentNode, String childName) {

		Document doc;

		if (parentNode instanceof Document) {
			doc = (Document) parentNode;
		} else {
			doc = parentNode.getOwnerDocument();
		}

		Node childNode = createNode(doc, childName);
		parentNode.appendChild(childNode);

		return childNode;
	}

	public static Document createDocument() {

		try {

			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			return b.newDocument();

		} catch (ParserConfigurationException e) {
			throw new RuntimeError(e);
		}

	}

	public static Node createDocument(String rootName) {

		Document doc = createDocument();

		return createChild(doc, rootName);
	}

	public static Node getRootNode(Document doc) {

		return doc.getDocumentElement();
	}

	public static Node getRootNode(Node node) {

		if (node != null) {
			return getRootNode(node.getOwnerDocument());
		} else {
			return null;
		}
	}

	public static Node getChildNode(Node parentNode, String childName) {

		return extractNode(parentNode, childName);
	}

	public static Node getAttribute(Node node, String attrName) {

		if (node.hasAttributes()) {

			NamedNodeMap attrs = node.getAttributes();

			for (int i = 0; i < attrs.getLength(); i++) {

				Node attr = attrs.item(i);

				if (attr.getNodeName() == attrName) {
					return attr;
				}
			}
		}

		return null;
	}

	public static String getAttrValue(Node node, String attrName) {

		Node attr = getAttribute(node, attrName);

		if (attr != null) {
			return attr.getNodeValue();
		} else {
			return Text.EMPTY_STRING;
		}
	}

	public static void setAttrValue(Node node, String attrName, String value) {

		Node attr = getAttribute(node, attrName);

		if (attr != null) {

			setNodeValue(attr, value);

		} else if (value != null) {

			NamedNodeMap attributes = node.getAttributes();
			attr = node.getOwnerDocument().createAttribute(attrName);
			attr.setNodeValue(value);
			attributes.setNamedItem(attr);
		}
	}

	public static void setAttrValue(Node node, String attrName, Object value) {

		if (value instanceof Date) {
			setAttrValue(node, attrName, Cal.formatDate((Date) value, DATETIME_FORMAT));
		} else {
			setAttrValue(node, attrName, value.toString());
		}
	}

	public static NodeList extractList(Node parent, String xpath) {

		NodeList result = null;

		try {

			XPath xPath = XPathFactory.newInstance().newXPath();

			result = (NodeList) xPath.evaluate(xpath, parent, XPathConstants.NODESET);

		} catch (XPathExpressionException e) {

			throw new RuntimeError(e);
		}

		return result;
	}

	public static NodeList extractList(Document doc, String xpath) {

		return extractList(doc.getDocumentElement(), xpath);
	}

	public static Node extractNode(Node parent, String xpath) {

		NodeList nodes = extractList(parent, xpath);
		int size = nodes.getLength();

		if (size == 0) {
			return null;
		} else if (size == 1) {
			return nodes.item(0);
		} else {
			throw new RuntimeException("More than one node found");
		}
	}

	public static Node extractNode(Document doc, String xpath) {

		return extractNode(doc.getDocumentElement(), xpath);
	}

	public static int getSize(Node parent, String xpath) {

		NodeList nodeList = extractList(parent, xpath);

		return nodeList.getLength();
	}

	public static boolean isHas(Node parent, String xpath) {

		return getSize(parent, xpath) != 0;
	}

	public static boolean isSet(Node parent, String xpath) {

		if (isHas(parent, xpath)) {

			Node textNode = extractNode(parent, xpath);

			if (textNode != null) {
				return Text.isNotEmpty(textNode.getTextContent());
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	@Deprecated
	public static String getText(Node parent, String xpath) {

		return getText(parent, xpath, Text.EMPTY_STRING);
	}

	@Deprecated
	public static String getText(Node parent, String xpath, String defaultValue) {

		Node textNode = extractNode(parent, xpath);

		if (textNode != null) {
			return textNode.getTextContent();
		} else {
			return defaultValue;
		}
	}

	public static String getString(Node parent, String xpath) {

		return getString(parent, xpath, Text.EMPTY_STRING);
	}

	public static String getString(Node parent, String xpath, String defaultValue) {

		Node node = extractNode(parent, xpath);

		if (node != null) {
			return node.getTextContent();
		} else {
			return defaultValue;
		}
	}

	public static Date getDate(Node parent, String xpath, String format) {

		return Cal.toDate(getString(parent, xpath), format);
	}

	public static Date getDate(Node parent, String xpath) {

		return getDate(parent, xpath, DATE_FORMAT);
	}

	public static Date getDate(Node parent, String xpath, Date defaultValue) {

		Date result = getDate(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static Date getDateTime(Node parent, String xpath) {

		return getDate(parent, xpath, DATETIME_FORMAT);
	}

	public static Date getDateTime(Node parent, String xpath, Date defaultValue) {

		Date result = getDateTime(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static Boolean getBoolean(Node parent, String xpath) {

		String value = getString(parent, xpath);

		try {

			if (Text.isEmpty(value)) {
				return null;
			} else if (value.equalsIgnoreCase(XML_TRUE)) {
				return true;
			} else if (value.equalsIgnoreCase(XML_FALSE)) {
				return false;
			}

			// threat as integer
			return new Integer(value) != 0;

		} catch (Exception e) {
			throw new RuntimeError("Unparseable boolean: %s", value);
		}
	}

	public static boolean getBoolean(Node parent, String xpath, boolean defaultValue) {

		Boolean result = getBoolean(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static Integer getInteger(Node parent, String xpath) {

		String value = getString(parent, xpath);

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new Integer(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable integer: %s", value);
		}
	}

	public static int getInteger(Node parent, String xpath, int defaultValue) {

		Integer result = getInteger(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static Long getLong(Node parent, String xpath) {

		String value = getString(parent, xpath);

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new Long(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable long: %s", value);
		}
	}

	public static long getLong(Node parent, String xpath, long defaultValue) {

		Long result = getLong(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static Float getFloat(Node parent, String xpath) {

		String value = getString(parent, xpath);

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new Float(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable float: %s", value);
		}
	}

	public static float getFloat(Node parent, String xpath, float defaultValue) {

		Float result = getFloat(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static Double getDouble(Node parent, String xpath) {

		String value = getString(parent, xpath);

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new Double(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable double: %s", value);
		}
	}

	public static double getDouble(Node parent, String xpath, double defaultValue) {

		Double result = getDouble(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static BigInteger getBigInteger(Node parent, String xpath) {

		String value = getString(parent, xpath);

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new BigInteger(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable BigInteger: %s", value);
		}
	}

	public static BigInteger getBigInteger(Node parent, String xpath, BigInteger defaultValue) {

		BigInteger result = getBigInteger(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static BigDecimal getBigDecimal(Node parent, String xpath) {

		String value = getString(parent, xpath);

		if (Text.isEmpty(value)) {
			return null;
		}

		try {

			return new BigDecimal(value);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable BigDecimal: %s", value);
		}
	}

	public static BigDecimal getBigDecimal(Node parent, String xpath, BigDecimal defaultValue) {

		BigDecimal result = getBigDecimal(parent, xpath);

		if (result != null) {
			return result;
		} else {
			return defaultValue;
		}
	}

	public static String getPrintedText(Object value) {

		if (value instanceof Date) {

			Date d = (Date) value;

			if (Cal.trunc(d).equals(d)) {
				return Cal.formatDate(d, Cal.DEFAULT_DATE_FORMAT);
			} else {
				return Cal.formatDate(d, Cal.DEFAULT_DATETIME_FORMAT);
			}

		} else if (value instanceof Float || value instanceof Double) {

			return String.format("%.2f", value);

		} else if (value instanceof BigDecimal) {

			return getPrintedText(Num.toDouble((BigDecimal) value));

		} else {

			return value.toString();
		}
	}

	public static void setNodeValue(Node node, String value) {

		node.setNodeValue(value);
	}

	public static void setObject(Node node, Object value) {

		if (value != null) {

			if (value instanceof Date) {
				setDateTime(node, (Date) value);
			} else {
				setString(node, value.toString());
			}
		}
	}

	public static void setText(Node node, String value) {

		node.setTextContent(value);
	}

	public static void setString(Node node, String value) {

		setText(node, value);
	}

	public static void setDate(Node node, Date value, String format) {

		setText(node, Cal.formatDate(value, format));
	}

	public static void setDate(Node node, Date value) {

		setDate(node, value, DATE_FORMAT);
	}

	public static void setDateTime(Node node, Date value) {

		setDate(node, value, DATETIME_FORMAT);
	}

	public static void setBoolean(Node node, Boolean value) {

		setText(node, value.toString());
	}

	public static void setInteger(Node node, Integer value) {

		setText(node, value.toString());
	}

	public static void setFloat(Node node, Float value) {

		setText(node, value.toString());
	}

	public static void setDouble(Node node, Double value) {

		setText(node, value.toString());
	}

	public static void setBigDecimal(Node node, BigDecimal value) {

		setText(node, value.toString());
	}

}
