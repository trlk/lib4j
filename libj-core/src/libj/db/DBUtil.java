package libj.db;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import libj.dom.XDataNode;
import libj.dom.XListNode;
import libj.dom.XMapNode;
import libj.utils.Cal;

public class DBUtil {

	private static Map<String, Integer> getMetadata(ResultSet rs) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();

		// rs map
		Map<String, Integer> rsMap = new HashMap<String, Integer>();

		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			rsMap.put(rsmd.getColumnName(i).toUpperCase(), rsmd.getColumnType(i));
		}

		return rsMap;
	}

	private static Map<String, String> getMetadata(XMapNode bo) {

		// bo map
		Map<String, String> boMap = new HashMap<String, String>();

		for (String name : bo.map().keySet()) {
			boMap.put(name, bo.get(name).getType());
		}

		return boMap;
	}

	public static void getRow(ResultSet rs, XMapNode bo) throws SQLException {

		// bo map
		Map<String, String> boMap = getMetadata(bo);

		// rs map
		Map<String, Integer> rsMap = getMetadata(rs);

		// join maps
		for (String prop : boMap.keySet()) {

			if (rsMap.containsKey(prop.toUpperCase())) {

				String boType = boMap.get(prop);
				int sqlType = rsMap.get(prop.toUpperCase());

				Object value = rs.getObject(prop);

				if (value != null) {

					if (sqlType == Types.NUMERIC) {

						if (boType == "Float") {
							value = libj.utils.Num.toFloat(rs.getBigDecimal(prop));
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


	public static XDataNode getRow(ResultSet rs, String nodeName) throws SQLException {

		XMapNode bo = new XMapNode(null, nodeName);

		getRow(rs, bo);

		return bo;
	}

	public static void fetchRow(ResultSet rs, XMapNode bo) throws SQLException {

		if (rs.next()) {

			getRow(rs, bo);

		} else {
			throw new SQLException("Fetch out of sequence");
		}
	}


	public static XDataNode fetchRow(ResultSet rs, String nodeName) throws SQLException {

		if (rs.next()) {

			return getRow(rs, nodeName);

		} else {
			throw new SQLException("No data found");
		}
	}

	public static XListNode fetchList(ResultSet rs, String listName, String nodeName) throws SQLException {

		XListNode boList = new XListNode(null, listName);

		while (rs.next()) {

			boList.add(getRow(rs, nodeName));
		}

		return boList;
	}

	public static void setRow(ResultSet rs, XMapNode bo) throws SQLException {

		// bo map
		Map<String, String> boMap = getMetadata(bo);

		// rs map
		Map<String, Integer> rsMap = getMetadata(rs);

		// join maps
		for (String prop : boMap.keySet()) {

			if (rsMap.containsKey(prop.toUpperCase())) {

				String boType = boMap.get(prop);
				int sqlType = rsMap.get(prop.toUpperCase());

				Object value = bo.get(prop);

				if (value != null && sqlType == Types.TIMESTAMP && boType == "Date") {

					value = Cal.toSQLTimestamp((Date) value);
				}

				rs.updateObject(prop, value);
			}
		}
	}

	public static void updateRow(ResultSet rs, XMapNode bo) throws SQLException {

		setRow(rs, bo);
		rs.updateRow();
	}

	public static void insertRow(ResultSet rs, XMapNode bo) throws SQLException {

		rs.moveToInsertRow();
		setRow(rs, bo);
		rs.insertRow();
	}

	public static void upsertRow(ResultSet rs, XMapNode bo) throws SQLException {

		if (rs.next()) {

			updateRow(rs, bo);

		} else {

			insertRow(rs, bo);
		}
	}

	public static void insertList(ResultSet rs, XListNode boList) throws SQLException {

		for (XDataNode row : boList.list()) {

			insertRow(rs, (XMapNode) row);
		}
	}

}
