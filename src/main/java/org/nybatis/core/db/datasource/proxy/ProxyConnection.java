package org.nybatis.core.db.datasource.proxy;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import org.nybatis.core.db.datasource.proxy.bci.ConnectionModifier;
import org.nybatis.core.db.datasource.proxy.bci.ConnectionResource;
import org.nybatis.core.log.NLogger;

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

    private Connection realConnection;
	private int        hashCode;

    public ProxyConnection( Connection connection ) {

    	this.realConnection  = connection;
		this.hashCode        = realConnection.hashCode();

		ConnectionModifier.$.modify( connection );

		NLogger.debug( "modified !!" );

    }

    public ProxyConnection setRunner( Runnable runner ) {
		getResource().setRunnable( runner );
		return this;
    }

	private ConnectionResource getResource() {
		return ConnectionModifier.$.getResource( realConnection );
	}

	public int hashCode() {
        return hashCode;
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
        return (int) ( ( System.nanoTime() - getResource().getLastUsedTime() ) / 1_000_000 );
    }

    private void resetLastUsedTime() {
		getResource().resetLastUsedTime();
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
        return realConnection;
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
	        return true;
        }
    }

    public void destroy() {
    	if( realConnection == null ) return;
//		try { realConnection.close(); } catch( SQLException e ) {}
		ConnectionModifier.$.removeResource( realConnection );
		realConnection = null;
    }

	public void rollback() {
		try {
			if( realConnection == null ) realConnection.rollback();
		} catch ( SQLException e ) {
			NLogger.error( e );
		}
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
			NLogger.error( "{}\n\tPing SQL : [{}]", e, pingSql );
        	return false;
        } finally {
        	if( stmt != null ) try { stmt.close(); } catch( SQLException e ) {}
        }

        resetLastUsedTime();
        return true;

	}

}
