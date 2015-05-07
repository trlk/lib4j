package libj.dom;

import java.util.HashMap;

import libj.error.RuntimeException2;
import libj.error.Throw;

public class MapDataNode extends DataNode {

	private MapDataNode() {

		super(new HashMap<String, DataNode>());
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
	public HashMap<String, DataNode> map() {

		return (HashMap<String, DataNode>) this.getObject();
	}

	@Override
	public DataNode get(String name) {

		if (!map().containsKey(name)) {
			Throw.runtimeException("[%s/%s] Node not found", this.name, name);
		}

		return map().get(name);
	}

	public DataNode set(String name, DataNode xnode) {

		map().put(name, xnode);

		return xnode;
	}

	public DataNode set(String name, Object object) {

		if (object instanceof DataNode) {
			return set(name, (DataNode) object);
		} else {
			return set(name, new LeafDataNode(name, object));
		}
	}

	public DataNode get(int index) {
		throw new RuntimeException2("Cannot get by index from map: %s[%d]", name, index);
	}

	public DataNode set(int index, Object object) {
		throw new RuntimeException2("Cannot set by index within map: %s[%d]", name, index);
	}

}
