package org.nybatis.core.db.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.proxy.ProxyConnection;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

/**
 * Transaction
 */
public class Transaction {

	/**
	 * Token
	 *   - EnvironmentId
	 *     - Connection
	 */
	private Map<String, Map<String, Connection>> transactionPool = new Hashtable<>();

    private static final Map<String, Connection> NULL_CONNECTIONS = new HashMap<>();

	public void end( String token ) {
		for( Connection conn : getStoredConnections(token).values() ) {
			try {
                conn.close();
            } catch( SQLException e ) {}
		}
		NLogger.trace( ">> transaction is end. (tokenId : {})", token );
		transactionPool.remove( token );
	}


	public void end() {
		for( String token : transactionPool.keySet() ) {
			end( token );
		}
	}

	public void commit( String token ) {
		if( ! TransactionManager.canExecuteCommit() ) return;
		for( Connection conn : getStoredConnections(token).values() ) {
			try {
                conn.commit();
            } catch( SQLException e ) {}
		}
		NLogger.trace( ">> commit : {}", token );
		end( token );
	}

	public void commit() {
		if( ! TransactionManager.canExecuteCommit() ) return;
		Set<String> keys = new HashSet<>( transactionPool.keySet() );
		for( String token : keys ) {
			commit( token );
		}
	}

	public void rollback( String token ) {
		for( Connection conn : getStoredConnections(token).values() ) {
			try {
				conn.rollback();
			} catch( SQLException e ) {}
		}
		NLogger.trace( ">> rollback : {}", token );
		end( token );

	}

	public void rollback() {
		for( String token : transactionPool.keySet() ) {
			rollback( token );
		}
	}

	private Map<String, Connection> getStoredConnections( String token ) {
        return Validator.nvl( transactionPool.get( token ), NULL_CONNECTIONS );
	}

	private Connection getStoredConnection( String token, String environmentId ) {

		Map<String, Connection> connections = getStoredConnections( token );

		if( connections == null ) return null;

		return connections.get( environmentId );

	}

	public synchronized Connection getConnection( String token, String environmentId ) {

		Assertion.isTrue( DatasourceManager.isExist( environmentId ), new SqlConfigurationException("There is no environment (id:{}}", environmentId) );

		Connection connection = getStoredConnection( token, environmentId );

		if( connection == null ) {

			DataSource dataSource = DatasourceManager.get( environmentId );

			if( dataSource == null ) {
				throw new SqlConfigurationException( "There is no datasource in environment (id:{}).", environmentId );
			}

			try {

				connection = dataSource.getConnection();
				grepConnection( token, environmentId, connection );

            } catch( Exception e ) {
				throw new DatabaseConfigurationException( e, "Fail to get connection (environmentId : {})", environmentId );
			}

		}

		return connection;

	}

	public boolean isBegun( String token ) {
		return transactionPool.containsKey( token );
	}

	public void begin( String token ) {
		NLogger.trace( ">> transaction is begun. (tokenId : {})", token );
		transactionPool.putIfAbsent( token, new HashMap<>() );
	}
	
	private void grepConnection( String token, String environmentId, Connection connection ) {

		if( ! isBegun(token) || isAutocommit(connection) ) return;

		Map<String, Connection> connections = transactionPool.get( token );

		if( connections == null ) return;

		connections.putIfAbsent( environmentId, connection );

	}

	private boolean isAutocommit( Connection connection ) {
		try {
			return connection.getAutoCommit() == true;
		} catch( SQLException e ) {
			NLogger.error( e );
			return false;
		}
	}

	public void releaseConnection( String token, Connection connection ) {

        if( connection == null ) return;

        try {
            if( ! isBegun(token) ) {
                connection.close();
            } else {
				if( isAutocommit( connection ) ) {
					connection.close();
				} else {
					// It it not real releaseSavepoint !
					// merely clear resources used by connection like Statement, PreparedStatement, CallableStatement and ResultSet.
					connection.releaseSavepoint( ProxyConnection.RELEASE_RESOURCE );
				}
            }
        } catch( SQLException e ) {
            NLogger.error( e );
        }

    }

}
