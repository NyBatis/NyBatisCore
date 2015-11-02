package org.nybatis.core.db.transaction;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.nybatis.core.context.NThreadLocal;
import org.nybatis.core.context.ThreadRoot;

public class TransactionManager implements Observer {

	/**
	 * ThreadLocal ID (created for each thread)
	 *   - Transaction
	 */
	private static Map<String, Transaction> managedPool = new Hashtable<>();

	static {
		NThreadLocal.addObserver( new TransactionManager() );
	}

	@Override
    public void update( Observable watcher, Object deliveredParameter ) {
		end();
		managedPool.remove( ThreadRoot.getKey() );
    }

	public static void begin( String token ) {
		getTransaction().begin( token );
	}

	public static void end( String token ) {
		getTransaction().end( token );
	}

	public static void commit( String token ) {
		getTransaction().commit( token );
	}

	public static void rollback( String token ) {
		getTransaction().rollback( token );
	}

	public static void end() {
		getTransaction().end();
	}

	public static void commit() {
		getTransaction().commit();
	}

	public static void rollback() {
		getTransaction().rollback();
	}

	public static Connection getConnection( String token, String datasourceId ) {
		return getTransaction().getConnection( token, datasourceId );
	}

	public static void releaseConnection( String token, Connection connection ) {
		getTransaction().releaseConnection( token, connection );
	}

	public static boolean isBegun( String token ) {
		return getTransaction().isBegun( token );
	}

	private static Transaction getTransaction() {

		synchronized( managedPool ) {
			if( ! managedPool.containsKey(ThreadRoot.getKey()) ) {
				managedPool.put( ThreadRoot.getKey(), new Transaction() );
			}
		}

	    return managedPool.get( ThreadRoot.getKey() );

	}

}
