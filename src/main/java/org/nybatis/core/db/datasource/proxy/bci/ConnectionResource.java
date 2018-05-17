package org.nybatis.core.db.datasource.proxy.bci;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import org.nybatis.core.reflection.Reflector;

/**
 * Connection resource manager like Statement or ResultSet
 *
 * @author nayasis@gmail.com
 * @since 2018-05-17
 */
public class ConnectionResource {

    public static final Savepoint RELEASE_RESOURCE = new Savepoint() {
        public String getSavepointName() throws SQLException {
            return ConnectionResource.class.getName() + ".CLEAR_RESOURCE";
        }
        public int getSavepointId() throws SQLException {
            return Integer.MIN_VALUE;
        }
    };

    private Set<Statement> poolStatement = new HashSet<>();
    private Set<ResultSet> poolResultset = new HashSet<>();
    private long           lastUsedTime  = System.nanoTime();
    private Runnable       runnable      = null;

    public void addPool( Object object ) {
        if( object == null ) return;
        if( object instanceof ResultSet ) {
            poolResultset.add( (ResultSet) object );
        } else if( object instanceof Statement ) {
            poolStatement.add( (Statement) object );
        }
    }

    public void resetLastUsedTime() {
        lastUsedTime = System.nanoTime();
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void releasePool() {
        for( ResultSet resultset : poolResultset ) {
            if( resultset == null ) continue;
            try { resultset.close(); } catch( SQLException e ) {}
        }
        poolResultset.clear();
        for( Statement statement : poolStatement ) {
            if( statement == null ) continue;
            try { statement.close(); } catch( SQLException e ) {}
        }
        poolStatement.clear();
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable( Runnable runnable ) {
        this.runnable = runnable;
    }

    public void executeRunnable() {
        if( runnable != null ) runnable.run();
    }

    private void resetLastUsedTime( String methodName ) {
        switch( methodName ) {
            case "commit"           :
            case "rollback"         :
            case "executeQuery"     :
            case "getGeneratedKeys" :
            case "getResultSet"     :
            case "execute"          :
            case "executeUpdate"    :
            case "next"             :
                resetLastUsedTime();
        }
    }

    public Statement invoke( Statement statement ) {
        addPool( statement );
        return Reflector.wrapProxy( statement, new Class<?>[] {Statement.class, PreparedStatement.class, CallableStatement.class}, ( proxy, method, arguments ) -> {
            resetLastUsedTime( method.getName() );
            switch( method.getName() ) {
                case "getObject":
                    Object returnValue = method.invoke( statement, arguments );
                    if( returnValue instanceof ResultSet ) {
                        addPool( returnValue );
                    }
                    return returnValue;
                case "executeQuery":
                case "getGeneratedKeys":
                case "getResultSet":
                    ResultSet rs = (ResultSet) method.invoke( statement, arguments );
                    return invoke( rs );
            }
            return method.invoke( statement, arguments );
        });
    }

    public ResultSet invoke( ResultSet resultSet ) {
        addPool( resultSet );
        return Reflector.wrapProxy( resultSet, new Class<?>[] {ResultSet.class}, ( proxy, method, arguments ) -> {
            resetLastUsedTime( method.getName() );
            switch( method.getName() ) {
                case "getObject":
                    Object returnValue = method.invoke( resultSet, arguments );
                    if( returnValue instanceof ResultSet ) {
                        addPool( returnValue );
                    }
                    return returnValue;
            }
            return method.invoke( resultSet, arguments );
        });
    }

}
