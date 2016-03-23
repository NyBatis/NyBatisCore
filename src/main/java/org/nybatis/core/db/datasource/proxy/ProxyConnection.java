package org.nybatis.core.db.datasource.proxy;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.mapper.MethodInvocator;

/**
 * Proxy connection
 *
 * @author nayasis@gmail.com
 * @since  2015-01-03
 */
public class ProxyConnection {

	public static final Savepoint RELEASE_RESOURCE = new Savepoint() {
		public String getSavepointName() throws SQLException {
			return ProxyConnection.class.getName() + ".CLEAR_RESOURCE";
		}
		public int getSavepointId() throws SQLException {
			return Integer.MIN_VALUE;
		}
	};

    private Connection      realConnection;
    private Connection      proxyConnection;
    private long            lastUsedTime; // nano time
    private Runnable        runnable;

    private Set<Statement> poolStatement = new HashSet<>();
    private Set<ResultSet> poolResultset = new HashSet<>();

    public ProxyConnection( Connection connection ) {

    	this.realConnection  = connection;
    	this.proxyConnection = invokeConnection( connection );

		resetLastUsedTime();

    }

    public ProxyConnection setRunner( Runnable runner ) {
    	this.runnable = runner;
		return this;
    }

    public int hashCode() {
        return realConnection.hashCode();
    }

    public boolean equals( Object object ) {
        if ( object != null && (object instanceof ProxyConnection || object instanceof Connection) ) {
        	return hashCode() == object.hashCode();
        }
        return false;
    }

	/**
	 * get duration mili seconds from connection last used time
	 *
	 * @return mili seconds
	 */
    public int getElpasedTimeAfterLastUsed() {
        return (int) ( ( System.nanoTime() - lastUsedTime ) / 1_000_000 );
    }

    private void resetLastUsedTime() {
    	lastUsedTime = System.nanoTime();
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

    public Connection getRealConnection() {
        return realConnection;
    }

    public Connection getConnection() {
        return proxyConnection;
    }

    public boolean isAutoCommit() {
    	try {
    		return realConnection.getAutoCommit();
    	} catch( SQLException e ) {
    		NLogger.error( e );
        	return false;
        }
    }

    public boolean isClosed() {
    	if( realConnection == null ) return true;
    	try {
	        return realConnection.isClosed();
        } catch( SQLException e ) {
			NLogger.error( e );
	        return false;
        }
    }

    public void destroy() {
    	releaseResource();
    	if( realConnection == null ) return;
		try { realConnection.close(); } catch( SQLException e ) {}
		realConnection = null;
    }

	public void rollback() {
		try {
			if( realConnection == null ) realConnection.rollback();
		} catch ( SQLException e ) {
			NLogger.error( e );
		}
	}

	public void releaseResource() {

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

	/**
	 * send ping query to database
	 *
	 * @param pingSql ping query
	 * @return ping query execution y/n
	 */
	public boolean ping( String pingSql ) {

		if( isClosed() ) return false;

		Statement stmt = null;
        try {
	        stmt = realConnection.createStatement();
	        stmt.executeQuery( pingSql );
        } catch( SQLException e ) {
        	return false;
        } finally {
        	if( stmt != null ) try { stmt.close(); } catch( SQLException e ) {}
        }

        resetLastUsedTime();
        return true;

	}

	private Connection invokeConnection( Connection connection ) {

		return Reflector.wrapProxy( connection, new Class<?>[] {Connection.class}, new MethodInvocator() {
			public Object invoke( Object proxy, Method method, Object[] arguments ) throws Throwable {

				resetLastUsedTime( method.getName() );

				switch( method.getName() ) {

					case "close":

						releaseResource();
						if( runnable != null ) runnable.run();
						return null;

					case "createStatement":
					case "prepareStatement":
					case "prepareCall":
						return invokeStatement( method.invoke( connection, arguments ) );

					case "releaseSavepoint":
						if( arguments != null && arguments[0] == RELEASE_RESOURCE ) {
							releaseResource();
							return null;
						}

						break;

				}

				return method.invoke( connection, arguments );

			}

		} );

	}

    private Object invokeStatement( Object statement ) {

		poolStatement.add( (Statement) statement );

        return Reflector.wrapProxy( statement, new Class<?>[] {Statement.class, PreparedStatement.class, CallableStatement.class}, ( proxy, method, arguments ) -> {

            resetLastUsedTime( method.getName() );

            switch( method.getName() ) {

                case "getObject":

                    Object returnValue = method.invoke( statement, arguments );

                    if( returnValue instanceof ResultSet ) {
                        poolResultset.add( (ResultSet) returnValue );
                    }

                    return returnValue;

                case "executeQuery":
                case "getGeneratedKeys":
                case "getResultSet":
                    ResultSet rs = (ResultSet) method.invoke( statement, arguments );
                    return invokeResultSet( rs );

            }

            return method.invoke( statement, arguments );

        });

	}

    private ResultSet invokeResultSet( ResultSet resultSet ) {

    	poolResultset.add( resultSet );

    	return Reflector.wrapProxy( resultSet, new Class<?>[] {ResultSet.class}, ( proxy, method, arguments ) -> {

            resetLastUsedTime( method.getName() );

            switch( method.getName() ) {

                case "getObject":

                    Object returnValue = method.invoke( resultSet, arguments );

                    if( returnValue instanceof ResultSet ) {
                        poolResultset.add( (ResultSet) returnValue );
                    }

                    return returnValue;

            }

            return method.invoke( resultSet, arguments );

        });

    }

}
