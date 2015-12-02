package org.nybatis.core.db.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.nybatis.core.context.NThreadLocal;
import org.nybatis.core.context.ThreadRoot;
import org.nybatis.core.log.NLogger;

public class TransactionToken implements Observer {

	private static Map<String, Integer> tokenPool = new HashMap<>();

	private static Object lock = new Object();

	static {
		NThreadLocal.addObserver( new TransactionToken() );
	}

	@Override
	public void update( Observable watcher, Object deliveredParameter ) {
		init();
	}

	public static void init() {
		tokenPool.remove( ThreadRoot.getKey() );
	}

	public static String createToken() {

		String key = ThreadRoot.getKey();

		Integer currentToken = null;

		synchronized( lock ) {

			currentToken = tokenPool.get( key );

			if( currentToken == null ) currentToken = 0;

			currentToken = currentToken + 1;

			tokenPool.put( key, currentToken );

			lock.notifyAll();

		}

		String token = String.format( "%s::%d", key, currentToken );
		NLogger.trace( ">> createToken : {}", token );
		return token;

	}

	public static String getDefaultToken() {
		String token = String.format( "%s::%d", ThreadRoot.getKey(), 0 );
		NLogger.trace( ">> defaultToken : {}", token );
		return token;
	}



}