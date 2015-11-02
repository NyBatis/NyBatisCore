package org.nybatis.core.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

/**
 * ThreadLocal
 *
 * @author nayasis@gmail.com
 *
 */
public final class NThreadLocal {

	private static NThreadLocalWatcher watcher = new NThreadLocalWatcher();

	private static Map<String, Map<String, Object>> threadLocal = new HashMap<>();

	private NThreadLocal() {}

	private static Object lock = new Object();

	/**
	 * <code>{@link #initialize}</code> 초기화 작업시 같이 실행될 Observer를 등록한다.
	 *
	 * @param observer
	 */
	public static void addObserver( Observer observer ) {
		watcher.addObserver( observer );
	}

	/**
	 * key 값으로 저장된 object를 가져온다.
	 *
	 * @param key key
	 * @return key key로 저장된 object
	 */
	public static Object get( String key ) {
		return getThreadLocal().get( key );
	}

	private static Map<String, Object> getThreadLocal() {

		synchronized( lock ) {
			if( ! threadLocal.containsKey(ThreadRoot.getKey()) ) {
				threadLocal.put( ThreadRoot.getKey(), new HashMap<>() );
			}
			lock.notifyAll();
        }

		return threadLocal.get( ThreadRoot.getKey() );

	}

	/**
	 * key 값으로 object를 저장한다.
	 *
	 * @param key   key
	 * @param value 저장할 값
	 */
	public static void set( String key, Object value ) {
		getThreadLocal().put( key, value );
	}

	/**
	 * key 값으로 저장된 object를 삭제한다.
	 *
	 * @param key key
	 */
	public static void remove( String key ) {
		getThreadLocal().remove( key );
	}

	/**
	 * key 값으로 저장된 object가 존재하는지 여부를 확인한다.
	 *
	 * @param key key
	 * @return key key로 저장된 object 존재여부
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
			threadLocal.remove( ThreadRoot.getKey() );
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
		return new ArrayList<String>( keySet() );
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

}
