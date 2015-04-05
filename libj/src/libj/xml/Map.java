package libj.xml;

import java.util.HashMap;
import java.util.List;

public class Map {

	HashMap<String, Object> map;

	public Map() {

		map = new HashMap<String, Object>();
	}

	public void set(String key, Object value) {

		map.put(key, value);
	}

	public Map get(String key) {

		return (Map) map.get(key);
	}

	@SuppressWarnings("all")
	public List<Map> getList(String key) {

		return (List) map.get(key);
	}

}
