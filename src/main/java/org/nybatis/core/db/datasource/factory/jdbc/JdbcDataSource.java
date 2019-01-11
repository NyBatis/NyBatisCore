package org.nybatis.core.db.datasource.factory.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Stack;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.factory.parameter.JdbcConnectionProperties;
import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.proxy.ProxyConnection;
import org.nybatis.core.exception.unchecked.DatabaseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StopWatch;
import org.nybatis.core.validation.Validator;

public class JdbcDataSource implements DataSource {

	protected JdbcDatasourceProperties datasourceProperties;
	protected JdbcConnectionProperties connectionProperties;

	protected final Stack<ProxyConnection> connectionPoolIdle   = new Stack<>();
	protected final Stack<ProxyConnection> connectionPoolActive = new Stack<>();

	private ConnectionHealthChecker healthChecker;

	private static final long THREAD_WAIT_MILI_SECONDS = 200;

	public JdbcDataSource( JdbcDatasourceProperties datasourceProperties, JdbcConnectionProperties connectionProperties ) {

		this.datasourceProperties = datasourceProperties;
		this.connectionProperties = connectionProperties;

		runHealthChecker();

	}

	public JdbcDatasourceProperties getDatasourceProperties() {
		return datasourceProperties;
	}

	@Override
    public Connection getConnection() throws SQLException {
		return getConnection( connectionProperties.getUserName(), connectionProperties.getUserPassword() );
    }

	@Override
    public Connection getConnection( String username, String password ) throws SQLException {
		return getProxyConnection( username, password ).getConnection();
    }

	public JdbcConnectionProperties getConnectionProperties() {
		return connectionProperties;
	}

	public ProxyConnection getProxyConnection() throws SQLException {
		return getProxyConnection( connectionProperties.getUserName(), connectionProperties.getUserPassword() );
	}

	public synchronized ProxyConnection getProxyConnection( String username, String password ) {

		if( connectionPoolIdle.isEmpty() ) {

			if( connectionPoolActive.isEmpty() ) {

				int count = datasourceProperties.getPoolMin();

				for( int i = 0; i < count; i++ ) {
					connectionPoolIdle.push( createProxyConnection( username, password ) );
				}

			} else if( connectionPoolActive.size() < datasourceProperties.getPoolMax() ) {

				int count = Math.min( datasourceProperties.getPoolStep(), datasourceProperties.getPoolMax() - connectionPoolActive.size() );

				for( int i = 0; i < count; i++ ) {
					connectionPoolIdle.push( createProxyConnection( username, password ) );
				}

			} else { // Wait for returned connection

				long waitTime = connectionProperties.getNanoTimeout();

				StopWatch stopWatch = new StopWatch();

				int tryCount = 0;

				while( stopWatch.elapsedNanoSeconds() < waitTime ) {

					try {
						Thread.sleep( THREAD_WAIT_MILI_SECONDS );
					} catch( InterruptedException e ) {
						break;
					}

					if( ! connectionPoolIdle.isEmpty() ) break;

					tryCount++;

					NLogger.trace( "... wait for getting idle connection. ({})", tryCount );

				}

				if( connectionPoolIdle.isEmpty() ) {
					NLogger.trace( "Fail to get connection" );
					DatasourceManager.printStatus();
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
		DatasourceManager.printStatus();

		return proxyConnection;

	}

	public NMap getPoolStatus() {

		NMap result = new NMap();

		result.put( "environment", datasourceProperties.getId() );
		result.put( "total",       getPoolCount()               );
		result.put( "active",      connectionPoolActive.size()  );
		result.put( "idle",        connectionPoolIdle.size()    );

		return result;

	}

	public int getPoolCount() {
		return connectionPoolActive.size() + connectionPoolIdle.size();
	}

	private ProxyConnection createProxyConnection() {
		return createProxyConnection( connectionProperties.getUserName(), connectionProperties.getUserPassword() );
	}

	private ProxyConnection createProxyConnection( String username, String password ) {

		if( JdbcDriverManager.registerDriver( connectionProperties.getDriverName() ) ) {
			JdbcDriverManager.setLoginTimeout( connectionProperties.getTimeout() );
		}

		Connection connection;

		if( Validator.isEmpty( username ) && Validator.isEmpty( password ) ) {
			connection = JdbcDriverManager.getConnection( connectionProperties.getUrl() );
		} else {
			connection = JdbcDriverManager.getConnection( connectionProperties.getUrl(), username, password );
		}

		try {
	        connection.setAutoCommit( connectionProperties.isAutoCommit() );
        } catch( SQLException e ) {
        	throw new DatabaseException( e, "Fail to set connection's autocommit configuration to false." );
        }

		ProxyConnection proxyConnection = new ProxyConnection( connection );

		return proxyConnection.setRunner( () -> giveBackConnectionToPool( proxyConnection ) );

	}

	public synchronized void giveBackConnectionToPool( ProxyConnection proxyConnection ) {

		connectionPoolActive.remove( proxyConnection );

		if( proxyConnection.isClosed() ) {

			proxyConnection.destroy();
			connectionPoolIdle.remove( proxyConnection );

		} else {

			if( ! proxyConnection.isAutoCommit() ) {
				proxyConnection.rollback();
			}

			if( ! connectionPoolIdle.contains(proxyConnection) ) {
				connectionPoolIdle.push( proxyConnection );
			}

		}

		NLogger.trace( ">> Release connection" );
		DatasourceManager.printStatus();

    }

	@Override
    public PrintWriter getLogWriter() {
		return DriverManager.getLogWriter();
    }

	@Override
    public void setLogWriter( PrintWriter out ) {
		DriverManager.setLogWriter( out );
    }

	@Override
    public void setLoginTimeout( int seconds ) {
		DriverManager.setLoginTimeout( seconds );
    }

	@Override
    public int getLoginTimeout() {
		return DriverManager.getLoginTimeout();
    }

	@Override
    public Logger getParentLogger() {
		return Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
    }

	@Override
    public <T> T unwrap( Class<T> iface ) throws SQLException {
		throw new SQLException( getClass().getName() + " is not a wrapper." );
    }

	@Override
    public boolean isWrapperFor( Class<?> iface ) {
	    return false;
    }

	public boolean runHealthChecker() {
		if( healthChecker == null && datasourceProperties.isPingEnable() ) {
			healthChecker = new ConnectionHealthChecker( this );
			healthChecker.run();
			return true;
		}
		return true;
	}

	public boolean stopHealthChecker() {
		if( healthChecker == null ) return false;
		healthChecker.interrupt();
		healthChecker = null;
		return true;
	}

}
