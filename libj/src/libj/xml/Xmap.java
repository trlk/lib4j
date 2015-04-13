package libj.xml;

import java.util.HashMap;
import java.util.List;

public class Xmap {

	HashMap<String, Object> map;

	public Xmap() {

		map = new HashMap<String, Object>();
	}

	public void set(String key, Object value) {

		map.put(key, value);
	}

	public Xmap get(String key) {

		return (Xmap) map.get(key);
	}

	@SuppressWarnings("all")
	public List<Xmap> getList(String key) {

		return (List) map.get(key);
	}

}
