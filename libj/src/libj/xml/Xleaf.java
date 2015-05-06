package libj.xml;

import libj.error.RuntimeException2;

public class Xleaf extends Xnode {

	public Xleaf(String name, Object object) {

		super();

		if (object instanceof Xnode) {
			throw new RuntimeException2("Xleaf cannot instantiate by '%s' class object", object.getClass()
					.getName());
		}

		setName(name);
		setObject(object);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Xleaf(String name, Class theClass, String value) throws Exception {

		this(name, theClass.getConstructor(String.class).newInstance(value));
	}

	public Xnode get(String name) {
		throw new RuntimeException2("Cannot get by name from leaf: %s/%s", this.name, name);
	}

	public Xnode get(int index) {
		throw new RuntimeException2("Cannot get by index from leaf: %s[%d]", this.name, index);
	}

	public Xnode set(String name, Object object) {
		throw new RuntimeException2("Cannot set by name within leaf: %s/%s", this.name, name);
	}

	public Xnode set(int index, Object object) {
		throw new RuntimeException2("Cannot set by index within leaf: %s[%d]", this.name, index);
	}

}
