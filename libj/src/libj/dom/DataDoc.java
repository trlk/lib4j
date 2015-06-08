package libj.dom;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import libj.debug.Log;
import libj.debug.Stack;
import libj.debug.Trace;
import libj.error.RuntimeException2;
import libj.error.Throw;
import libj.utils.Stream;
import libj.utils.Text;
import libj.utils.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import commonj.sdo.DataObject;

public class DataDoc {

	protected DataNode root;

	public DataDoc(DataNode rootNode) {

		root = rootNode;
	}

	public DataDoc(String rootName) {

		root = new MapDataNode(rootName);
	}

	public DataDoc(Document doc) {

		parse(doc);
	}

	public DataDoc(byte[] xmlBytes) {

		parse(xmlBytes);
	}

	public DataDoc(InputStream xmlStream) {

		parse(xmlStream);
	}

	@SuppressWarnings("rawtypes")
	public DataDoc(String rootName, List rootList) {

		root = new ListDataNode(rootName, rootList);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataDoc(String rootName, Class rootClass) throws Exception {

		// check for appropriate class
		if (!DataNode.class.isAssignableFrom(rootClass)) {
			throw new RuntimeException2("Incompatible class: %s", rootClass.getSimpleName());
		}

		// create root
		root = (DataNode) rootClass.getConstructor(String.class).newInstance(rootName);
	}

	public DataNode getRoot() {
		return root;
	}

	public String getRootName() {
		return root.getName();
	}

	public DataNode get(String name) {
		return root.get(name);
	}

	public DataNode get(int index) {
		return root.get(index);
	}

	public DataNode set(String name, Object object) {
		return root.set(name, object);
	}

	public DataNode set(int index, Object object) {
		return root.set(index, object);
	}

	public DataNode eval(String expr) {

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

		DataNode node = this.root;
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

	private void serialize(DataNode dataNode, Node target) {

		if (dataNode == null || target == null) {
			throw new RuntimeException2("Invalid arguments for %s()", Stack.thisMethodName());
		}

		String nodeName = dataNode.getName();

		if (nodeName == null || nodeName.isEmpty()) {
			throw new RuntimeException2("Node name can't be empty: %s (%s)", target.getNodeName(), dataNode.getType());
		}

		// parse
		if (dataNode instanceof MapDataNode) {

			for (String childName : ((MapDataNode) dataNode).map().keySet()) {

				DataNode childNode = dataNode.get(childName);

				if (childNode.isList()) {
					serialize(childNode, target);
				} else {
					serialize(childNode, Xml.createChild(target, childName));
				}
			}

		} else if (dataNode instanceof ListDataNode) {

			ListDataNode listNode = (ListDataNode) dataNode;

			for (DataNode item : listNode.list()) {

				serialize(item, Xml.createChild(target, nodeName));
			}

		} else {

			Xml.setObject(target, dataNode.getObject());
			Xml.setAttrValue(target, "type", dataNode.getType());
		}

		// attributes
		if (dataNode.hasAttributes()) {

			Map<String, String> attr = dataNode.getAttributes();

			for (String attrName : attr.keySet()) {

				Xml.setAttrValue(target, attrName, attr.get(attrName));
			}
		}
	}

	public Document serialize() {

		Document doc = Xml.createDocument();
		Node rootNode = Xml.createChild(doc, root.getName());
		serialize(root, rootNode);

		return doc;
	}

	private DataNode parse(Node node) {

		DataNode dataNode = null;
		String nodeName = node.getNodeName();

		if (Trace.isEnabled()) {
			Trace.point(Xml.getNodeXPath(node));
		}

		if (Text.isNotEmpty(nodeName)) {

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				if (!node.hasChildNodes()) {

					// empty node
					dataNode = new LeafDataNode(nodeName, null);

				} else if (node.getChildNodes().getLength() == 1
						&& node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

					// text node
					Node textNode = node.getFirstChild();

					String text = textNode.getTextContent().trim();
					String type = Xml.getAttrValue(textNode.getParentNode(), "type");

					if (type != null) {

						try {
							dataNode = new LeafDataNode(nodeName, Class.forName(type), text);

						} catch (Exception e) {

							//Log.error(e);
							dataNode = new LeafDataNode(nodeName, text);
						}

					} else {
						dataNode = new LeafDataNode(nodeName, text);
					}
				}

				else {

					dataNode = new MapDataNode(nodeName);

					for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {

						String childName = child.getNodeName();

						if (child.getNodeType() == Node.ELEMENT_NODE) {

							boolean isList = dataNode.isHave(childName);

							if (isList) {

								Log.trace("List detected: %s/%s[]", nodeName, childName);

								DataNode childNode = dataNode.get(childName);

								if (childNode.isList()) {

									// add
									childNode.set(childNode.size(), parse(child));

								} else {

									// convert to list
									ListDataNode listNode = new ListDataNode(childNode);
									dataNode.set(childName, listNode);
									listNode.add(parse(child));
								}

							} else {
								dataNode.set(childName, parse(child));
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

		this.root = parse(Xml.getRoot(doc));
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

	public String toXML() {

		return Xml.serialize(serialize());
	}

	public String toString() {

		return Text.sprintf("%s=%s", this.root.getName(), this.root.toString());
	}

	public void toDataObject(DataObject bo) {

		this.getRoot().toDataObject(bo);
	}

	public void print() {

		Log.print(this.toString());
	}

	public void printXML() {

		Log.print(this.toXML());
	}

}
