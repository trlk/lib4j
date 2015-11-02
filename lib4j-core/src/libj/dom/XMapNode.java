package libj.dom;

import java.util.HashMap;
import java.util.LinkedHashMap;

import commonj.sdo.DataObject;
import libj.error.RuntimeError;
import libj.sdo.SDOUtils;
import spring.util.LinkedCaseInsensitiveMap;

public class XMapNode extends XDataNode {

	public XMapNode(XNode parent, String name) {

		super(parent, name, new LinkedCaseInsensitiveMap<XDataNode>());
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public XMapNode clone() {

		XMapNode clone = (XMapNode) super.clone();

		clone.object = ((HashMap) object).clone();

		for (String key : clone.map().keySet()) {
			clone.set(key, this.get(key).clone());
		}

		return clone;
	}

	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, XDataNode> map() {

		return (LinkedHashMap<String, XDataNode>) this.getObject();
	}

	public boolean isHas(String name) {

		return map().containsKey(name);
	}

	public boolean isSet() {

		return map().size() != 0;
	}

	public boolean isList() {

		return false;
	}

	public int size() {

		return map().size();
	}

	public String nameOf(int index) {

		if (0 <= index && index < size()) {

			String[] keyArray = map().keySet().toArray(new String[0]);
			return keyArray[index];
		}

		throw new RuntimeError("Index out of bound: %s[%s]", this.name, index);
	}

	public int indexOf(String name) {

		int i = 0;
		for (String keyName : map().keySet()) {

			if (keyName.equalsIgnoreCase(name)) {
				return i;
			}

			i++;
		}

		throw new RuntimeError("Node not found: %s/%s", this.name, name);
	}

	public XDataNode get(String name) {

		if (!isHas(name)) {
			throw new RuntimeError("Node not found: %s/%s", this.name, name);
		}

		return map().get(name);
	}

	public XDataNode get(int index) {
		return get(nameOf(index));
	}

	public XDataNode set(String name, XDataNode dataNode) {

		map().put(name, dataNode);

		dataNode.setParent(this);

		return dataNode;
	}

	public XDataNode set(String name, Object object) {

		if (object instanceof XDataNode) {
			return set(name, (XDataNode) object);
		} else if (object instanceof XDocument) {
			return set(name, ((XDocument) object).getRoot());
		} else if (SDOUtils.isAvailable() && object instanceof DataObject) {
			return set(name, SDOUtils.convertToDocument((DataObject) object).getRoot());
		} else {
			return set(name, new XLeafNode(this, name, object));
		}
	}

	public XDataNode set(int index, Object object) {

		return set(nameOf(index), object);
	}

	public void remove(String name) {

		if (!isHas(name)) {
			throw new RuntimeError("Node not found: %s/%s", this.name, name);
		}

		map().remove(name);
	}

	public void remove(int index) {

		remove(nameOf(index));
	}

	public void clear() {

		map().clear();
	}

	public void merge(XDataNode node) {

		if (node instanceof XMapNode) {

			for (int i = 0; i < node.size(); i++) {

				this.set(((XMapNode) node).nameOf(i), node.get(i).clone());
			}
		}
	}

}
