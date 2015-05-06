package libj.xml;

import java.util.HashMap;

import libj.error.RuntimeException2;
import libj.error.Throw;

public class Xmap extends Xnode {

	private Xmap() {

		super(new HashMap<String, Xnode>());
	}

	public Xmap(String name) {

		this();
		setName(name);
	}

	public Xmap(Xnode parent, String name) {

		this(name);
		parent.set(name, this);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Xnode> map() {

		return (HashMap<String, Xnode>) this.getObject();
	}

	@Override
	public Xnode get(String name) {

		if (!map().containsKey(name)) {
			Throw.runtimeException("[%s/%s] Node not found", this.name, name);
		}

		return map().get(name);
	}

	public Xnode set(String name, Xnode xnode) {

		map().put(name, xnode);

		return xnode;
	}

	public Xnode set(String name, Object object) {

		if (object instanceof Xnode) {
			return set(name, (Xnode) object);
		} else {
			return set(name, new Xleaf(name, object));
		}
	}

	public Xnode get(int index) {
		throw new RuntimeException2("Cannot get by index from map: %s[%d]", name, index);
	}

	public Xnode set(int index, Object object) {
		throw new RuntimeException2("Cannot set by index into map: %s[%d]", name, index);
	}

}
