package org.nybatis.core.db.session.handler;

import org.nybatis.core.db.session.executor.SqlBean;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlHandler {

	void run( SqlBean sqlBean, Connection connection ) throws SQLException;

}
