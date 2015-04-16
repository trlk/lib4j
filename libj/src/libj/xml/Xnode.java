package libj.xml;

import java.util.HashMap;

public class Xnode {

	private Object object;
	private HashMap<String, String> attr = new HashMap<String, String>();

	public Xnode() {
		setObject(new Object());
	}

	public Xnode(Object object) {

		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getAttr(String key) {
		return attr.get(key);
	}

	public void setAttr(String key, String value) {
		attr.put(key, value);
	}

}
