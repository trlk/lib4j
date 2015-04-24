package libj.db.ora;

/**
 * @author Taras Lyuklyanchuk
 * 
 * Oracle DB Library
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import libj.db.Database;
import libj.debug.Error;
import libj.debug.Log;
import libj.error.Raise;
import libj.jdbc.NamedStatement;
import libj.utils.Text;

public class Oracle extends Database {

	// SQL codes
	public static final Integer ORA_SUCCESS = 0;
	public static final Integer ORA_TIMEOUT = 1013;
	public static final Integer ORA_DISCARDED = 4068;
	public static final Integer ORA_USER_ERROR = 20000;
	public static final Integer ORA_USER_TIMEOUT = 20999;

	// jdbc driver class
	public static String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

	// формат даты/времени по-умолчанию
	public static String DATE_FORMAT = "dd.mm.yyyy";
	public static String TIME_FORMAT = "hh24:mm:ss";

	// переменные
	private String user;
	private String password;
	private Connection conn;
	private Boolean isAutoCloseable = true;
	private Boolean isMultiConnection = null;

	// конструктор
	public Oracle() {
		setTimeout(DEFAULT_TIMEOUT);
	}

	// конструктор
	public Oracle(String url) {

		this();
		connect(url);
	}

	// конструктор
	public Oracle(String url, String user, String password) {

		this();
		connect(url, user, password);
	}

	// флаг автозакрытия соединения
	public boolean isAutoCloseable() {
		return isAutoCloseable;
	}

	// установить флаг автозакрытия соединения
	public void setAutoCloseable(boolean isAutoCloseable) {
		this.isAutoCloseable = isAutoCloseable;
	}

	// флаг мультиконнекта
	public boolean isMultiConnection() {
		return isMultiConnection;
	}

	// установить флаг мультиконнекта
	public void setMultiConnection(boolean isMultiConnection) {
		this.isMultiConnection = isMultiConnection;
	}

	// получить коннект
	public synchronized Connection getConnection() {

		try {

			if (isMultiConnection) {

				connect();

			} else {

				if (conn == null) {

					Raise.runtimeException("Connection not established yet");

				} else if (conn.isClosed()) {

					reconnect();
				}
			}

		} catch (Exception e) {
			Raise.runtimeException(e);
		}

		return conn;
	}

	// получить коннект
	public synchronized Connection conn() {

		return getConnection();
	}

	// подключиться
	public synchronized void connect(String url) {

		this.url = url;

		connect();
	}

	// подключиться
	public synchronized void connect(String url, String user, String password) {

		this.url = url;
		this.user = user;
		this.password = password;

		connect();
	}

	// подключиться к базе
	public synchronized void connect(String host, String sid, String user, String password) {

		String url;

		if (host.contains(":")) {
			url = Text.printf("jdbc:oracle:thin:@%s/%s", host, sid);
		} else {
			url = Text.printf("jdbc:oracle:thin:@%s:%s/%s", host, 1521, sid);
		}

		connect(url, user, password);
	}

	// подключиться
	public synchronized void connect() {

		try {

			if (conn != null && !isMultiConnection) {
				disconnect();
			}

			if (url == null) {

				Raise.runtimeException("Database URL not specified");

			} else if (url.startsWith("jdbc:")) {

				Class.forName(DRIVER_CLASS);

				// получаем коннект
				conn = DriverManager.getConnection(url, user, password);

				// флаг мультиконнекта				
				if (isMultiConnection == null) {
					setMultiConnection(false);
				}

			} else if (url.startsWith("jdbc/")) {

				String jndiURL = Text.sprintf("java:comp/env/%s", url);
				DataSource ds = (DataSource) new InitialContext().lookup(jndiURL);

				// получаем коннект
				conn = ds.getConnection();

				// флаг мультиконнекта
				if (isMultiConnection == null) {
					setMultiConnection(true);
				}

			} else {
				Raise.runtimeException("Invalid database URL: %s", url);
			}

			// установим параметры сессии
			alterSession("NLS_DATE_FORMAT", DATE_FORMAT);

		} catch (Exception e) {

			disconnect();
			Raise.runtimeException(e);
		}
	}

	// отключиться от базы
	public synchronized void disconnect() {

		try {

			if (conn != null && !conn.isClosed()) {

				conn.rollback();
				conn.close();
			}

		} catch (Exception e) {

			Raise.runtimeException(e);
		}
	}

	// переподключиться
	public void reconnect() {

		disconnect();
		connect();
	}

	// commit
	public void commit() throws SQLException {

		if (conn != null && !conn.isClosed()) {

			conn.commit();
		}
	}

	// rollback
	public void rollback() throws SQLException {

		if (conn != null && !conn.isClosed()) {

			conn.rollback();
		}
	}

	// execute
	public void execute(PreparedStatement q) throws SQLException {

		try {

			q.setQueryTimeout(this.timeout);
			q.execute();

		} catch (SQLException e) {

			raise(e); // re-raise
		}
	}

	// execute
	public void execute(NamedStatement q) throws SQLException {

		try {

			q.setQueryTimeout(this.timeout);
			q.execute();

		} catch (SQLException e) {

			raise(e); // re-raise
		}
	}

	// execute
	public void execute(String sqlStatement) throws SQLException {

		PreparedStatement q = conn.prepareStatement(sqlStatement);

		execute(q);
	}

	// forced execute
	public void enforce(PreparedStatement q) throws SQLException {

		try {

			q.setQueryTimeout(this.timeout);
			q.execute();

		} catch (SQLException e) {

			if (e.getErrorCode() == ORA_DISCARDED) {

				Log.error(e.getMessage());
				Log.error("Last query will be retried...");

				execute(q); // retry

			} else {

				raise(e);
			}
		}
	}

	// forced execute
	public void enforce(NamedStatement q) throws SQLException {

		try {

			q.setQueryTimeout(this.timeout);
			q.execute();

		} catch (SQLException e) {

			if (e.getErrorCode() == ORA_DISCARDED) {

				Log.error(e.getMessage());
				Log.error("Last query will be retried...");

				execute(q); // retry

			} else {

				raise(e);
			}
		}
	}

	// alter session
	public void alterSession(String param, String value) throws SQLException {

		Statement sql = conn.createStatement();
		sql.execute(Text.printf("alter session set %s='%s'", param, value));
	}

	// удалить все строки таблицы
	public void deleteTable(String table) throws SQLException {

		execute("delete from " + table);
	}

	// удалить все строки таблицы	
	public void truncateTable(String table) throws SQLException {

		execute("truncate table " + table);
	}

	// текст ошибки Oracle без стека
	public String truncMessage(String message) {

		if (message == null) {
			return null;
		}

		String result;
		int i = message.indexOf("\n");

		if (i > 0) {
			result = message.substring(0, i);
		} else {
			result = message;
		}

		if (result.startsWith("ORA")) {

			i = result.indexOf(":");

			if (i > 0) {
				result = result.substring(i + 1).trim();
			}
		}

		return result;
	}

	// текст ошибки Oracle без стека
	public String truncMessage(Exception e) {

		return truncMessage(e.getMessage());
	}

	// raise(code,message)
	public void raise(Integer sqlCode, String message) throws SQLException {

		throw new SQLException(message, STATE_GENERAL_ERROR, sqlCode);
	}

	// raise(message)
	public void raise(String message) throws SQLException {

		raise(ORA_USER_ERROR, message);
	}

	// raise(message,state,code)
	public void raise(String message, String sqlState, Integer sqlCode) throws SQLException {

		throw new SQLException(message, sqlState, sqlCode);
	}

	// raise(SQLException)
	public void raise(SQLException e) throws SQLException {

		// log
		Error.print(e);

		// timeout handle
		if (e.getErrorCode() == ORA_TIMEOUT) {

			e = new SQLException("Request timeout", e.getSQLState(), ORA_USER_TIMEOUT);
		} else {
			e = new SQLException(truncMessage(e), e.getSQLState(), e.getErrorCode());
		}

		throw e;
	}

	// raise(Exception)
	public void raise(Exception e) throws Exception {

		// log
		Error.print(e);

		if (e instanceof SQLException) {
			raise((SQLException) e);
		} else {
			raise(truncMessage(e));
		}
	}

	// executeQuery
	public ResultSet executeQuery(String sqlStatement) throws SQLException {

		PreparedStatement q = conn.prepareStatement(sqlStatement);

		return executeQuery(q);
	}

	// executeQuery
	public ResultSet executeQuery(PreparedStatement q) throws SQLException {

		try {

			q.setQueryTimeout(this.timeout);

			return q.executeQuery();

		} catch (SQLException e) {

			raise(e);
		}

		return null;
	}

	// close ResultSet
	public void close(ResultSet q) {

		try {

			if (!isAutoCloseable) {

				q.close();

			} else {

				Statement stmt = q.getStatement();

				q.close();
				close(stmt);
			}

		} catch (SQLException e) {
			Log.error(e);
		}
	}

	// close Statement
	public void close(Statement q) {

		try {

			if (q != null && !q.isClosed()) {

				if (!isAutoCloseable) {

					q.close();

				} else {

					Connection conn = q.getConnection();

					q.close();
					close(conn);
				}
			}

		} catch (SQLException e) {
			Log.error(e);
		}
	}

	// close NamedStatement
	public void close(NamedStatement q) {

		if (q != null) {
			close(q.getStatement());
		}
	}

	// close Connection
	public void close(Connection q) {

		try {

			if (q != null && !q.isClosed()) {
				q.close();
			}

		} catch (SQLException e) {
			Log.error(e);
		}
	}

	// instance finalizer
	protected void finalize() {

		if (conn != null && isAutoCloseable) {
			close(conn);
		}
	}

}
