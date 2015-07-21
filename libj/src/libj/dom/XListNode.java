package libj.dom;

import java.util.ArrayList;
import java.util.List;

import libj.debug.Stack;
import libj.error.RuntimeError;
import libj.sdo.SDOUtils;

import commonj.sdo.DataObject;

public class XListNode extends XDataNode {

	public XListNode(XNode parent, String name) {

		super(parent, name, new ArrayList<XDataNode>());
	}

	@SuppressWarnings("rawtypes")
	public XListNode(XNode parent, String name, List list) {

		this(parent, name);

		for (Object object : list) {
			add(object);
		}
	}

	protected XListNode(XNode parent, XDataNode dataNode) {

		this(parent, dataNode.name);

		if (dataNode.isList()) {
			throwNotApplicable(Stack.thisMethodName());
		} else {
			add(dataNode);
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public XListNode clone() throws CloneNotSupportedException {

		XListNode clone = (XListNode) super.clone();

		clone.object = ((ArrayList) object).clone();

		for (int i = 0; i < clone.size(); i++) {
			clone.set(i, this.get(i).clone());
		}

		return clone;
	}

	@SuppressWarnings("unchecked")
	public List<XDataNode> list() {

		return (List<XDataNode>) this.getObject();
	}

	public boolean isHas(String name) {

		throwNotApplicable(Stack.thisMethodName());
		return false;
	}

	public boolean isSet() {

		return list().size() != 0;
	}

	public boolean isList() {

		return true;
	}

	public int size() {
		return list().size();
	}

	public XDataNode add(XDataNode dataNode) {

		list().add(dataNode);

		dataNode.setParent(this);

		return dataNode;
	}

	public XDataNode add(Object object) {

		if (object instanceof XDataNode) {
			return add((XDataNode) object);
		} else {
			return add(new XLeafNode(this, this.name, object));
		}
	}

	public XDataNode get(int index) {

		return list().get(index);
	}

	public XDataNode get(String name) {

		if (name == this.name) {
			return this;
		} else {
			return throwNotApplicable(Stack.thisMethodName());
		}
	}

	public XDataNode get(String attrName, String attrValue) {

		for (XDataNode item : list()) {

			if (item.getAttrValue(attrName) == attrValue) {
				return item;
			}
		}

		throw new RuntimeError("Attribute not found: %s@%s=%s", this.name, attrName, attrValue);
	}

	public XDataNode set(int index, XDataNode dataNode) {

		if (list().size() == index) {
			list().add(dataNode);
		} else {
			list().set(index, dataNode);
		}

		dataNode.setParent(this);

		return dataNode;
	}

	public XDataNode set(int index, Object object) {

		if (object instanceof XDataNode) {
			return set(index, (XDataNode) object);
		} else if (object instanceof XDocument) {
			return set(index, ((XDocument) object).getRoot());
		} else if (object instanceof DataObject) {
			return set(index, SDOUtils.convertToDocument((DataObject) object).getRoot());
		} else {
			return set(index, new XLeafNode(this, this.name, object));
		}
	}

	public XDataNode set(String name, Object object) {
		return throwNotApplicable(Stack.thisMethodName());
	}

	public void remove(int index) {
		list().remove(index);
	}

	public void remove(String name) {
		throwNotApplicable(Stack.thisMethodName());
	}

	protected void append(String name, XDataNode child) {
		this.add(child);
	}

}
