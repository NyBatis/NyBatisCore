package org.nybatis.core.db.session.handler;

import org.nybatis.core.db.session.executor.SqlBean;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface CallableHandler {

	void run( SqlBean sqlBean, CallableStatement statement, boolean hasResultSet ) throws SQLException;

}
