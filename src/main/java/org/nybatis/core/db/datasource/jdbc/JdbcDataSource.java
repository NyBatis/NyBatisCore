package org.nybatis.core.db.datasource.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Stack;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.nybatis.core.db.configuration.connection.JdbcConnectionProperties;
import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.proxy.ProxyConnection;
import org.nybatis.core.exception.unchecked.DatabaseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StopWatcher;

public class JdbcDataSource implements DataSource {

	private JdbcDatasourceProperties datasourceProperties;
	private JdbcConnectionProperties connectionProperties;

	private final Stack<ProxyConnection> connectionPoolIdle   = new Stack<>();
	private final Stack<ProxyConnection> connectionPoolActive = new Stack<>();

	private static final long THREAD_WAIT_MILI_SECONDS = 200;

	public JdbcDataSource( JdbcDatasourceProperties datasourceProperties, JdbcConnectionProperties connectionProperties ) {

		this.datasourceProperties = datasourceProperties;
		this.connectionProperties = connectionProperties;

		if( datasourceProperties.isPingEnable() ) {
			new ConnectionHealthChecker( connectionPoolIdle, this.datasourceProperties ).run();
		}

	}

	public JdbcDatasourceProperties getDatasourceProperties() {
		return datasourceProperties;
	}

	@Override
    public Connection getConnection() throws SQLException {
		return getConnection( connectionProperties.getUserName(), connectionProperties.getUserPassword() );
    }

	@Override
    public synchronized Connection getConnection( String username, String password ) throws SQLException {

		if( connectionPoolIdle.isEmpty() ) {

			if( connectionPoolActive.isEmpty() ) {

				int count = datasourceProperties.getPoolMin();

				for( int i = 0; i < count; i++ ) {
					connectionPoolIdle.push( createProxyConnection( username, password ) );
				}

			} else if( connectionPoolActive.size() < datasourceProperties.getPoolMax() ) {

				int count = Math.min(  datasourceProperties.getPoolStep(), datasourceProperties.getPoolMax() - connectionPoolActive.size() );

				for( int i = 0; i < count; i++ ) {
					connectionPoolIdle.push( createProxyConnection( username, password ) );
				}

			} else { // Wait for returned connection

				long waitTime = connectionProperties.getNanoTimeout();

				StopWatcher stopWatcher = new StopWatcher();

				while( stopWatcher.elapsedNanoSeconds() < waitTime ) {

					try {
	                    Thread.sleep( THREAD_WAIT_MILI_SECONDS );
                    } catch( InterruptedException e ) {
	                    break;
                    }

					if( ! connectionPoolIdle.isEmpty() ) break;

				}

				if( connectionPoolIdle.isEmpty() ) {
					throw new DatabaseException( "Fail to get connection from JdbcConnectionPool. [{}]", connectionProperties );
				}

			}

		}

		ProxyConnection proxyConnection = connectionPoolIdle.pop();

		if( proxyConnection.isClosed() ) {

			proxyConnection.destroy();
			proxyConnection = createProxyConnection( username, password );

		}

		proxyConnection = connectionPoolActive.push( proxyConnection );

		NLogger.trace( "Get connection" );
		logPoolStatus();

		return proxyConnection.getConnection();

    }

	private void logPoolStatus() {

		if( ! NLogger.isTraceEnabled() ) return;

		int activeCount = connectionPoolActive.size();
		int idleCount   = connectionPoolIdle.size();
		int total       = activeCount + idleCount;

		NLogger.trace( "connection pool status ( total : {}, active : {}, idle : {})", total, activeCount, idleCount );

	}

	private ProxyConnection createProxyConnection() {
		return createProxyConnection( connectionProperties.getUserName(), connectionProperties.getUserPassword() );
	}

	private ProxyConnection createProxyConnection( String username, String password ) {

		if( JdbcDriverManager.registerDriver( connectionProperties.getDriverName() ) ) {
			JdbcDriverManager.setLoginTimeout( connectionProperties.getTimeout() );
		}

		Connection connection = JdbcDriverManager.getConnection( connectionProperties.getUrl(), username, password );

		try {
	        connection.setAutoCommit( connectionProperties.isAutoCommit() );
        } catch( SQLException e ) {
        	throw new DatabaseException( e, "Fail to set connection's autocommit configuration to false." );
        }

		ProxyConnection proxyConnection = new ProxyConnection( connection );

		proxyConnection.setRunner( new Runnable() {
			public void run() {
				pushConnection( proxyConnection );
			}
		});

		return proxyConnection;

	}

	private synchronized void pushConnection( ProxyConnection proxyConnection ) {

		connectionPoolActive.remove( proxyConnection );

		if( proxyConnection.isClosed() ) {

			proxyConnection.destroy();

			connectionPoolIdle.remove( proxyConnection );
			connectionPoolIdle.push( createProxyConnection() );

		} else {

			if( ! proxyConnection.isAutoCommit() ) {
				proxyConnection.rollback();
			}

			connectionPoolIdle.push( proxyConnection );

		}

		NLogger.trace( "Release connection" );
		logPoolStatus();

    }

	@Override
    public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
    }

	@Override
    public void setLogWriter( PrintWriter out ) throws SQLException {
		DriverManager.setLogWriter( out );
    }

	@Override
    public void setLoginTimeout( int seconds ) throws SQLException {
		DriverManager.setLoginTimeout( seconds );
    }

	@Override
    public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
    }

	@Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
    }

	@Override
    public <T> T unwrap( Class<T> iface ) throws SQLException {
		throw new SQLException( getClass().getName() + " is not a wrapper." );
    }

	@Override
    public boolean isWrapperFor( Class<?> iface ) throws SQLException {
	    return false;
    }

}
