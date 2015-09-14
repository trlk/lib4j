package libj.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class PreparedNamedStatement extends NamedStatement {

	public PreparedNamedStatement(Connection connection, String query)
			throws SQLException {

		super(connection, query);

		this.setStatement(connection.prepareStatement(this.getParsedQuery()));
	}
}
