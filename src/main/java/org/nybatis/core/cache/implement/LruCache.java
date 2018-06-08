package org.nybatis.core.cache.implement;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nybatis.core.cache.Cache;
import org.nybatis.core.util.StopWatch;

public class LruCache implements Cache {

	protected Map<Object, Object>      map            = null;
	protected Map<Object, StopWatch> mapAccess      = new HashMap<>();
	private   int                      flushCycle     = Integer.MAX_VALUE;
	private   boolean                  hasFlushCycle  = false;

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public void setCapacity( int capacity ) {

		map = new LinkedHashMap<Object, Object>( capacity, .75F, true ) {

			private static final long serialVersionUID = 8870565267049463277L;

			@Override
			protected boolean removeEldestEntry( Map.Entry<Object, Object> eldest ) {
				return size() > capacity;
			}
		};

	}

	@Override
	public void setFlushCycle( int seconds ) {
		this.flushCycle    = seconds;
		this.hasFlushCycle = seconds != Integer.MAX_VALUE;
	}

	@Override
	public void put( Object key, Object value ) {
		map.put( key, value );
		resetAccessTime( key );
	}

	public StopWatch getWatcher( Object key ) {
		if( ! mapAccess.containsKey( key ) ) {
			mapAccess.put( key, new StopWatch() );
		}
		return mapAccess.get( key );
	}

	private void resetAccessTime( Object key ) {
		if( ! hasFlushCycle ) return;
		getWatcher( key ).reset();
	}

	@Override
	public void putIfAbsent( Object key, Object value ) {
		map.putIfAbsent( key, value );
		resetAccessTime( key );
	}

	@Override
	public Object get( Object key ) {

		Object val = map.get( key );

		if( val != null && hasFlushCycle ) {
			if( getWatcher( key ).elapsedSeconds() >= flushCycle ) {
				clear( key );
				return null;
			}
		}

		return val;
	}

	@Override
	public void clear( Object key )	{
		map.remove( key );
		mapAccess.remove( key );
	}

	@Override
	public void clear() {
		map.clear();
		mapAccess.clear();
	}

}
