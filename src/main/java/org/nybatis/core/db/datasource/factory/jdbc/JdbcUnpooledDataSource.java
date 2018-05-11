package org.nybatis.core.db.datasource.factory.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.nybatis.core.db.datasource.factory.parameter.JdbcConnectionProperties;
import org.nybatis.core.db.datasource.proxy.ProxyConnection;
import org.nybatis.core.exception.unchecked.DatabaseException;
import org.nybatis.core.validation.Validator;

public class JdbcUnpooledDataSource implements DataSource {

	private JdbcConnectionProperties connectionProperties;

	public JdbcUnpooledDataSource( JdbcConnectionProperties connectionProperties ) {
		this.connectionProperties = connectionProperties;
	}

	@Override
    public Connection getConnection() throws SQLException {
		return getConnection( connectionProperties.getUserName(), connectionProperties.getUserPassword() );
    }

	@Override
    public synchronized Connection getConnection( String username, String password ) throws SQLException {
		return createProxyConnection( username, password ).getConnection();
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

		return proxyConnection.setRunner( () -> proxyConnection.destroy() );

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
