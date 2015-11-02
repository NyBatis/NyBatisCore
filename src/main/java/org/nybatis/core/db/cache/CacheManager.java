package org.nybatis.core.db.cache;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nybatis.core.cache.Cache;
import org.nybatis.core.cache.implement.NullCache;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;

public class CacheManager {

	private static final Cache NULL_CACHE = new NullCache();
	private static       Object lock       = new Object();

	private static Map<String, Cache>        cachePool    = new HashMap<>();
	private static Map<String, CacheModel>   cacheModels  = new LinkedHashMap<>();

	public static void registerCacheModel( String id, String className, String size, String flush ) {

		try {
			CacheModel cacheModel = new CacheModel( id, className, size, flush );
			cacheModels.put( cacheModel.getId(), cacheModel );
		} catch( IllegalArgumentException e ) {
			NLogger.warn( e.getMessage() );
		}

	}

	public static boolean hasCacheModel( String cacheId ) {
		return cacheId != null && cacheModels.containsKey( cacheId );
	}

	public static void clear() {
		for( Cache cache : cachePool.values() ) {
			cache.clear();
		}
	}

	public static Cache getCache( String sqlId ) {

		if( ! isCacheSql(sqlId) ) return NULL_CACHE;

		synchronized( lock ) {

			if( cachePool.containsKey(sqlId) ) return cachePool.get( sqlId );

			registerCache( sqlId );

			Cache cache = cachePool.get( sqlId );

			return ( cache == null ) ? NULL_CACHE : cache;

        }

	}

	public static boolean isCacheSql( String sqlId ) {

		SqlProperties properties = SqlRepository.getProperties( sqlId );

		if( ! properties.isCacheEnable() ) return false;

		String cacheId = properties.getCacheId();

		if( ! hasCacheModel(cacheId) ) {
			NLogger.warn( "Sql[id:{}] does not have cache model[id:{}]. It'll be never cached.", sqlId, cacheId );
			properties.isCacheEnable( false );
			return false;
		}

		return true;

	}

	public static void registerCache( String sqlId ) {

		SqlProperties prop = SqlRepository.getProperties( sqlId );

		CacheModel cacheModel = cacheModels.get( prop.getCacheId() );

		try {

			Cache cache = cacheModel.makeCache();

			cache.setFlushCycle( prop.getCacheFlushCycle() );

			cachePool.put( sqlId, cache );

		} catch( ClassCastException e ) {

			cacheModels.remove( prop.getCacheId() );
			prop.setCacheId( null );
			prop.isCacheEnable( false );

			NLogger.warn( e, "cache[id:{}]'s class({}) is fail to initialize. Sql[id:{}] will be never cached.", prop.getCacheId(), cacheModel.getKlass(), sqlId );

		}

	}

	public String toString() {

		NList reportCacheModel = new NList();

		for( CacheModel cacheModel : cacheModels.values() ) {

			reportCacheModel.addRow( "cacheId", cacheModel.getId() );
			reportCacheModel.addRow( "class", cacheModel.getKlass().getName() );
			reportCacheModel.addRow( "size", cacheModel.getSize() );
			reportCacheModel.addRow( "flush", cacheModel.getFlush() );

		}

		StringBuilder sb = new StringBuilder();

		sb.append( ">> Registered Cache Model\n" );
		sb.append( reportCacheModel );

		return sb.toString();

	}

}
