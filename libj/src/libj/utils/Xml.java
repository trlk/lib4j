package libj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import libj.error.Raise;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Xml {

	// defaults
	public static Integer DEFAULT_INDENT = 2;
	public static Boolean DEFAULT_PUT_EMPTY_NODES = false;
	public static char ELEMENT_DELIMITER = '.';
	public static char ATTRIBUTE_DELIMITER = ':';
	public static String ATTR_TYPE = "type";
	public static String ATTR_INDEX = "index";
	public static String TAG_ITEM = "item";
	public static String TAG_ITEMS = "items";

	public static Document parse(InputStream inputStream) {

		try {

			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();

			return b.parse(inputStream);
		}

		catch (Exception e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}

	public static void serialize(Document document, StreamResult streamResult) {

		try {

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			transformerFactory.setAttribute("indent-number", DEFAULT_INDENT);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource domSource = new DOMSource(document);
			transformer.transform(domSource, streamResult);

		} catch (Exception e) {
			e.printStackTrace();
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
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();

			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);

			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	public static String getAttributeValue(Node node, String attrName) {

		Node attr = getAttribute(node, attrName);

		if (attr != null)
			return attr.getNodeValue();
		else
			return null;
	}

	public static String getNodePath(Node node, char delimiter) {

		if (node == null)
			return null;

		if (delimiter == 0)
			Raise.runtimeException("%s: delimiter is null",
					App.thisMethodName());

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
				if (nodeName == TAG_ITEM) {

					// index attribute
					String nodeIndex = getAttributeValue(n, ATTR_INDEX);

					if (nodeIndex != null) {

						nodeName = Text.printf("%s[%s]", nodeName, nodeIndex);
					}
				}

				// build path
				if (path == null)
					path = nodeName;
				else
					path = nodeName + delimiter + path;
			}
		}

		if (delimiter == '/')
			path = delimiter + path;

		return path;
	}

	public static String getNodePath(Node node) {

		return getNodePath(node, ELEMENT_DELIMITER);
	}

	private static Map<String, String> createMap(Node node,
			Boolean putEmptyNodes) {

		Map<String, String> map = new HashMap<String, String>();

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			String path = getNodePath(node, ELEMENT_DELIMITER);

			if (path != null) {

				if (node.hasChildNodes()) {

					if (node.getChildNodes().getLength() == 1
							&& node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

						Node textNode = node.getFirstChild();

						String text = textNode.getTextContent().trim();

						if (putEmptyNodes || text.length() != 0) {

							map.put(path, text);

							// xml attributes
							if (node.hasAttributes()) {

								NamedNodeMap attrs = node.getAttributes();

								for (int i = 0; i < attrs.getLength(); i++) {

									Node attr = attrs.item(i);

									String attrPath = path
											+ ATTRIBUTE_DELIMITER
											+ attr.getNodeName();

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
		for (Node child = node.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			map.putAll(createMap(child, putEmptyNodes));
		}

		return map;
	}

	public static Map<String, String> createMap(Document doc,
			Boolean putEmptyNodes) {

		return createMap(doc.getDocumentElement(), putEmptyNodes);
	}

	public static Map<String, String> createMap(Document doc) {

		return createMap(doc, DEFAULT_PUT_EMPTY_NODES);
	}

	public static Map<String, String> createMap(InputStream inputStream,
			Boolean putEmptyNodes) {

		try {

			DocumentBuilder b = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = b.parse(inputStream);

			return createMap(doc, putEmptyNodes);
		}

		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, String> createMap(InputStream inputStream) {
		return createMap(inputStream, DEFAULT_PUT_EMPTY_NODES);
	}

	@SuppressWarnings("all")
	public static ArrayList<Map> createMapList(Document doc,
			Boolean putEmptyNodes) {

		ArrayList<Map> list = new ArrayList<Map>();

		try {

			// DocumentBuilder b = DocumentBuilderFactory.newInstance()
			// .newDocumentBuilder();

			// Document doc = b.parse(inputStream);
			Element root = doc.getDocumentElement();

			NodeList items = root.getChildNodes();

			if (items != null && items.getLength() > 0) {

				for (int i = 0; i < items.getLength(); i++) {

					if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {

						Map<String, String> map = new HashMap<String, String>();

						Element item = (Element) items.item(i);

						// parse nodes
						for (Node node = item.getFirstChild(); node != null; node = node
								.getNextSibling()) {

							if (node.getNodeType() == Node.ELEMENT_NODE) {
								map.put(node.getNodeName(),
										node.getTextContent());
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
}
