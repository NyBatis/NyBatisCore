package org.nybatis.core.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

/**
 * Nybatis ThreadLocal
 *
 * <pre>
 *     It is simple static pool.
 *     The value made in thread local stored it with thread local's unique key issued by {@link ThreadRoot}.
 *     The unique key to distinguish thread local is consisted with UUID.
 * </pre>
 *
 * @author nayasis@gmail.com
 *
 */
public final class NThreadLocal {

	private static NThreadLocalWatcher watcher = new NThreadLocalWatcher();

	private static Map<String, Map<String, Object>> staticPool = new HashMap<>();

	private NThreadLocal() {}

	private static Object lock = new Object();

	/**
	 * add observer to run with when <code>{@link #clear()}</code> is called
	 *
	 * @param observer observer
	 */
	public static void addObserver( Observer observer ) {
		watcher.addObserver( observer );
	}

	/**
	 * Get value by key
	 *
	 * @param key key to distingush value
	 * @return value to store
	 */
	public static Object get( String key ) {
		return getThreadLocal().get( key );
	}

	private static Map<String, Object> getThreadLocal() {

		synchronized( lock ) {
			if( ! staticPool.containsKey(ThreadRoot.getKey()) ) {
				staticPool.put( ThreadRoot.getKey(), new HashMap<>() );
			}
			lock.notifyAll();
        }

		return staticPool.get( ThreadRoot.getKey() );

	}

	/**
	 * Store value
	 *
	 * @param key   key to distingush value
	 * @param value value to store
	 */
	public static void set( String key, Object value ) {
		getThreadLocal().put( key, value );
	}

	/**
	 * Remove value by key
	 *
	 * @param key key key to distingush value
	 */
	public static void remove( String key ) {
		getThreadLocal().remove( key );
	}

	/**
	 * Check whether key is exist.
	 *
	 * @param key key to distingush value
	 * @return true if key is exist.
	 */
	public static boolean containsKey( String key ) {
		return getThreadLocal().containsKey( key );
	}

	/**
	 * initialize thread local
	 *
	 * <pre>
	 * It also nofify other thread local worked in NayasisCommon library
	 * </pre>
	 */
	public static void clear() {

		watcher.notifyObservers();

		synchronized( lock ) {
			staticPool.remove( ThreadRoot.getKey() );
			lock.notifyAll();
        }

		watcher.notifyObservers( ThreadRoot.WATCHER_KEY );

	}

	/**
	 * get all keys stored in thread local
	 *
	 * @return keys stored in thread local
	 */
	public static List<String> keyList() {
		return new ArrayList<>( keySet() );
	}

	/**
	 * get all keys stored in thread local
	 *
	 * @return keys stored in thread local
	 */
	public static Set<String> keySet() {
		return getThreadLocal().keySet();
	}

	/**
	 * get all values stored in thread local
	 *
	 * @return values stored in thread local
	 */
	public static Collection<Object> values() {
		return getThreadLocal().values();
	}

	/**
	 * Get static pool
	 *
	 * @return static pool to store indivisual thread local ( 'Key' is the unique key to distinguish indivisual thread local. )
	 */
	public static Map<String, Map<String, Object>> getStaticPool() {
		return staticPool;
	}

}
