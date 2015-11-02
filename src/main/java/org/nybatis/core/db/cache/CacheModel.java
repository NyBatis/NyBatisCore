package org.nybatis.core.db.cache;

import org.nybatis.core.cache.Cache;
import org.nybatis.core.conf.Const;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;

public class CacheModel {

	private String       id;
	private Class<Cache> klass;
	private int          size  = Const.db.DEFAULT_CACHE_CAPACITY;
	private int          flush = Const.db.DEFAULT_CACHE_FLUSH_CYCLE;


	public CacheModel( String id, String className, String size, String flush ) {

		Assertion.isNotEmpty( id, "cache's id is missing." );

		this.id = id;

		setKlass( className );

		if( StringUtil.isNotBlank(size)  ) this.size  = new PrimitiveConverter( size  ).toInt();
		if( StringUtil.isNotBlank(flush) ) this.flush = new PrimitiveConverter( flush ).toInt();

	}

	public Class<Cache> getKlass() {
	    return klass;
    }

	@SuppressWarnings( "unchecked" )
    private void setKlass( String className ) {

		ClassUtil classUtil = new ClassUtil();

		try {
			klass = (Class<Cache>) ClassUtil.getClass( className );
        } catch( ClassNotFoundException | ClassCastException e ) {
        	throw new IllegalArgumentException( StringUtil.format("cache[id:{}]'s class({}) is not exist.", getId(), className) );
        }

    }

	public int getSize() {
	    return size;
    }

	public int getFlush() {
	    return flush;
    }

	public String getId() {
	    return id;
    }

	public String toString() {
		return String.format( "cacheId: %s, class: %s, size: %d, flush: %d", id, klass.getName(), size, flush );
	}

	public Cache makeCache() {

		Cache cache = new ClassUtil().getInstance( klass );

		cache.setCapacity( size );

		return cache;

	}

}
