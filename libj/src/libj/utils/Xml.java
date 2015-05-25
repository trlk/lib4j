package libj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import libj.debug.Debug;
import libj.error.Throw;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Xml {

	// defaults
	@Deprecated
	public static char ATTR_DELIMITER = ':';

	@Deprecated
	public static char ELEMENT_DELIMITER = '.';

	@Deprecated
	public static boolean THROW_NOT_FOUND = false;

	@Deprecated
	public static boolean PUT_EMPTY_NODES = Debug.isEnabled();

	// constants
	public static char XPATH_DELIMITER = '/';
	public static final String ATTR_NAME_TYPE = "type";
	public static final String ATTR_NAME_INDEX = "index";
	public static final String TAG_NAME_ITEM = "item";
	public static final String TAG_NAME_ITEMS = "items";
	public static final int INDENT_LENGTH = 2;

	// xml date/time format
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static Document parse(InputStream inputStream) {

		try {

			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();

			return b.parse(inputStream);

		} catch (Exception e) {
			e.printStackTrace(System.err);
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
			e.printStackTrace(System.err);
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
			e.printStackTrace(System.err);
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
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}

	public static String getNodePath(Node node, char delimiter) {

		if (node == null)
			return null;

		if (delimiter == 0) {
			Throw.runtimeException("%s: delimiter cannot be null", Debug.thisMethodName());
		}

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
						nodeName = Text.printf("%s[%s]", nodeName, nodeIndex);
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
	}

	public static String getNodePath(Node node) {

		return getNodePath(node, ELEMENT_DELIMITER);
	}

	public static String getNodeXPath(Node node) {

		return getNodePath(node, XPATH_DELIMITER);
	}

	private static Map<String, String> createMap(Node node, Boolean putEmptyNodes, char elementDelimiter,
			char attrDelimiter) {

		Map<String, String> map = new HashMap<String, String>();

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			String path = getNodePath(node, elementDelimiter);

			if (path != null) {

				if (node.hasChildNodes()) {

					if (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

						Node textNode = node.getFirstChild();

						String text = textNode.getTextContent().trim();

						if (putEmptyNodes || text.length() != 0) {

							map.put(path, text);

							// xml attributes
							if (node.hasAttributes()) {

								NamedNodeMap attrs = node.getAttributes();

								for (int i = 0; i < attrs.getLength(); i++) {

									Node attr = attrs.item(i);

									String attrPath = path + attrDelimiter + attr.getNodeName();

									map.put(attrPath, attr.getNodeValue());
								}
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

	public static Map<String, String> createMap(Document doc, char elementDelimiter) {

		return createMap(doc.getDocumentElement(), PUT_EMPTY_NODES, elementDelimiter, ATTR_DELIMITER);
	}

	public static Map<String, String> createMap(Document doc, char elementDelimiter, char attrDelimiter) {

		return createMap(doc.getDocumentElement(), PUT_EMPTY_NODES, elementDelimiter, attrDelimiter);
	}

	public static Map<String, String> createMap(Document doc) {

		return createMap(doc, PUT_EMPTY_NODES);
	}

	public static Map<String, String> createMap(Document doc, Boolean putEmptyNodes) {

		return createMap(doc, putEmptyNodes, ELEMENT_DELIMITER, ATTR_DELIMITER);
	}

	public static Map<String, String> createMap(InputStream inputStream, Boolean putEmptyNodes) {

		try {

			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = b.parse(inputStream);

			return createMap(doc, putEmptyNodes);
		}

		catch (Exception e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}

	public static Map<String, String> createMap(InputStream inputStream) {
		return createMap(inputStream, PUT_EMPTY_NODES);
	}

	@SuppressWarnings("rawtypes")
	public static ArrayList<Map> createMapList(Document doc, Boolean putEmptyNodes) {

		ArrayList<Map> list = new ArrayList<Map>();

		try {

			// Document doc = b.parse(inputStream);
			Element root = doc.getDocumentElement();

			NodeList items = root.getChildNodes();

			if (items != null && items.getLength() > 0) {

				for (int i = 0; i < items.getLength(); i++) {

					if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {

						Map<String, String> map = new HashMap<String, String>();

						Element item = (Element) items.item(i);

						// parse nodes
						for (Node node = item.getFirstChild(); node != null; node = node.getNextSibling()) {

							if (node.getNodeType() == Node.ELEMENT_NODE) {
								map.put(node.getNodeName(), node.getTextContent());
							}
						}

						list.add(map);
					}
				}
			}

			return list;
		}

		catch (Exception e) {
			e.printStackTrace(System.err);
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
			Throw.runtimeException(e);
		}

		return null;
	}

	public static Node createDocument(String rootName) {

		Document doc = createDocument();

		return createChild(doc, rootName);
	}

	public static Node getRoot(Document doc) {
		return doc.getDocumentElement();
	}

	public static Node getChild(Node parentNode, String childName) {

		return extractNode(parentNode, childName);
	}

	public static Node getAttribute(Node node, String attrName) {

		if (node.hasAttributes()) {

			NamedNodeMap attrs = node.getAttributes();

			for (int i = 0; i < attrs.getLength(); i++) {

				Node attr = attrs.item(i);

				if (attr.getNodeName() == attrName)
					return attr;
			}
		}

		return null;
	}

	public static String getAttrValue(Node node, String attrName) {

		Node attr = getAttribute(node, attrName);

		if (attr != null)
			return attr.getNodeValue();
		else
			return Text.EMPTY_STRING;
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

	public static NodeList extractList(Node node, String xpath) {

		NodeList result = null;

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();

			result = (NodeList) xPath.evaluate(xpath, node, XPathConstants.NODESET);

		} catch (XPathExpressionException e) {
			Throw.runtimeException(e);
		}

		return result;
	}

	public static NodeList extractList(Document doc, String xpath) {

		return extractList(doc.getDocumentElement(), xpath);
	}

	public static Node extractNode(Node node, String xpath) {

		NodeList nodes = extractList(node, xpath);

		if (nodes.getLength() == 0) {

			if (THROW_NOT_FOUND) {
				Throw.runtimeException("Node not found");
			} else {
				return null;
			}

		} else if (nodes.getLength() > 1) {

			Throw.runtimeException("More then one nodes found");
		}

		return nodes.item(0);
	}

	public static Node extractNode(Document doc, String xpath) {

		return extractNode(doc.getDocumentElement(), xpath);
	}

	public static String getText(Node node, String xpath, String defaultText) {

		Node textNode = extractNode(node, xpath);

		if (textNode != null)
			return textNode.getTextContent();
		else
			return defaultText;
	}

	public static String getText(Node node, String xpath) {

		return getText(node, xpath, Text.EMPTY_STRING);
	}

	public static String getString(Node node, String xpath) {

		return getText(node, xpath);
	}

	public static Date getDate(Node node, String xpath, String format) {

		return Cal.toDate(getString(node, xpath), format);
	}

	public static Date getDate(Node node, String xpath) {

		return getDate(node, xpath, DATE_FORMAT);
	}

	public static Date getDateTime(Node node, String xpath) {

		return getDate(node, xpath, DATETIME_FORMAT);
	}

	public static Boolean getBoolean(Node node, String xpath) {

		String value = getString(node, xpath);

		String trueText = ((Boolean) true).toString();
		String falseText = ((Boolean) false).toString();

		try {

			if (value.equalsIgnoreCase(trueText))
				return true;

			if (value.equalsIgnoreCase(falseText))
				return false;

			// threat as integer
			return new Integer(value) != 0;

		} catch (Exception e) {

			Throw.runtimeException("Unparseable boolean: %s", value);
		}

		return null;
	}

	public static Integer getInteger(Node node, String xpath) {

		String value = getString(node, xpath);

		try {

			return new Integer(value);

		} catch (Exception e) {

			Throw.runtimeException("Unparseable integer: %s", value);
		}

		return null;
	}

	public static Float getFloat(Node node, String xpath) {

		String value = getString(node, xpath);

		try {

			return new Float(value);

		} catch (Exception e) {

			Throw.runtimeException("Unparseable float: %s", value);
		}

		return null;
	}

	public static Double getDouble(Node node, String xpath) {

		String value = getString(node, xpath);

		try {
			return new Double(value);

		} catch (Exception e) {
			Throw.runtimeException("Unparseable double: %s", value);
		}

		return null;
	}

	public static BigDecimal getBigDecimal(Node node, String xpath) {

		String value = getString(node, xpath);

		try {
			return new BigDecimal(value);

		} catch (Exception e) {
			Throw.runtimeException("Unparseable bigDecimal: %s", value);
		}

		return null;
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
