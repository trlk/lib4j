package libj.dom;

import libj.debug.Stack;
import libj.error.RuntimeError;
import libj.xml.XMLSchema;

public class XLeafNode extends XDataNode {

	public XLeafNode(XNode parent, String name, Object object) {

		super(parent, name);

		if (object instanceof XDataNode) {
			throw new RuntimeError("Incompatible object class: %s", object.getClass().getName());
		}

		setObject(object);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public XLeafNode(XNode parent, String name, Class clazz, String value) throws Exception {

		this(parent, name, clazz.getConstructor(String.class).newInstance(value));
	}

	public XLeafNode(XNode parent, String name, String xsType, String value) throws Exception {

		this(parent, name, XMLSchema.createObject(xsType, value));
	}

	public boolean isHas(String name) {

		return false;
	}

	public boolean isSet() {

		return (object != null);
	}

	public boolean isList() {

		return false;
	}

	public int size() {

		throwNotApplicable(Stack.thisMethodName());
		return 0;
	}

	public XDataNode get(String name) {

		return throwNotApplicable(Stack.thisMethodName());
	}

	public XDataNode get(int index) {

		return throwNotApplicable(Stack.thisMethodName());
	}

	public XDataNode set(String name, Object object) {

		return throwNotApplicable(Stack.thisMethodName());
	}

	public XDataNode set(int index, Object object) {

		return throwNotApplicable(Stack.thisMethodName());
	}

	public void remove(String name) {

		throwNotApplicable(Stack.thisMethodName());
	}

	public void remove(int index) {

		throwNotApplicable(Stack.thisMethodName());
	}

	public void clear() {

		setObject(null);
	}

	public void merge(XDataNode node) {

		if (node instanceof XLeafNode) {
			setObject(node.getObject());
		}
	}

}
