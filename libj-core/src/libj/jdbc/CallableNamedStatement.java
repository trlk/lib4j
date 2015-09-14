package libj.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CallableNamedStatement extends NamedStatement {

	private CallableStatement statement;

	public CallableNamedStatement(Connection connection, String query)
			throws SQLException {

		super(connection, query);

		statement = connection.prepareCall(this.getParsedQuery());

		this.setStatement(statement);
	}

	public Object get(String name) throws SQLException {

		return getObject(name);
	}

	public Object getObject(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getObject(idx[0]);
	}

	public String getString(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getString(idx[0]);
	}

	public int getInt(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getInt(idx[0]);
	}

	public long getLong(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getLong(idx[0]);
	}

	public float getFloat(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getFloat(idx[0]);
	}

	public double getDouble(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getDouble(idx[0]);
	}

	public Date getDate(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getDate(idx[0]);
	}

	public Timestamp getTimestamp(String name) throws SQLException {

		int[] idx = getIndexes(name);

		return statement.getTimestamp(idx[0]);
	}

	public void registerOutParameter(String name, int sqlType)
			throws SQLException {

		int[] indexes = getIndexes(name);

		for (int i = 0; i < indexes.length; i++) {
			statement.registerOutParameter(indexes[i], sqlType);
		}

	}

}
