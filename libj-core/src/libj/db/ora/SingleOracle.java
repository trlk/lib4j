package libj.db.ora;

/**
 * @author Taras Lyuklyanchuk
 * 
 * Oracle DB Library
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import libj.debug.Trace;

public class SingleOracle extends Oracle {

	private Connection connection;

	// конструктор
	public SingleOracle(String url) {

		super(url);
	}

	// конструктор
	public SingleOracle(String url, String user, String password) {

		super(url, user, password);
		connect();
	}

	// конструктор
	public SingleOracle(String host, String sid, String user, String password) {

		super(host, sid, user, password);
		connect();
	}

	// получить коннект
	@Override
	public Connection getConnection() {

		if (connection == null) {
			connect();
		}

		return connection;
	}

	// подключиться
	public synchronized void connect() {

		connection = super.getConnection();
	}

	// отключиться от базы
	public synchronized void disconnect() {

		close(connection);
	}

	// переподключиться
	public synchronized void reconnect() {

		disconnect();
		connect();
	}

	// commit
	public void commit() throws SQLException {

		commit(connection);
	}

	// rollback
	public void rollback() throws SQLException {

		rollback(connection);
	}

	// prepare statement
	public PreparedStatement prepare(String sql, Object... params) throws SQLException {

		return prepare(connection, sql, params);
	}

	// execute statement
	public void execute(String sqlStatement) throws SQLException {

		execute(connection, sqlStatement);
	}

	// alter session
	public void alterSession(String param, String value) throws SQLException {

		alterSession(param, value);
	}

	// удалить все строки таблицы
	public void deleteTable(String table) throws SQLException {

		Trace.point(table);

		execute("delete from " + table);
	}

	// удалить все строки таблицы	
	public void truncateTable(String table) throws SQLException {

		Trace.point(table);

		execute("truncate table " + table);
	}

	// executeQuery
	public ResultSet executeQuery(String sqlStatement) throws SQLException {

		return executeQuery(sqlStatement);
	}

	// instance finalizer
	protected void finalize() {

		if (connection != null) {
			close(connection);
		}
	}

}
