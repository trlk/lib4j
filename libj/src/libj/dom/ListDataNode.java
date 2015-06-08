package libj.dom;

import java.util.ArrayList;
import java.util.List;

import libj.debug.Stack;
import libj.error.RuntimeException2;

public class ListDataNode extends DataNode {

	private ListDataNode() {

		super(new ArrayList<DataNode>());
	}

	public ListDataNode(String name) {

		this();
		setName(name);
	}

	public ListDataNode(DataNode parent, String name) {

		this(name);
		parent.set(name, this);
	}

	public ListDataNode(DataNode dataNode) {

		this(dataNode.name);

		if (dataNode.isList()) {
			throwNotApplicable(Stack.thisMethodName());
		} else {
			add(dataNode);
		}
	}

	@SuppressWarnings("rawtypes")
	public ListDataNode(String name, List list) {

		this(name);

		for (Object object : list) {
			add(object);
		}
	}

	@SuppressWarnings("unchecked")
	public List<DataNode> list() {

		return (List<DataNode>) this.getObject();
	}

	public int size() {
		return list().size();
	}

	public boolean isList() {
		return true;
	}

	public boolean isHave(String name) {
		throwNotApplicable(Stack.thisMethodName());
		return false;
	}

	public DataNode add(DataNode dataNode) {

		list().add(dataNode);

		return dataNode;
	}

	public DataNode add(Object object) {

		if (object instanceof DataNode) {
			return add((DataNode) object);
		} else {
			return add(new LeafDataNode(this.name, object));
		}
	}

	public DataNode get(int index) {

		return list().get(index);
	}

	public DataNode get(String name) {

		if (name == this.name) {
			return this;
		} else {
			return throwNotApplicable(Stack.thisMethodName());
		}
	}

	public DataNode get(String attrName, String attrValue) {

		for (DataNode item : list()) {

			if (item.getAttrValue(attrName) == attrValue) {
				return item;
			}
		}

		throw new RuntimeException2("Attribute not found: %s@%s=%s", this.name, attrName, attrValue);
	}

	public DataNode set(int index, DataNode dataNode) {

		if (list().size() == index) {
			list().add(dataNode);
		} else {
			list().set(index, dataNode);
		}

		return dataNode;
	}

	public DataNode set(int index, Object object) {

		if (object instanceof DataNode) {
			return set(index, (DataNode) object);
		} else {
			return set(index, new LeafDataNode(this.name, object));
		}
	}

	public DataNode set(String name, Object object) {
		return throwNotApplicable(Stack.thisMethodName());
	}

	public void remove(int index) {
		list().remove(index);
	}

	public void remove(String name) {
		throwNotApplicable(Stack.thisMethodName());
	}

}
