package libj.xml;

import libj.error.RuntimeException2;

public class Xleaf extends Xnode {

	public Xleaf(String name, Object object) {

		super();

		if (object instanceof Xnode) {
			throw new RuntimeException2("[%s] Xleaf cannot instantiate of xnode class object", object.getClass()
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
		throw new RuntimeException2("[%s] Cannot get object", name);
	}

	public Xnode get(int index) {
		throw new RuntimeException2("[%d] Cannot get object", index);
	}

	public Xnode set(String name, Object object) {
		throw new RuntimeException2("[%s] Cannot set object", name);
	}

	public Xnode set(int index, Object object) {
		throw new RuntimeException2("[%d] Cannot set object", index);
	}

	public Xmap create(String name) {
		throw new RuntimeException2("[%s] Cannot create object", name);
	}

}
