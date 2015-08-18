package libj.db.ora;

/**
 * @author Taras Lyuklyanchuk
 * 
 * Oracle DB Library
 */

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import libj.db.Database;
import libj.debug.Log;
import libj.debug.Trace;
import libj.error.RuntimeError;
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
	public static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

	enum Mode {
		DIRECT, POOL
	};

	// переменные
	private Mode mode;
	private Boolean isAutoCommit = null; // driver defaults
	private Boolean isAutoClose = false; // disabled by default

	// формат даты/времени по-умолчанию
	private String dateFormat = "dd.mm.yyyy";
	@SuppressWarnings("unused")
	private String timeFormat = "hh24:mm:ss";

	private String user;
	private String password;

	// конструктор
	public Oracle(String url) {

		setURL(url);

		if (url.startsWith("jdbc:")) {

			mode = Mode.DIRECT;

		} else if (url.startsWith("jdbc/")) {

			mode = Mode.POOL;

		} else {
			throw new RuntimeError("Invalid database URL: %s", url);
		}
	}

	// конструктор
	public Oracle(String url, String user, String password) {

		this(url);

		this.user = user;
		this.password = password;
	}

	// конструктор
	public Oracle(String host, String sid, String user, String password) {

		if (host.contains(":")) {
			setURL(Text.sprintf("jdbc:oracle:thin:@%s/%s", host, sid));
		} else {
			setURL(Text.sprintf("jdbc:oracle:thin:@%s:%s/%s", host, 1521, sid));
		}

		this.user = user;
		this.password = password;

		mode = Mode.DIRECT;
	}

	// флаг автокоммитта
	public Boolean isAutoCommit() {

		return isAutoCommit;
	}

	// установить флаг автокоммитта
	public void setAutoCommit(Boolean isAutoCommit) {

		this.isAutoCommit = isAutoCommit;
	}

	// флаг рекурсивного закрытия
	public Boolean isAutoClose() {

		return isAutoClose;
	}

	// установить флаг рекурсивного закрытия
	public void setAutoClose(Boolean isAutoClose) {

		this.isAutoClose = isAutoClose;
	}

	// получить коннект
	public Connection getConnection() {

		Connection connection = null;

		try {

			if (url == null) {

				throw new RuntimeError("Database URL is not specified");

			} else if (mode == Mode.DIRECT) {

				Class.forName(DRIVER_CLASS);

				// получаем коннект
				connection = DriverManager.getConnection(url, user, password);

			} else if (mode == Mode.POOL) {

				String jndiURL = Text.sprintf("java:comp/env/%s", url);
				DataSource ds = (DataSource) new InitialContext().lookup(jndiURL);

				// получаем коннект
				connection = ds.getConnection();

			} else {
				throw new RuntimeError("Invalid connection mode: %s", mode.toString());
			}

			int sid = getSID(connection);

			Log.trace("(%s) Connected to %s", sid, getBanner(connection));

			// режим автокоммита
			if (isAutoCommit != null) {

				connection.setAutoCommit(isAutoCommit);
				Log.trace("(%s) AutoCommit: %b", sid, isAutoCommit);

			} else {
				Log.trace("(%s) AutoCommit: default", sid);
			}

			// установим параметры сессии
			alterSession(connection, "NLS_DATE_FORMAT", dateFormat);

		} catch (Exception e) {

			close(connection);
			throw new RuntimeError(e);
		}

		return connection;
	}

	// получить коннект
	public Connection conn() {

		return getConnection();
	}

	public int getSID(Connection connection) {

		CallableStatement q = null;

		try {

			q = connection.prepareCall("begin select sys_context('userenv','sid') into :1 from dual; end;");

			q.registerOutParameter(1, Types.ORA_INTEGER);
			q.execute();

			return q.getInt(1);

		} catch (SQLException e) {

			Log.warn(e);
			return 0;

		} finally {
			close(q, false);
		}
	}

	public String getBanner(Connection connection) {

		String banner = null;
		PreparedStatement q = null;

		try {

			q = connection.prepareStatement("select banner from sys.v_$version");

			ResultSet rs = q.executeQuery();

			if (rs.next()) {
				banner = rs.getString(1);
			}

			close(rs, false);

		} catch (SQLException e) {

			Log.warn(e);

		} finally {
			close(q, false);
		}

		return banner;
	}

	public int getSID(Statement statement) throws SQLException {

		return getSID(statement.getConnection());
	}

	// commit
	public void commit(Connection connection) throws SQLException {

		Trace.point();

		if (connection != null && !connection.isClosed()) {

			connection.commit();
			Log.trace("(%s) Commit competed", getSID(connection));
		}
	}

	// rollback
	public void rollback(Connection connection) throws SQLException {

		Trace.point();

		if (connection != null && !connection.isClosed()) {

			connection.rollback();
			Log.trace("(%s) Rollback competed", getSID(connection));
		}
	}

	// prepare statement
	public PreparedStatement prepare(Connection connection, String sql, Object... params) throws SQLException {

		Trace.point(sql, params);

		PreparedStatement q = connection.prepareStatement(sql);

		for (int i = 0; i < params.length; i++) {

			q.setObject(i + 1, params[i]);
		}

		return q;
	}

	// execute statement
	public void execute(PreparedStatement q) throws SQLException {

		Trace.point(q.toString());

		try {

			q.setQueryTimeout(this.timeout);
			q.execute();

		} catch (SQLException e) {

			raise(e); // re-raise
		}
	}

	// execute statement
	public void execute(NamedStatement q) throws SQLException {

		Trace.point(q.getQuery().toString());

		try {

			q.setQueryTimeout(this.timeout);
			q.execute();

		} catch (SQLException e) {

			raise(e); // re-raise
		}
	}

	// execute statement
	public void execute(Connection connection, String sqlStatement) throws SQLException {

		Trace.point(sqlStatement);

		PreparedStatement q = connection.prepareStatement(sqlStatement);

		execute(q);
	}

	// forced execute
	public void enforce(PreparedStatement q) throws SQLException {

		Trace.point(q.toString());

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

		Trace.point(q.getQuery());

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
	public void alterSession(Connection connection, String param, String value) throws SQLException {

		Trace.point(param, value);

		Statement sql = connection.createStatement();
		sql.execute(Text.sprintf("alter session set %s='%s'", param, value));
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
		Log.error(e);

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
		Log.error(e);

		if (e instanceof SQLException) {
			raise((SQLException) e);
		} else {
			raise(truncMessage(e));
		}
	}

	// executeQuery
	public ResultSet executeQuery(Connection connection, String sqlStatement) throws SQLException {

		Trace.point(sqlStatement);

		PreparedStatement q = connection.prepareStatement(sqlStatement);

		return executeQuery(q);
	}

	// executeQuery
	public ResultSet executeQuery(PreparedStatement q) throws SQLException {

		Trace.point(q);

		try {

			q.setQueryTimeout(this.timeout);

			return q.executeQuery();

		} catch (SQLException e) {

			raise(e);
		}

		return null;
	}

	// close ResultSet
	public void close(ResultSet q, boolean closeParent) {

		try {

			Statement stmt = q.getStatement();

			q.close();

			if (closeParent) {
				close(stmt, closeParent);
			}

		} catch (SQLException e) {
			Log.error(e);
		}
	}

	// close ResultSet
	public void close(ResultSet q) {
		close(q, isAutoClose);
	}

	// close Statement
	public void close(Statement q, boolean closeParent) {

		try {

			if (q != null && !q.isClosed()) {

				Connection conn = q.getConnection();

				q.close();

				if (closeParent) {
					close(conn);
				}
			}

		} catch (SQLException e) {
			Log.error(e);
		}
	}

	// close Statement
	public void close(Statement q) {
		close(q, isAutoClose);
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
				// q.rollback();
				q.close();
			}

		} catch (SQLException e) {
			Log.error(e);
		}
	}

	// close jdbc objects
	public void close(Object... args) {

		for (Object object : args) {

			if (object != null) {

				if (object instanceof ResultSet) {
					close((ResultSet) object);
				} else if (object instanceof Statement) {
					close((Statement) object);
				} else if (object instanceof Connection) {
					close((Connection) object);
				} else {
					throw new RuntimeError("Cannot close %s object", object.getClass().getName());
				}
			}
		}
	}

}
