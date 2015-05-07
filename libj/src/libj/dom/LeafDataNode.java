package libj.dom;

import libj.error.RuntimeException2;

public class LeafDataNode extends DataNode {

	public LeafDataNode(String name, Object object) {

		super();

		if (object instanceof DataNode) {
			throw new RuntimeException2("Incompatible object class: %s", object.getClass()
					.getName());
		}

		setName(name);
		setObject(object);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LeafDataNode(String name, Class theClass, String value) throws Exception {

		this(name, theClass.getConstructor(String.class).newInstance(value));
	}

	public DataNode get(String name) {
		throw new RuntimeException2("Cannot get by name from leaf: %s/%s", this.name, name);
	}

	public DataNode get(int index) {
		throw new RuntimeException2("Cannot get by index from leaf: %s[%d]", this.name, index);
	}

	public DataNode set(String name, Object object) {
		throw new RuntimeException2("Cannot set by name within leaf: %s/%s", this.name, name);
	}

	public DataNode set(int index, Object object) {
		throw new RuntimeException2("Cannot set by index within leaf: %s[%d]", this.name, index);
	}

}
