package org.nybatis.core.db.session.executor.util;

import oracle.jdbc.OracleStatement;
import org.nybatis.core.reflection.Reflector;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Connection Controller
 *
 * @author nayasis@gmail.com
 * @since 2015-11-05
 */
public class OracleStatementController {

    public void setLobPrefetchCount( Statement statement, Integer size ) throws SQLException {
        ( (OracleStatement) unwrapProxy(statement) ).setLobPrefetchSize( size );
    }

    private <T> T unwrapProxy( T instance ) {
        return new Reflector().unwrapProxy( instance );
    }

}
