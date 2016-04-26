package org.nybatis.core.db.datasource.factory.jdbc;

import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.proxy.ProxyConnection;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.LimitedQueue;

import java.sql.SQLException;
import java.util.UUID;


/**
 * Connection health checker
 */
public class ConnectionHealthChecker {

	private JdbcDataSource        dataSource    = null;
	private LimitedQueue<Integer> poolStatistic = new LimitedQueue<>( 10 );
	private LimitedQueue<Integer> idleStatistic = new LimitedQueue<>( 10 );

	private Thread healthChecker = new Thread( () -> {
        while( true ) {
            pingConnections();
			sleep( 1_000 );
        }
    }, generateThreadName("healthChecker") );

	private Thread idleConnectionShrinker = new Thread( () -> {
        while( true ) {
            shrinkConnections();
			sleep( 30_000 );
        }
    }, generateThreadName("idleConnectionShrinker") );

	private void sleep( long millis ) {
		try { Thread.sleep( millis ); } catch( InterruptedException e ) {}
	}

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

	private void pingConnections() {
		for( ProxyConnection connection : dataSource.connectionPoolIdle ) {
			if( connection.getElpasedTimeAfterLastUsed() <= dataSource.datasourceProperties.getPingCycle() ) continue;
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

		for( int i = 0; i < closableCount; i++ ) {
			try {
				ProxyConnection connection = dataSource.getProxyConnection();
				connection.destroy();
				dataSource.giveBackConnectionToPool( connection );
			} catch( SQLException e ) {
				NLogger.error( e );
			}
		}

		NLogger.trace( ">> shrink connecton pool (environmenti:{}, shrinked connection count:{}, current pool count:", properties.getId(), closableCount, dataSource.getPoolCount() );

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
