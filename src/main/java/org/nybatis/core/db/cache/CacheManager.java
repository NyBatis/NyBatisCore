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
import org.nybatis.core.validation.Validator;

public class CacheManager {

	private static final Cache  NULL_CACHE = new NullCache();
	private static       Object lock       = new Object();

	private static Map<String, Cache>        cachePool    = new HashMap<>();
	private static Map<String, CacheModel>   cacheModels  = new LinkedHashMap<>();

	public static void registerCacheModel( String id, Class<Cache> klass, Integer size, Integer flushSeconds ) {
		try {
			cacheModels.put( id, new CacheModel(id, klass, size, flushSeconds) );
		} catch( IllegalArgumentException e ) {
			NLogger.warn( e.getMessage() );
		}
	}

	public static void registerCacheModel( String id, String className, String size, String flushSeconds ) {
		try {
			cacheModels.put( id, new CacheModel(id, className, size, flushSeconds) );
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

		if( ! isCacheSql( sqlId ) ) return NULL_CACHE;

		synchronized( lock ) {

			if( ! cachePool.containsKey(sqlId) ) {
				enableCache( sqlId, null, null );
			}

			return Validator.nvl( cachePool.get(sqlId), NULL_CACHE );

        }

	}

	public static boolean isCacheSql( String sqlId ) {

		if( ! SqlRepository.isExist( sqlId ) ) return false;

		SqlProperties properties = SqlRepository.getProperties( sqlId );

		if( ! properties.isCacheEnable() ) return false;

		String cacheId = properties.getCacheId();

		if( ! hasCacheModel( cacheId ) ) {
			NLogger.warn( "Sql[id:{}] does not have cache model[id:{}]. It'll be never cached.", sqlId, cacheId );
			properties.isCacheEnable( false );
			return false;
		}

		return true;

	}

	public static void setCacheProperties( String sqlId, String cacheId, Integer flushCycle ) {

		if( ! SqlRepository.isExist( sqlId ) ) return;

		SqlProperties properties = SqlRepository.getProperties( sqlId );

		properties.setCacheId( cacheId );
		properties.setCacheFlushCycle( flushCycle );
		properties.isCacheEnable( true );

	}

	public static void disableCache( String sqlId ) {

		if( ! SqlRepository.isExist(sqlId) ) return;

		SqlProperties properties = SqlRepository.getProperties( sqlId );

		if( ! properties.isCacheEnable() ) return;

		properties.setCacheId( null );
		properties.clearCacheFlushCycle();
		properties.isCacheEnable( false );

		if( ! cachePool.containsKey(sqlId) ) return;

		Cache cache = cachePool.get( sqlId );
		cache.clear();

		cachePool.remove( sqlId );

	}

	public static void enableCache( String sqlId, String cacheId, Integer flushCycle ) {

		if( ! SqlRepository.isExist(sqlId) ) return;

		SqlProperties properties = SqlRepository.getProperties( sqlId );

		CacheModel cacheModel = cacheModels.get( Validator.nvl( cacheId, properties.getCacheId()) );

		if( cacheModel == null ) {
			NLogger.warn( "sql({})'s cache model({}) is not exist,", sqlId, properties.getCacheId() );
			disableCache( sqlId );
			return;
		}

		try {

			if( ! cachePool.containsKey( sqlId ) ) {

				properties.isCacheEnable( true );
				properties.setCacheId( cacheModel.getId() );

				Cache cache = cacheModel.makeCache();
				cache.setFlushCycle( Validator.nvl( flushCycle, properties.getCacheFlushCycle()) );
				cachePool.put( sqlId, cache );

			}

		} catch( ClassCastException e ) {
			NLogger.warn( e, "cache model({})'s class({}) is fail to initialize. Sql({}) will be never cached.", properties.getCacheId(), cacheModel.getKlass(), sqlId );
			cacheModels.remove( properties.getCacheId() );
			disableCache( sqlId );
		}

	}

	public String toString() {

		NList reportCacheModel = new NList();

		for( CacheModel cacheModel : cacheModels.values() ) {

			reportCacheModel.add( "cacheId", cacheModel.getId() );
			reportCacheModel.add( "class", cacheModel.getKlass().getName() );
			reportCacheModel.add( "size", cacheModel.getSize() );
			reportCacheModel.add( "flush", cacheModel.getFlushSeconds() );

		}

		StringBuilder sb = new StringBuilder();

		sb.append( ">> Registered Cache Model\n" );
		sb.append( reportCacheModel );

		return sb.toString();

	}

}
