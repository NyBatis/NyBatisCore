package org.nybatis.core.db.session.executor.batch.module;

import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.log.NLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-08
 */
public abstract class Statements {

    private Map<Object, String>    keyInfos    = new HashMap<>();
    private Map<Object, Statement> statements  = new HashMap<>();

    private String     token;
    private String     environmentId;
    private Connection connection;

    public Statements init( String token, String environmentId ) {
        this.token         = token;
        this.environmentId = environmentId;
        return this;
    }

    public String getKeyInfo( Object key ) {
        return keyInfos.get( key );
    }

    public Map<Object, Statement> getStatements() {
        return statements;
    }

    public Set<Object> keySet() {
        return statements.keySet();
    }

    public Statement get( Object key ) throws SQLException {
        return statements.get( key );
    }

    private boolean exist( Object key ) {
        return statements.containsKey( key );
    }

    public Object getKey( SqlBean sqlBean ) {
        Object key = generateKey( sqlBean );
        keyInfos.put( key, sqlBean.toString() );
        return key;
    }

    public void addBatch( Object key, SqlBean sqlBean ) throws SQLException {

        if( ! exist(key) ) {
            statements.put( key, getStatement( key, sqlBean ) );
        }

        Statement statement = statements.get( key );

        addBatch( statement, sqlBean );

    }

    public void commit() {
        if( connection != null ) {
            try {
                connection.commit();
            } catch( SQLException e ) {}
        }
    }

    public void clear() {
        for( Statement statement : statements.values() ) {
            try { statement.close(); } catch( SQLException e ) {}
        }
        statements.clear();
    }


    protected Connection getConnection() {
        if( connection == null ) {
            connection = TransactionManager.getConnection( token, environmentId );
            NLogger.debug( ">> Get Connection : {}", connection );
        }
        return connection;
    }

    public void close() {

        if( connection != null ) {
            TransactionManager.releaseConnection( token, connection );
            connection = null;
        }

        clear();

    }

    public abstract Object  generateKey( SqlBean sqlBean );

    public abstract SqlBean generateSqlBean( SqlNode sqlNode, Object param );

    public abstract Statement getStatement( Object key, SqlBean sqlBean ) throws SQLException;

    public abstract void addBatch( Statement statement, SqlBean sqlBean ) throws SQLException;

}
