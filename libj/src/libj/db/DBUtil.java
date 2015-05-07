package libj.db;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import libj.dom.DataNode;
import libj.dom.ListDataNode;
import libj.dom.MapDataNode;

public class DBUtil {

	private static Map<String, Integer> getResultSetMap(ResultSet rs) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();

		// rs map
		Map<String, Integer> rsMap = new HashMap<String, Integer>();

		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			rsMap.put(rsmd.getColumnName(i).toUpperCase(), rsmd.getColumnType(i));
		}

		return rsMap;
	}

	private static Map<String, String> getDataObjectMap(MapDataNode bo) {

		// bo map
		Map<String, String> boMap = new HashMap<String, String>();

		for (String name : bo.map().keySet()) {
			boMap.put(name, bo.get(name).getType());
		}

		return boMap;
	}

	public static void getResultSetRow(ResultSet rs, MapDataNode bo) throws SQLException {

		// bo map
		Map<String, String> boMap = getDataObjectMap(bo);

		// rs map
		Map<String, Integer> rsMap = getResultSetMap(rs);

		// join maps
		for (String prop : boMap.keySet()) {

			if (rsMap.containsKey(prop.toUpperCase())) {

				String boType = boMap.get(prop);
				int sqlType = rsMap.get(prop.toUpperCase());

				Object value = rs.getObject(prop);

				if (value != null) {

					if (sqlType == Types.NUMERIC) {

						if (boType == "Float") {
							value = libj.utils.Math.toFloat(rs.getBigDecimal(prop));
						} else if (boType == "Integer") {
							value = rs.getInt(prop);
						} else if (boType == "Boolean") {
							value = rs.getBoolean(prop);
						}
					}
				}

				bo.set(prop, value);
			}
		}
	}


	public static DataNode getResultSetRow(ResultSet rs, String nodeName) throws SQLException {

		MapDataNode bo = new MapDataNode(nodeName);

		getResultSetRow(rs, bo);

		return bo;
	}

	public static void fetchResultSetRow(ResultSet rs, MapDataNode bo) throws SQLException {

		if (rs.next()) {

			getResultSetRow(rs, bo);

		} else {
			throw new SQLException("Fetch out of sequence");
		}
	}


	public static DataNode fetchResultSetRow(ResultSet rs, String nodeName) throws SQLException {

		if (rs.next()) {

			return getResultSetRow(rs, nodeName);

		} else {
			throw new SQLException("No data found");
		}
	}

	public static ListDataNode fetchResultSetList(ResultSet rs, String listName, String nodeName) throws SQLException {

		ListDataNode boList = new ListDataNode(listName);

		while (rs.next()) {

			boList.add(getResultSetRow(rs, nodeName));
		}

		return boList;
	}

	public static void setResultSetRow(ResultSet rs, MapDataNode bo) throws SQLException {

		// bo map
		Map<String, String> boMap = getDataObjectMap(bo);

		// rs map
		Map<String, Integer> rsMap = getResultSetMap(rs);

		// join maps
		for (String prop : boMap.keySet()) {

			if (rsMap.containsKey(prop.toUpperCase())) {

				String boType = boMap.get(prop);
				int sqlType = rsMap.get(prop.toUpperCase());

				Object value = bo.get(prop);

				if (value != null && sqlType == Types.TIMESTAMP && boType == "Date") {

					value = libj.utils.Cal.getSqlTimestamp((Date) value);
				}

				rs.updateObject(prop, value);
			}
		}
	}

	public static void updateResultSetRow(ResultSet rs, MapDataNode bo) throws SQLException {

		setResultSetRow(rs, bo);
		rs.updateRow();
	}

	public static void insertResultSetRow(ResultSet rs, MapDataNode bo) throws SQLException {

		rs.moveToInsertRow();
		setResultSetRow(rs, bo);
		rs.insertRow();
	}

	public static void upsertResultSetRow(ResultSet rs, MapDataNode bo) throws SQLException {

		if (rs.next()) {

			updateResultSetRow(rs, bo);

		} else {

			insertResultSetRow(rs, bo);
		}
	}

	public static void insertResultSetList(ResultSet rs, ListDataNode boList) throws SQLException {

		for (DataNode row : boList.list()) {

			insertResultSetRow(rs, (MapDataNode) row);
		}
	}

}
