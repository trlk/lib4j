package libj.xml;

import java.util.ArrayList;
import java.util.List;

import libj.error.RuntimeException2;

public class Xlist extends Xnode {

	private String itemName = "item";

	private Xlist() {

		super(new ArrayList<Xnode>());
	}

	public Xlist(String name) {

		this();
		setName(name);
	}

	public Xlist(String name, String itemName) {

		this(name);
		setItemName(itemName);
	}

	public Xlist(Xnode parent, String name) {

		this(name);
		parent.set(name, this);
	}

	public Xlist(Xnode parent, String name, String itemName) {

		this(name, itemName);
		parent.set(name, this);
	}

	@SuppressWarnings("rawtypes")
	public Xlist(String name, List list) {

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
	public List<Xnode> list() {

		return (List<Xnode>) this.getObject();
	}

	public Xnode add(Xnode xnode) {

		list().add(xnode);

		return xnode;
	}

	public Xnode add(Object object) {
		
		if (object instanceof Xnode) {
			return add((Xnode) object);
		} else {
			return add(new Xleaf(this.itemName, object));
		}
	}

	public Xnode get(int index) {

		return list().get(index);
	}

	public Xnode set(int index, Xnode xnode) {

		if (list().size() == index) {
			list().add(xnode);
		} else {
			list().set(index, xnode);
		}

		return xnode;
	}

	public Xnode set(int index, Object object) {

		if (object instanceof Xnode) {
			return set(index, (Xnode) object);
		} else {
			return set(index, new Xleaf(this.itemName, object));
		}
	}

	public Xnode get(String name) {

		if (name == itemName) {
			return this;
		} else {
			throw new RuntimeException2("Cannot get by name from list: %s/%s", this.name, name);
		}
	}

	public Xnode get(String attrName, String attrValue) {

		for (Xnode item : list()) {

			if (item.getAttrValue(attrName) == attrValue) {
				return item;
			}
		}

		throw new RuntimeException2("[%s] %s not found in list (@%s=%s)", this.name, this.itemName, attrName, attrValue);
	}

	public Xnode set(String name, Object object) {
		throw new RuntimeException2("Cannot set by name into list: %s/%s", this.name, name);
	}

	public void remove(int index) {
		list().remove(index);
	}

}
