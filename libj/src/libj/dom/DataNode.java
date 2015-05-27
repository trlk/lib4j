package libj.dom;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import libj.utils.Bool;
import libj.utils.Cal;
import libj.utils.Math;
import libj.utils.Xml;

import org.w3c.dom.Node;

public abstract class DataNode {

	protected String name;
	protected Object object;
	protected HashMap<String, String> attr;

	protected DataNode() {

		attr = new HashMap<String, String>();
	}

	protected DataNode(Object object) {

		this();
		this.object = object;
	}

	public abstract DataNode get(String name);

	public abstract DataNode get(int index);

	public abstract DataNode set(String name, Object object);

	public abstract DataNode set(int index, Object object);

	public MapDataNode create(String name) {

		return new MapDataNode(this, name);
	}

	public ListDataNode createList(String name) {

		return new ListDataNode(this, name);
	}

	public ListDataNode createList(String name, String itemName) {

		return new ListDataNode(this, name, itemName);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {

		if (object != null) {
			return object.getClass().getSimpleName();
		} else {
			return null;
		}
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

	public String getAttrValue(String attrName) {
		return attr.get(attrName);
	}

	public void setAttrValue(String attrName, String attrValue) {
		attr.put(attrName, attrValue);
	}

	public void setAttribute(Node attrNode) {
		setAttrValue(attrNode.getNodeName(), attrNode.getNodeValue());
	}

	public String toString() {

		return object.toString();
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

	public Integer toInteger() {

		return Math.toInteger(this.toString());
	}

	public Float toFloat() {

		return Math.toFloat(this.toString());
	}

	public Double toDouble() {

		return Math.toDouble(this.toString());
	}

	public BigDecimal toBigDecimal() {

		return Math.toBigDecimal(this.toString());
	}

	public Boolean toBoolean() {

		if (object instanceof Boolean) {
			return (Boolean) object;
		} else {
			return Bool.toBoolean(this.toString());
		}
	}

	public String getString(String name) {

		return get(name).toString();
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

	public Integer getInteger(String name) {

		return get(name).toInteger();
	}

	public Integer getInteger(int index) {

		return get(index).toInteger();
	}

	public Float getFloat(String name) {

		return get(name).toFloat();
	}

	public Float getFloat(int index) {

		return get(index).toFloat();
	}

	public Double getDouble(String name) {

		return get(name).toDouble();
	}

	public Double getDouble(int index) {

		return get(index).toDouble();
	}

	public BigDecimal getBigDecimal(String name) {

		return get(name).toBigDecimal();
	}

	public BigDecimal getBigDecimal(int index) {

		return get(index).toBigDecimal();
	}

	public Boolean getBoolean(String name) {

		return get(name).toBoolean();
	}

	public Boolean getBoolean(int index) {

		return get(index).toBoolean();
	}

}
