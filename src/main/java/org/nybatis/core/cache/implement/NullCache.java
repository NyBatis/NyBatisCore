package org.nybatis.core.cache.implement;

import org.nybatis.core.cache.Cache;

public class NullCache implements Cache {

	@Override
    public int size() {
	    return 0;
    }

	@Override
    public void setCapacity( int size ) {}

    @Override
    public void setFlushCycle( int seconds ) {
    }

    @Override
    public void put( Object key, Object value ) {}

	@Override
    public void putIfAbsent( Object key, Object value ) {}

	@Override
    public Object get( Object key ) {
	    return null;
    }

	@Override
    public void clear( Object key ) {}

	@Override
    public void clear() {}

}
