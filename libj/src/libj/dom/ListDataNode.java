package libj.dom;

import java.util.ArrayList;
import java.util.List;

import libj.error.RuntimeException2;

public class ListDataNode extends DataNode {

	private String itemName = "item";

	private ListDataNode() {

		super(new ArrayList<DataNode>());
	}

	public ListDataNode(String name) {

		this();
		setName(name);
	}

	public ListDataNode(String name, String itemName) {

		this(name);
		setItemName(itemName);
	}

	public ListDataNode(DataNode parent, String name) {

		this(name);
		parent.set(name, this);
	}

	public ListDataNode(DataNode parent, String name, String itemName) {

		this(name, itemName);
		parent.set(name, this);
	}

	@SuppressWarnings("rawtypes")
	public ListDataNode(String name, List list) {

		this(name);

		for (Object object : list) {
			add(object);
		}
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@SuppressWarnings("unchecked")
	public List<DataNode> list() {

		return (List<DataNode>) this.getObject();
	}

	public DataNode add(DataNode xnode) {

		list().add(xnode);

		return xnode;
	}

	public DataNode add(Object object) {
		
		if (object instanceof DataNode) {
			return add((DataNode) object);
		} else {
			return add(new LeafDataNode(this.itemName, object));
		}
	}

	public DataNode get(int index) {

		return list().get(index);
	}

	public DataNode set(int index, DataNode xnode) {

		if (list().size() == index) {
			list().add(xnode);
		} else {
			list().set(index, xnode);
		}

		return xnode;
	}

	public DataNode set(int index, Object object) {

		if (object instanceof DataNode) {
			return set(index, (DataNode) object);
		} else {
			return set(index, new LeafDataNode(this.itemName, object));
		}
	}

	public DataNode get(String name) {

		if (name == itemName) {
			return this;
		} else {
			throw new RuntimeException2("Cannot get by name from list: %s/%s", this.name, name);
		}
	}

	public DataNode get(String attrName, String attrValue) {

		for (DataNode item : list()) {

			if (item.getAttrValue(attrName) == attrValue) {
				return item;
			}
		}

		throw new RuntimeException2("[%s] %s not found in list (@%s=%s)", this.name, this.itemName, attrName, attrValue);
	}

	public DataNode set(String name, Object object) {
		throw new RuntimeException2("Cannot set by name within list: %s/%s", this.name, name);
	}

	public void remove(int index) {
		list().remove(index);
	}

}
