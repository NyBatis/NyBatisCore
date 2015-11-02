package org.nybatis.core.cache.implement;

import java.util.LinkedHashMap;
import java.util.Map;

public class FifoCache extends LruCache {

	@Override
	public void setCapacity( int capacity ) {

		map = new LinkedHashMap<Object, Object>( capacity, .75F, false ) {

            private static final long serialVersionUID = -1041533883153692789L;

			@Override
			protected boolean removeEldestEntry( Map.Entry<Object, Object> eldest ) {
				return size() > capacity;
			}
		};

	}

}
