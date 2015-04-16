package libj.xml;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import libj.debug.Log;
import libj.error.Raise;
import libj.utils.Cal;
import libj.utils.Text;
import libj.utils.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Xmap {

	String name;
	HashMap<String, Object> map;

	public Xmap(String name) {

		this.name = name;
		this.map = new HashMap<String, Object>();
	}

	public Xmap(Document doc) {

		parse(doc);
	}

	public void set(String key, Object value) {

		map.put(key, value);
	}

	public Xmap create(String key) {

		Xmap node = new Xmap(key);
		this.set(key, node);

		return node;
	}

	@SuppressWarnings("rawtypes")
	public void setList(String key, List list) {

		map.put(key, list);
	}

	public Xmap get(String key) {

		return (Xmap) map.get(key);
	}

	public Object getObject(String key) {

		return map.get(key);
	}

	public String getString(String key) {

		return getObject(key).toString();
	}

	public Date getDate(String key, String format) {

		Object o = getObject(key);

		if (o instanceof Date) {
			return (Date) o;
		} else {
			return Cal.toDate(getString(key), format);
		}
	}

	public Date getDate(String key) {

		return getDate(key, Xml.DATE_FORMAT);
	}

	public Date getDateTime(String key) {

		return getDate(key, Xml.DATETIME_FORMAT);
	}

	@SuppressWarnings("rawtypes")
	public List getList(String key) {

		Object list = map.get(key);

		if (list instanceof List) {
		} else {
			Raise.runtimeException("Object %s is not list", key);
		}

		return (List) list;
	}

	@SuppressWarnings("rawtypes")
	private Node serialize(Node node, Xmap xmap) {

		for (String key : xmap.map.keySet()) {

			Object object = xmap.map.get(key);

			Node child = Xml.createChild(node, key);

			if (object instanceof Xmap) {

				serialize(child, (Xmap) object);

			} else if (object instanceof List) {

				for (Object o : (List) object) {

					Xml.setObject(child, o);
				}
			} else {

				Xml.setObject(child, object);
				Xml.setAttrValue(child, "type", object.getClass().getName());
			}

		}

		return node;
	}

	public Document serialize() {

		Document doc = Xml.createDocument();

		Node root = Xml.createChild(doc, name);

		serialize(root, this);

		return doc;
	}

	private HashMap<String, Object> parse(Node node) {

		HashMap<String, Object> map = new HashMap<String, Object>();

		boolean putEmptyNodes = true;

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			String name = node.getNodeName();

			if (name != null) {

				if (node.hasChildNodes()) {

					if (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {

						Node textNode = node.getFirstChild();

						String text = textNode.getTextContent().trim();

						String type = Xml.getAttrValue(textNode.getParentNode(), "type");

						Log.info("Hello %s %s", String.class.getName(), type);

						if (type != null) {

							try {

								if (type.equals(String.class.getName()))
 {
									Log.info("put %s", text);
									map.put(name, text);

								}

								text = Text.EMPTY_STRING;

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							Log.info(type);

						}

						if (putEmptyNodes || text.length() != 0) {

							map.put(name, text);

							// xml attributes
							/*
							if (node.hasAttributes()) {

								NamedNodeMap attrs = node.getAttributes();

								for (int i = 0; i < attrs.getLength(); i++) {

									Node attr = attrs.item(i);

									String attrPath = path + ATTR_DELIMITER + attr.getNodeName();

									map.put(attrPath, attr.getNodeValue());
								}
							} */
						}
					}

				} else if (putEmptyNodes) {
					map.put(name, new String());
				}
			}
		}

		// recursive parse child
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			map.putAll(parse(child));
		}

		return map;
	}

	public void parse(Document doc) {

		this.name = Xml.getRoot(doc).getNodeName();

		this.map = parse(Xml.getRoot(doc));
	}

}
