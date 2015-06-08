package libj.dom;

import libj.debug.Stack;
import libj.error.RuntimeException2;

public class LeafDataNode extends DataNode {

	public LeafDataNode(String name, Object object) {

		super();

		if (object instanceof DataNode) {
			throw new RuntimeException2("Incompatible object class: %s", object.getClass().getName());
		}

		setName(name);
		setObject(object);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LeafDataNode(String name, Class theClass, String value) throws Exception {

		this(name, theClass.getConstructor(String.class).newInstance(value));
	}

	public int size() {
		throwNotApplicable(Stack.thisMethodName());
		return 0;
	}

	public boolean isList() {
		return false;
	}

	public boolean isHave(String name) {
		throwNotApplicable(Stack.thisMethodName());
		return false;
	}

	public DataNode get(String name) {
		return throwNotApplicable(Stack.thisMethodName());
	}

	public DataNode get(int index) {
		return throwNotApplicable(Stack.thisMethodName());
	}

	public DataNode set(String name, Object object) {
		return throwNotApplicable(Stack.thisMethodName());
	}

	public DataNode set(int index, Object object) {
		return throwNotApplicable(Stack.thisMethodName());
	}

	public void remove(String name) {
		throwNotApplicable(Stack.thisMethodName());
	}

	public void remove(int index) {
		throwNotApplicable(Stack.thisMethodName());
	}

}
