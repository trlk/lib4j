package libj.test;

import java.sql.SQLException;

import libj.utils.App;

public class Main {

	public static void main(String[] args) throws SQLException {

		App.printHello();

		/*
		Oracle ora = new Oracle("jdbc:oracle:thin:@192.168.77.68:1521:ora11",
				"IBPE", "IBPE");

		CallableNamedStatement q = new CallableNamedStatement(ora.conn(), "begin :result := ib_conf.get_value(p_key => :key); end;");

		q.registerOutParameter("result", java.sql.Types.VARCHAR);
		q.setString("key", "engine.xmlns");
		
		ora.enforce(q);

		Log.info(q.getString("result"));
		*/
	}

}
