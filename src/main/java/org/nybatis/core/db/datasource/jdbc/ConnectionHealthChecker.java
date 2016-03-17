package org.nybatis.core.db.datasource.jdbc;

import java.util.Stack;
import java.util.UUID;

import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.proxy.ProxyConnection;
import org.nybatis.core.log.NLogger;


public class ConnectionHealthChecker {

	Stack<ProxyConnection>   idleConnectionPool   = null;
	JdbcDatasourceProperties datasourceProperties = null;

	private Thread healthChecker  = new Thread( new Runnable() {
		public void run() {
			while( true ) {
				try { Thread.sleep( 1_000 ); } catch( InterruptedException e ) {}
				pingConnection();
			}
		}
	}, ConnectionHealthChecker.class.getName() + "." + UUID.randomUUID().toString() );

	public ConnectionHealthChecker( Stack<ProxyConnection> idleConnectionPool, JdbcDatasourceProperties datasourceProperties ) {
		this.idleConnectionPool   = idleConnectionPool;
		this.datasourceProperties = datasourceProperties;
	}

	public void run() {
		if( datasourceProperties.isPingEnable() ) {
			healthChecker.start();
			NLogger.trace( "Connection HealthChecker is started." );
		}
	}

	private void pingConnection() {

		for( ProxyConnection connection : idleConnectionPool ) {

			if( connection.getElapsedMiliSecondsSinceLastUsed() <= datasourceProperties.getPingCycle() ) continue;
			if( connection.ping(datasourceProperties.getPingQuery()) == false ) {
				connection.destroy();
			}

		}

	}


}
