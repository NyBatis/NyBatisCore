package org.nybatis.core.db.cache;

import org.nybatis.core.cache.Cache;
import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

public class CacheModel {

	private String       id;
	private Class<Cache> klass;
	private int          size         = Const.db.DEFAULT_CACHE_CAPACITY;
	private int          flushSeconds = Const.db.DEFAULT_CACHE_FLUSH_CYCLE_SECONDS;

	public CacheModel( String id, Class<Cache> klass, Integer size, Integer flushSeconds ) {
		setId( id );
		this.klass = klass;
		if( hasValue(size) ) this.size = size;
		if( hasValue(flushSeconds) ) this.flushSeconds = flushSeconds;

	}

	private boolean hasValue( Integer integer ) {
		return integer != null && integer > 0;
	}

	private boolean hasValue( String integer ) {
		return Validator.isNotEmpty( integer );
	}

	public CacheModel( String id, String className, String size, String flushSeconds ) {
		setId( id );
		setKlass( className );
		if( hasValue( size )  ) this.size  = new PrimitiveConverter( size ).toInt();
		if( hasValue( flushSeconds ) ) this.flushSeconds = new PrimitiveConverter( flushSeconds ).toInt();

	}

	private void setId( String id ) {
		Assertion.isNotEmpty( id, "cache's id is missing." );
		this.id = id;
	}

	public Class<Cache> getKlass() {
	    return klass;
    }

	@SuppressWarnings( "unchecked" )
    private void setKlass( String className ) {
		Assertion.isNotEmpty( className, new IllegalArgumentException( StringUtil.format( "Class of cache(id:{}) is null", id ) ) );
		try {
			klass = (Class<Cache>) ClassUtil.getClass( className );
        } catch( ClassNotFoundException | ClassCastException e ) {
			NLogger.error( e );
        	throw new IllegalArgumentException( StringUtil.format("cache(id:{})'s class({}) is not exist.\n\n{}", getId(), className), e );
        }

    }

	private void setKlass( Class<Cache> klass ) {
		Assertion.isNotEmpty( klass, new IllegalArgumentException( StringUtil.format( "Class of cache(id:{}) is null", id ) ) );
		this.klass = klass;
	}

	public int getSize() {
	    return size;
    }

	public int getFlushSeconds() {
	    return flushSeconds;
    }

	public String getId() {
	    return id;
    }

	public String toString() {
		return String.format( "cacheId: %s, class: %s, size: %d, flush: %d", id, klass.getName(), size, flushSeconds );
	}

	public Cache makeCache() {

		Cache cache = new ClassUtil().getInstance( klass );

		cache.setCapacity( size );

		return cache;

	}

}
