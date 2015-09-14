package libj.dom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libj.debug.Log;
import libj.debug.Stack;
import libj.error.RuntimeError;
import libj.sdo.SDOUtils;
import libj.utils.Bool;
import libj.utils.Cal;
import libj.utils.Num;
import libj.utils.Xml;
import libj.xml.XMLSchema;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public abstract class XDataNode extends XNode {

	protected String name;
	protected Object object;
	protected HashMap<String, String> attr;

	public XDataNode(XNode parent, String name) {

		setName(name);
		setParent(parent);

		this.attr = new HashMap<String, String>();
	}

	public XDataNode(XNode parent, String name, Object object) {

		this(parent, name);
		setObject(object);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public XDataNode clone() throws CloneNotSupportedException {

		XDataNode clone = (XDataNode) super.clone();

		if (object instanceof XNode) {
			clone.object = ((XNode) object).clone();
		} else {
			clone.object = object;
		}

		clone.attr = (HashMap) attr.clone();

		return clone;
	}

	public abstract boolean isHas(String name);

	public abstract boolean isSet();

	public boolean isSet(String name) {

		if (this.isHas(name)) {
			return this.get(name).isSet();
		} else {
			return false;
		}
	}

	public abstract boolean isList();

	public abstract int size();

	public abstract XDataNode get(String name);

	public abstract XDataNode get(int index);

	public abstract XDataNode set(String name, Object object);

	public abstract XDataNode set(int index, Object object);

	public abstract void remove(String name);

	public abstract void remove(int index);

	public XDataNode parent() {

		XNode parent = this.getParent();

		if (parent instanceof XDataNode) {
			return (XDataNode) parent;
		} else {
			throw new RuntimeError("Node '%s' has no parent", this.name);
		}
	}

	public XDataNode create(String name) {

		if (isHas(name)) {
			return this.get(name);
		} else {
			return createMap(name);
		}
	}

	public XMapNode createMap(String name) {

		XMapNode mapNode = new XMapNode(this, name);

		set(name, mapNode);

		return mapNode;
	}

	public XListNode createList(String name) {

		XListNode listNode = new XListNode(this, name);

		set(name, listNode);

		return listNode;
	}

	public String toXML() {

		return Xml.serialize(this.serialize());
	}

	@Override
	public String toString() {

		if (object != null) {
			return object.toString();
		} else {
			return null;
		}
	}

	public Date toDate(String format) {

		if (object instanceof Date) {
			return (Date) object;
		} else {
			return Cal.toDate(this.toString(), format);
		}
	}

	public Date toDate() {

		return toDate(Xml.DATE_FORMAT);
	}

	public Date toDateTime() {

		return toDate(Xml.DATETIME_FORMAT);
	}

	public int toInteger() {

		return Num.toInteger(this.toString());
	}

	public float toFloat() {

		return Num.toFloat(this.toString());
	}

	public double toDouble() {

		return Num.toDouble(this.toString());
	}

	public BigDecimal toBigDecimal() {

		return Num.toBigDecimal(this.toString());
	}

	public boolean toBoolean() {

		if (object instanceof Boolean) {
			return (Boolean) object;
		} else {
			return Bool.toBoolean(this.toString());
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {

		if (object != null) {
			return XMLSchema.getType(object);
		} else {
			return null;
		}
	}

	public Object getObject() {

		return object;
	}

	public void setObject(Object object) {

		this.object = object;
	}

	public void setObject(String name, Object object) {

		setName(name);
		setObject(object);
	}

	public Map<String, String> getAttributes() {
		return attr;
	}

	public boolean hasAttributes() {

		return !attr.isEmpty();
	}

	public boolean hasAttribute(String attrName) {

		return getAttrValue(attrName) != null;
	}

	public void setAttribute(Node attrNode) {
		setAttrValue(attrNode.getNodeName(), attrNode.getNodeValue());
	}

	public String getAttrValue(String attrName) {
		return attr.get(attrName);
	}

	public void setAttrValue(String attrName, String attrValue) {
		attr.put(attrName, attrValue);
	}

	// cast as list
	public XDataNode getList(String name) {

		XDataNode node = this.get(name);

		if (node.isList()) {

			return node;

		} else {

			XDataNode list = new XListNode(this, node);

			return this.set(name, list);
		}
	}

	public String getString(String name) {

		return get(name).toString();
	}

	public String getString(String name, String defaultValue) {

		if (isSet(name)) {
			return getString(name);
		} else {
			return defaultValue;
		}
	}

	public String getString(int index) {

		return get(index).toString();
	}

	public Date getDate(String name) {

		return get(name).toDate();
	}

	public Date getDate(int index) {

		return get(index).toDate();
	}

	public Date getDate(String name, String format) {

		return get(name).toDate(format);
	}

	public Date getDate(int index, String format) {

		return get(index).toDate(format);
	}

	public Date getDateTime(String name) {

		return get(name).toDateTime();
	}

	public Date getDateTime(int index) {

		return get(index).toDateTime();
	}

	public int getInt(String name) {

		return get(name).toInteger();
	}

	public int getInt(String name, int defaultValue) {

		if (isSet(name)) {
			return getInt(name);
		} else {
			return defaultValue;
		}
	}

	public int getInt(int index) {

		return get(index).toInteger();
	}

	public float getFloat(String name) {

		return get(name).toFloat();
	}

	public float getFloat(String name, int defaultValue) {

		if (isSet(name)) {
			return getFloat(name);
		} else {
			return defaultValue;
		}
	}

	public float getFloat(int index) {

		return get(index).toFloat();
	}

	public double getDouble(String name) {

		return get(name).toDouble();
	}

	public double getDouble(String name, double defaultValue) {

		if (isSet(name)) {
			return getDouble(name);
		} else {
			return defaultValue;
		}
	}

	public double getDouble(int index) {

		return get(index).toDouble();
	}

	public BigDecimal getBigDecimal(String name) {

		return get(name).toBigDecimal();
	}

	public BigDecimal getBigDecimal(int index) {

		return get(index).toBigDecimal();
	}

	public BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {

		if (isSet(name)) {
			return getBigDecimal(name);
		} else {
			return defaultValue;
		}
	}

	public boolean getBoolean(String name) {

		return get(name).toBoolean();
	}

	public boolean getBoolean(String name, boolean defaultValue) {

		if (isSet(name)) {
			return getBoolean(name);
		} else {
			return defaultValue;
		}
	}

	public boolean getBoolean(int index) {

		return get(index).toBoolean();
	}

	protected XDataNode throwNotApplicable(String methodName) {
		throw new RuntimeError("Method not applicable here: %s.%s", this.getClass().getSimpleName(), methodName);
	}

	protected XDataNode throwNotSupported(String methodName) {
		throw new RuntimeError("Method not supported: %s.%s", this.getClass().getSimpleName(), methodName);
	}

	private void serialize(XDataNode dataNode, Node target) {

		if (dataNode == null || target == null) {
			throw new RuntimeError("Invalid arguments for %s()", Stack.thisMethodName());
		}

		String nodeName = dataNode.getName();

		if (nodeName == null || nodeName.isEmpty()) {
			throw new RuntimeError("Node name can't be empty: %s (%s)", target.getNodeName(), dataNode.getType());
		}

		// parse
		if (dataNode instanceof XMapNode) {

			for (String childName : ((XMapNode) dataNode).map().keySet()) {

				XDataNode childNode = dataNode.get(childName);

				if (childNode.isList()) {
					serialize(childNode, target);
				} else {
					serialize(childNode, Xml.createChild(target, childName));
				}
			}

		} else if (dataNode instanceof XListNode) {

			XListNode listNode = (XListNode) dataNode;

			for (XDataNode item : listNode.list()) {

				serialize(item, Xml.createChild(target, nodeName));
			}

		} else {

			Xml.setObject(target, dataNode.getObject());
			Xml.setAttrValue(target, Xml.TAG_TYPE, dataNode.getType());
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
		Node rootNode = Xml.createChild(doc, this.getName());
		serialize(this, rootNode);

		return doc;
	}

	public void print() {
		Log.print("%s=%s", this.name, this.toString());
	}

	public void printXML() {
		Log.print(this.toXML());
	}

	public DataObject toDataObject(DataObject bo) {

		Type type = bo.getType();
		Map<String, Property> propMap = SDOUtils.getTypePropMap(type);

		Log.devel("### %s: %s (%s) ###", this.getClass().getSimpleName(), this.getName(), type.getName());

		for (String propName : propMap.keySet()) {

			Property prop = propMap.get(propName);
			//Type propType = prop.getType();
			boolean isList = prop.isMany();
			boolean isContainer = prop.isContainment();

			Log.devel("propName=%s, isContainer=%b, isList=%b", propName, isContainer, isList);

			if (this.isHas(propName)) {

				XDataNode childNode = this.get(propName);

				if (isContainer) {

					if (isList) {

						List<DataObject> list = new ArrayList<DataObject>();

						if (childNode.isList()) {

							for (int i = 0; i < childNode.size(); i++) {

								DataObject listItem = bo.createDataObject(propName);
								childNode.get(i).toDataObject(listItem);
								list.add(listItem);
							}

						} else {

							DataObject listItem = bo.createDataObject(propName);
							childNode.toDataObject(listItem);
							list.add(listItem);
						}

						bo.setList(propName, list);

					} else if (childNode instanceof XMapNode) {

						childNode.toDataObject(bo.createDataObject(propName));

					} else {
						Log.debug("Could not link node: %s <-> %s", propName, this.getClass().getName());
					}

				} else {

					// not container
					if (isList) {

						List<Object> list = new ArrayList<Object>();

						if (childNode.isList()) {

							for (int i = 0; i < childNode.size(); i++) {
								list.add(childNode.get(i));
							}

						} else {

							list.add(childNode.getObject());
						}

						bo.setList(propName, list);

					} else {

						bo.set(propName, childNode.getObject());
					}
				}
			}
		}

		return bo;
	}

}
