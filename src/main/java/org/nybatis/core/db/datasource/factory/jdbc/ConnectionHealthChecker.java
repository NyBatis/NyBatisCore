package org.nybatis.core.db.datasource.factory.jdbc;

import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.proxy.ProxyConnection;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.LimitedQueue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Connection health checker
 */
public class ConnectionHealthChecker {

	private JdbcDataSource        dataSource    = null;
	private LimitedQueue<Integer> poolStatistic = new LimitedQueue<>( 10 );
	private LimitedQueue<Integer> idleStatistic = new LimitedQueue<>( 10 );
	private boolean               shutdown      = false;

	private Thread healthChecker = new Thread( () -> {
        while( ! shutdown ) {
            pingConnections();
			try { Thread.sleep( 1_000 ); } catch( InterruptedException e ) { Thread.currentThread().interrupt(); return; }
        }
    }, generateThreadName("healthChecker") );

	private Thread idleConnectionShrinker = new Thread( () -> {
        while( ! Thread.currentThread().isInterrupted() ) {
            shrinkConnections();
			try { Thread.sleep( 30_000 ); } catch( InterruptedException e ) { Thread.currentThread().interrupt(); return; }
        }
    }, generateThreadName("idleConnectionShrinker") );

	private String generateThreadName( String name ) {
		return String.format( "%s.%s.%s", ConnectionHealthChecker.class.getName(), name, UUID.randomUUID().toString() );
	}

	public ConnectionHealthChecker( JdbcDataSource dataSource ) {
		this.dataSource = dataSource;
	}

	public void run() {
		if( dataSource.getDatasourceProperties().isPingEnable() ) {
			healthChecker.start();
			idleConnectionShrinker.start();
			NLogger.trace( "Connection HealthChecker is started." );
		}
	}

	public void interrupt() {
		healthChecker.interrupt();
		idleConnectionShrinker.interrupt();
	}

	private void pingConnections() {
		List<ProxyConnection> connections = new ArrayList( dataSource.connectionPoolIdle );
		for( ProxyConnection connection : connections ) {
			if( connection == null || connection.isClosed() || connection.getElpasedTimeAfterLastUsed() <= dataSource.datasourceProperties.getPingCycle() ) continue;
			if( connection.ping(dataSource.datasourceProperties.getPingQuery()) == false ) {
				connection.destroy();
			}
		}
	}

	private void shrinkConnections() {

		poolStatistic.add( dataSource.getPoolCount() );
		idleStatistic.add( dataSource.connectionPoolIdle.size() );

		if( ! hasReliableStatistic() ) return;

		JdbcDatasourceProperties properties = dataSource.getDatasourceProperties();

		int closableCount = getLeastCount( idleStatistic ) -  properties.getPoolMin();

		if( closableCount <= 0 ) return;

		NLogger.trace( ">> before shrinking connection pool (environment:{})", properties.getId() );
		DatasourceManager.printStatus();

		for( int i = 0; i < closableCount; i++ ) {
			try {
				ProxyConnection connection = dataSource.getProxyConnection();
				connection.destroy();
				dataSource.giveBackConnectionToPool( connection );
			} catch( SQLException e ) {
				NLogger.error( e );
			}
		}

		NLogger.trace( ">> after shrinking connection pool (environment:{}, closed:[{}]cnt)", properties.getId(), closableCount );
		DatasourceManager.printStatus();

	}

	private boolean hasReliableStatistic() {

		if( ! poolStatistic.isFull() ) return false;

		int prevCount = poolStatistic.get( 0 );

		for( Integer count : poolStatistic ) {
			if( count != prevCount ) return false;
		}

		return true;

	}

	private int getLeastCount( LimitedQueue<Integer> statistic ) {

		int maxCount = Integer.MAX_VALUE;

		for( Integer count : statistic ) {
			maxCount = Math.min( maxCount, count );
		}

		return maxCount;

	}

}
