package libj.dom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libj.debug.Log;
import libj.error.RuntimeException2;
import libj.sdo.SDOUtils;
import libj.utils.Bool;
import libj.utils.Cal;
import libj.utils.Math;
import libj.utils.Xml;

import org.w3c.dom.Node;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

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

	public abstract int size();

	public abstract boolean isList();

	public abstract boolean isHave(String name);

	public abstract DataNode get(String name);

	public abstract DataNode get(int index);

	public abstract DataNode set(String name, Object object);

	public abstract DataNode set(int index, Object object);

	public abstract void remove(String name);

	public abstract void remove(int index);

	public DataNode create(String name) {

		if (isHave(name)) {
			return this.get(name);
		} else {
			return createMap(name);
		}
	}

	public MapDataNode createMap(String name) {

		return new MapDataNode(this, name);
	}

	public ListDataNode createList(String name) {

		return new ListDataNode(this, name);
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

	public void print() {
		Log.print("%s=%s", this.name, toString());
	}

	protected DataNode throwNotApplicable(String methodName) {
		throw new RuntimeException2("Method is not applicable here: %s.%s", this.getClass().getSimpleName(), methodName);
	}

	public void toDataObject(DataObject bo) {

		Type type = bo.getType();
		Map<String, Property> propMap = SDOUtils.getTypePropMap(type);

		Log.trace("### %s: %s (%s) ###", this.getClass().getSimpleName(), this.getName(), type.getName());

		for (String propName : propMap.keySet()) {

			Property prop = propMap.get(propName);
			//Type propType = prop.getType();
			boolean isList = prop.isMany();
			boolean isContainer = prop.isContainment();

			Log.trace("propName=%s, isContainer=%b, isList=%b", propName, isContainer, isList);

			if (this.isHave(propName)) {

				DataNode childNode = this.get(propName);

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

					} else if (childNode instanceof MapDataNode) {

						childNode.toDataObject(bo.createDataObject(propName));

					} else {
						Log.debug("Could not link node: %s <-> %s", propName, this.getClass().getName());
					}

				} else {

					// not container
					bo.set(propName, childNode.getObject());
				}
			}
		}

	}

}
