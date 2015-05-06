package libj.xml;

import java.util.List;
import java.util.Map;

import libj.debug.Log;
import libj.error.Throw;
import libj.utils.Text;
import libj.utils.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Xdoc {

	private Xnode root;

	public Xdoc(String rootName) {

		root = new Xmap(rootName);
	}

	public Xdoc(String rootName, Object rootValue) {

		root = new Xleaf(rootName, rootValue);
	}

	@SuppressWarnings("rawtypes")
	public Xdoc(String rootName, List list) {

		root = new Xlist(rootName, list);
	}

	public Xdoc(Document doc) {
		parse(doc);
	}

	public Xnode getRoot() {
		return root;
	}

	public String getRootName() {
		return root.getName();
	}

	public Xnode get(String name) {
		return root.get(name);
	}

	public Xnode get(int index) {
		return root.get(index);
	}

	public Xnode set(String name, Object object) {
		return root.set(name, object);
	}

	public Xnode set(int index, Object object) {
		return root.set(index, object);
	}

	public Xnode root() {
		return root;
	}

	public Xnode eval(String expr) {

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
		}

		Xnode node = getRoot();
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

	private Xnode parse(Node node) {

		Xnode xnode = null;
		String name = node.getNodeName();

		if (name != null) {

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				if (!node.hasChildNodes()) {

					// empty node
					xnode = new Xleaf(name, null);

				} else if (node.getChildNodes().getLength() == 1) {

					if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

						// text node
						Node textNode = node.getFirstChild();

						String text = textNode.getTextContent().trim();
						String type = Xml.getAttrValue(textNode.getParentNode(), "type");

						if (type != null) {

							try {
								xnode = new Xleaf(name, Class.forName(type), text);

							} catch (Exception e) {

								//Log.error(e);
								xnode = new Xleaf(name, text);
							}

						} else {
							xnode = new Xleaf(name, text);
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
						Xlist xlist = new Xlist(name, itemName);

						Log.debug("List detected: %s[%s]", name, itemName);

						for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {

							if (child.getNodeType() == Node.ELEMENT_NODE) {

								xlist.add(parse(child));
							}
						}

						xnode = xlist;

					} else {

						Xmap xmap = new Xmap(name);

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

	private Node serialize(Node parent, Xnode xnode) {

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
		if (xnode instanceof Xmap) {

			Xmap xmap = (Xmap) xnode;

			for (String name : xmap.map().keySet()) {

				serialize(node, xmap.get(name));
			}

		} else if (xnode instanceof Xlist) {

			Xlist xlist = (Xlist) xnode;

			for (Xnode item : xlist.list()) {

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
