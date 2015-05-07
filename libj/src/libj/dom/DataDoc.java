package libj.dom;

import java.util.List;
import java.util.Map;

import libj.debug.Log;
import libj.error.RuntimeException2;
import libj.error.Throw;
import libj.utils.Text;
import libj.utils.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DataDoc {

	private DataNode root;

	public DataDoc(DataNode rootNode) {

		root = rootNode;
	}

	public DataDoc(String rootName) {

		root = new MapDataNode(rootName);
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

	public DataDoc(Document doc) {
		parse(doc);
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

	public DataNode root() {
		return root;
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
			return root();
		}

		DataNode node = root();
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

	private DataNode parse(Node node) {

		DataNode xnode = null;
		String name = node.getNodeName();

		if (name != null) {

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				if (!node.hasChildNodes()) {

					// empty node
					xnode = new LeafDataNode(name, null);

				} else if (node.getChildNodes().getLength() == 1) {

					if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

						// text node
						Node textNode = node.getFirstChild();

						String text = textNode.getTextContent().trim();
						String type = Xml.getAttrValue(textNode.getParentNode(), "type");

						if (type != null) {

							try {
								xnode = new LeafDataNode(name, Class.forName(type), text);

							} catch (Exception e) {

								//Log.error(e);
								xnode = new LeafDataNode(name, text);
							}

						} else {
							xnode = new LeafDataNode(name, text);
						}
					}

				} else if (node.getChildNodes().getLength() > 1) {

					// list detection
					Node firstChild = null;
					Node lastChild = null;

					for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {

						if (child.getNodeType() == Node.ELEMENT_NODE) {

							if (firstChild == null) {
								firstChild = child;
							} else {
								lastChild = child;
							}
						}
					}

					// list flag
					boolean isList = firstChild != null && lastChild != null && firstChild != lastChild
							&& firstChild.getNodeName().equals(lastChild.getNodeName());

					if (isList) {

						String itemName = firstChild.getNodeName();
						ListDataNode xlist = new ListDataNode(name, itemName);

						Log.debug("List detected: %s[%s]", name, itemName);

						for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {

							if (child.getNodeType() == Node.ELEMENT_NODE) {

								xlist.add(parse(child));
							}
						}

						xnode = xlist;

					} else {

						MapDataNode xmap = new MapDataNode(name);

						for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {

							if (child.getNodeType() == Node.ELEMENT_NODE) {

								xmap.set(child.getNodeName(), parse(child));
							}
						}

						xnode = xmap;
					}
				}

				// xml attributes
				if (xnode != null && node.hasAttributes()) {

					NamedNodeMap attrs = node.getAttributes();

					for (int i = 0; i < attrs.getLength(); i++) {
						xnode.setAttribute(attrs.item(i));
					}
				}
			}
		}

		return xnode;
	}

	public void parse(Document doc) {

		this.root = parse(Xml.getRoot(doc));
	}

	public void parse(String xml) {

		parse(Xml.parse(xml));
	}

	private Node serialize(Node parent, DataNode xnode) {

		Node node = null;

		if (parent == null) {
			Throw.runtimeException("parent is null");
		} else if (xnode == null) {
			return null;
		}

		String nodeName = xnode.getName();

		if (nodeName == null || nodeName.isEmpty()) {

			Log.debug("parentName=%s", parent.getNodeName());
			Log.debug("xnodeName=%s, xnodeType=%s", xnode.getName(), xnode.getType());

			Throw.runtimeException("[%s] Child node name cannot be empty", parent.getNodeName());
		}

		// this node
		node = Xml.createChild(parent, nodeName);

		// parse
		if (xnode instanceof MapDataNode) {

			MapDataNode xmap = (MapDataNode) xnode;

			for (String name : xmap.map().keySet()) {

				serialize(node, xmap.get(name));
			}

		} else if (xnode instanceof ListDataNode) {

			ListDataNode xlist = (ListDataNode) xnode;

			for (DataNode item : xlist.list()) {

				serialize(node, item);
			}

		} else {

			Xml.setObject(node, xnode.getObject());
			Xml.setAttrValue(node, "type", xnode.getType());
		}

		// attributes
		if (xnode.hasAttributes()) {

			Map<String, String> attr = xnode.getAttributes();

			for (String attrName : attr.keySet()) {

				Xml.setAttrValue(node, attrName, attr.get(attrName));
			}
		}

		return node;
	}

	public Document serialize() {

		Document doc = Xml.createDocument();
		serialize(doc, root);

		return doc;
	}

	public String getXML() {

		return Xml.serialize(serialize());
	}

	public void printXML() {

		Log.print(getXML());
	}

}
