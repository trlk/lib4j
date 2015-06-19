package libj.dom;

import java.io.InputStream;
import java.util.List;

import libj.debug.Log;
import libj.debug.Trace;
import libj.error.RuntimeException2;
import libj.error.Throw;
import libj.utils.Stream;
import libj.utils.Text;
import libj.utils.Xml;
import libj.xml.XMLSchema;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import commonj.sdo.DataObject;

public class XDocument extends XNode {

	private XDataNode root;

	public XDocument(XDataNode rootNode) {

		setRoot(rootNode);
	}

	public XDocument(String rootName) {

		setRoot(new XMapNode(this, rootName));
	}

	public XDocument(Document doc) {

		parse(doc);
	}

	public XDocument(byte[] xmlBytes) {

		parse(xmlBytes);
	}

	public XDocument(InputStream xmlStream) {

		parse(xmlStream);
	}

	@SuppressWarnings("rawtypes")
	public XDocument(String rootName, List rootList) {

		setRoot(new XListNode(this, rootName, rootList));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public XDocument(String rootName, Class rootClass) throws Exception {

		// check for appropriate class
		if (!XDataNode.class.isAssignableFrom(rootClass)) {
			throw new RuntimeException2("Incompatible class: %s", rootClass.getSimpleName());
		}

		// create root
		setRoot((XDataNode) rootClass.getConstructor(String.class).newInstance(rootName));
	}

	@Override
	public XDocument clone() throws CloneNotSupportedException {

		XDocument clone = (XDocument) super.clone();

		clone.setRoot(this.root.clone());

		return clone;
	}

	public XDataNode root() {
		return getRoot();
	}

	public XDataNode getRoot() {
		return root;
	}

	public void setRoot(XDataNode rootNode) {

		root = rootNode;
		root.setParent(this);
	}

	public String getRootName() {

		return root.getName();
	}

	public XDataNode get(String name) {

		return root.get(name);
	}

	public XDataNode get(int index) {

		return root.get(index);
	}

	public XDataNode set(String name, Object object) {

		return root.set(name, object);
	}

	public XDataNode set(int index, Object object) {

		return root.set(index, object);
	}

	public XDataNode eval(String expr) {

		if (expr.charAt(0) != '/') {
			Throw.runtimeException("Evaluation error: %s", expr);
		} else {
			expr = Text.substr(expr, 2);
		}

		// split by "/"
		String[] parts = expr.split("/");

		// root name check
		if (!getRootName().equals(parts[0])) {
			Throw.runtimeException("Root node evaluation error: %d:%s", 0, parts[0]);
		} else if (parts.length == 0) {
			return this.root;
		}

		XDataNode node = this.root;
		for (int i = 1; i < parts.length; i++) {

			String name = parts[i];

			if (name.contains("[")) {

				int index = Integer.parseInt(name.split("\\[")[1].split("\\]")[0]);
				node = node.get(name.split("\\[")[0]).get(index);

			} else {
				node = node.get(name);
			}
		}

		return node;
	}

	private XDataNode parse(XNode parent, Node node) {

		XDataNode dataNode = null;
		String nodeName = node.getNodeName();

		if (Trace.isEnabled() && node != null) {
			Trace.point(Text.printf("%s=%s", Xml.getNodeXPath(node), node.toString()));
		}

		if (Text.isNotEmpty(nodeName)) {

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				if (!node.hasChildNodes()) {

					// empty node
					dataNode = new XLeafNode(parent, nodeName, null);

				} else if (node.getChildNodes().getLength() == 1
						&& node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

					// text node
					Node textNode = node.getFirstChild();

					String text = textNode.getTextContent().trim();
					String type = Xml.getAttrValue(textNode.getParentNode(), Xml.TAG_TYPE);

					if (type != null) {

						try {

							dataNode = new XLeafNode(parent, nodeName, XMLSchema.getClass(type), text);

						} catch (Exception e) {

							//Log.error(e);
							dataNode = new XLeafNode(parent, nodeName, text);
						}

					} else {
						dataNode = new XLeafNode(parent, nodeName, text);
					}
				}

				else {

					dataNode = new XMapNode(parent, nodeName);

					for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {

						String childName = child.getNodeName();

						if (child.getNodeType() == Node.ELEMENT_NODE) {

							boolean isList = dataNode.isHas(childName);

							if (isList) {

								Log.trace("List detected: %s/%s[]", nodeName, childName);

								XDataNode childNode = dataNode.get(childName);

								if (childNode.isList()) {

									// add
									childNode.set(childNode.size(), parse(childNode, child));

								} else {

									// convert to list
									XListNode listNode = new XListNode(parent, childNode);
									dataNode.set(childName, listNode);
									listNode.add(parse(listNode, child));
								}

							} else {
								dataNode.set(childName, parse(dataNode, child));
							}
						}
					}
				}

				// xml attributes
				if (dataNode != null && node.hasAttributes()) {

					NamedNodeMap attrs = node.getAttributes();

					for (int i = 0; i < attrs.getLength(); i++) {
						dataNode.setAttribute(attrs.item(i));
					}
				}
			}
		}

		return dataNode;
	}

	public void parse(Document doc) {

		root = parse(this, Xml.getRootNode(doc));
	}

	public void parse(InputStream inputStream) {

		parse(Xml.parse(inputStream));
	}

	public void parse(String xml) {

		parse(Xml.parse(xml));
	}

	public void parse(byte[] bytes) {

		parse(Stream.newInputStream(bytes));
	}

	public Document serialize() {

		return root.serialize();
	}

	public String toXML() {

		return root.toXML();
	}

	public String toString() {

		return Text.sprintf("%s=%s", this.root.getName(), this.root.toString());
	}

	public void toDataObject(DataObject bo) {

		root.toDataObject(bo);
	}

	public void print() {

		Log.print(this.toString());
	}

	public void printXML() {

		Log.print(this.toXML());
	}

}
