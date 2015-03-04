package libj.db;

/**
 * @author Taras Lyuklyanchuk
 * 
 * Oracle DB Library
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import libj.debug.Log;
import libj.error.Error;
import libj.error.Raise;
import libj.jdbc.NamedStatement;
import libj.utils.Text;

public class Oracle {

	/*
	 * константы
	 */

	// таймаут по-умолчанию
	public static final Integer DEFAULT_TIMEOUT = 60;

	// формат даты/времени по-умолчанию
	public static final String DEFAULT_DATE_FORMAT = "dd.mm.yyyy";
	public static final String DEFAULT_TIME_FORMAT = "hh24:mm:ss";

	// SQL codes
	public static final Integer ORA_SUCCESS = 0;
	public static final Integer ORA_TIMEOUT = 1013;
	public static final Integer ORA_DISCARDED = 4068;
	public static final Integer ORA_USER_ERROR = 20000;
	public static final Integer ORA_USER_TIMEOUT = 20999;

	// SQL states
	public static final String STATE_SUCCESS = "00000";
	public static final String STATE_GENERAL_WARN = "01000";
	public static final String STATE_GENERAL_ERROR = "HY000";
	public static final String STATE_TIMEOUT_EXPIRED = "HYT00";

	// jdbc driver class
	public static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

	// TCP-порт по-умолчанию
	public static final Integer DEFAULT_TCP_PORT = 1521;

	/*
	 * переменные
	 */

	private Integer timeout = DEFAULT_TIMEOUT;
	private String url;
	private String user;
	private String password;
	private Connection conn;

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

	// прочесть таймаут
	public Integer getTimeout() {
		return timeout;
	}

	// установить таймаут
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	// прочесть URL
	public String getURL() {
		return url;
	}

	// получить коннект
	public Connection getConnection() {

		try {

			if (conn == null) {

				Raise.runtimeException("Connection not established yet");

			} else if (conn.isClosed()) {

				reconnect();
			}

		} catch (Exception e) {
			Raise.runtimeException(e);
		}

		return conn;
	}

	// получить коннект
	public Connection conn() {

		return getConnection();
	}

	// подключиться
	public void connect(String url) {

		this.url = url;

		connect();
	}

	// подключиться
	public void connect(String url, String user, String password) {

		this.url = url;
		this.user = user;
		this.password = password;

		connect();
	}

	// подключиться к базе
	public void connect(String host, String sid, String user, String password) {

		String url;

		if (host.contains(":")) {

			url = Text.printf("jdbc:oracle:thin:@%s/%s", host, sid);
		} else {
			url = Text.printf("jdbc:oracle:thin:@%s:%s/%s", host,
					DEFAULT_TCP_PORT, sid);
		}

		connect(url, user, password);
	}

	// подключиться
	public void connect() {

		try {

			if (conn != null) {
				disconnect();
			}

			if (url == null) {

				Raise.runtimeException("Database URL not specified");

			} else if (url.startsWith("jdbc:")) {

				Class.forName(DRIVER_CLASS);

				// получаем коннект
				conn = DriverManager.getConnection(url, user, password);

			} else if (url.startsWith("jdbc/")) {

				String fullURL = Text.sprintf("java:comp/env/%s", url);
				DataSource ds = (DataSource) new InitialContext()
						.lookup(fullURL);

				// получаем коннект
				conn = ds.getConnection();

			} else
				Raise.runtimeException("Invalid database URL: %s", url);

			// установим параметры сессии
			alterSession("NLS_DATE_FORMAT", DEFAULT_DATE_FORMAT);

		} catch (Exception e) {

			disconnect();
			Raise.runtimeException(e);
		}

	}

	// отключиться от базы
	public void disconnect() {

		try {

			if (conn != null && !conn.isClosed()) {

				conn.rollback();
				conn.close();
			}
		} catch (Exception e) {

			disconnect();
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

		try {

			Log.debug("Execute sql: %s\n", sqlStatement);

			Statement q = conn.createStatement();

			q.execute(sqlStatement);

		} catch (SQLException e) {

			raise(e); // re-raise
		}
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
	public void raise(String message, String sqlState, Integer sqlCode)
			throws SQLException {

		throw new SQLException(message, sqlState, sqlCode);
	}

	// raise(SQLException)
	public void raise(SQLException e) throws SQLException {

		// log
		Error.print(e);

		// timeout handle
		if (e.getErrorCode() == ORA_TIMEOUT) {

			e = new SQLException("Request timeout", e.getSQLState(),
					ORA_USER_TIMEOUT);
		} else {
			e = new SQLException(truncMessage(e), e.getSQLState(),
					e.getErrorCode());
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

}
