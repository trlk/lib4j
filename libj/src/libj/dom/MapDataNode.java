package libj.dom;

import java.util.LinkedHashMap;

import spring.util.LinkedCaseInsensitiveMap;
import libj.error.RuntimeException2;

public class MapDataNode extends DataNode {

	private MapDataNode() {

		super(new LinkedCaseInsensitiveMap<DataNode>());
	}

	public MapDataNode(String name) {

		this();
		setName(name);
	}

	public MapDataNode(DataNode parent, String name) {

		this(name);
		parent.set(name, this);
	}

	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, DataNode> map() {

		return (LinkedHashMap<String, DataNode>) this.getObject();
	}

	public int size() {
		return map().size();
	}

	public boolean isList() {
		return false;
	}

	public boolean isHave(String name) {
		return map().containsKey(name);
	}

	public String nameOf(int index) {

		if (index >= 0 && index < size()) {

			String[] keyArray = map().keySet().toArray(new String[0]);
			return keyArray[index];
		}

		throw new RuntimeException2("Index out of bound: %s[%s]", this.name, index);
	}

	public int indexOf(String name) {

		int i = 0;
		for (String keyName : map().keySet()) {

			if (keyName.equalsIgnoreCase(name)) {
				return i;
			}

			i++;
		}

		throw new RuntimeException2("Node not found: %s/%s", this.name, name);
	}

	public DataNode get(String name) {

		if (!isHave(name)) {
			throw new RuntimeException2("Node not found: %s/%s", this.name, name);
		}

		return map().get(name);
	}

	public DataNode get(int index) {
		return get(nameOf(index));
	}

	public DataNode set(String name, DataNode dataNode) {

		map().put(name, dataNode);

		return dataNode;
	}

	public DataNode set(String name, Object object) {

		if (object instanceof DataNode) {
			return set(name, (DataNode) object);
		} else {
			return set(name, new LeafDataNode(name, object));
		}
	}

	public DataNode set(int index, Object object) {
		return set(nameOf(index), object);
	}

	public void remove(String name) {

		if (!isHave(name)) {
			throw new RuntimeException2("Node not found: %s/%s", this.name, name);
		}

		map().remove(name);
	}

	public void remove(int index) {
		remove(nameOf(index));
	}

}
