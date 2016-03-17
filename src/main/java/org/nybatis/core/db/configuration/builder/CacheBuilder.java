package org.nybatis.core.db.configuration.builder;

import org.nybatis.core.cache.Cache;
import org.nybatis.core.cache.implement.FifoCache;
import org.nybatis.core.cache.implement.LruCache;
import org.nybatis.core.conf.Const;
import org.nybatis.core.db.cache.CacheManager;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.node.Node;

public class CacheBuilder {

	private PropertyResolver prop = new PropertyResolver();

	public CacheBuilder() {}

	public CacheBuilder( PropertyResolver propertyResolver ) {
		prop = propertyResolver;
	}

	public void setCache( Node cache ) {

		String cacheId = prop.getAttrVal( cache, "id" );

		if( StringUtil.isEmpty( cacheId ) ) {
			NLogger.warn( "Cache element of database configuration has no id.\n\n{}", cache );
			return;
		}

		// <cache id="fifo" class="nayasis.common.cache.fifoCache" size="512" flush="60_000">
		CacheManager.registerCacheModel(
			prop.getAttrVal(cache, "id"    ),
			prop.getAttrVal(cache, "class" ),
			prop.getAttrVal(cache, "size"  ),
			prop.getAttrVal(cache, "flush" )
		);

	}

	public void setDefaultCache() {
		setDefaultCache( "lru",  LruCache.class  );
		setDefaultCache( "fifo", FifoCache.class );
	}

	private void setDefaultCache( String cacheId, Class<? extends Cache> klass ) {

		if( CacheManager.hasCacheModel(cacheId) ) return;

		CacheManager.registerCacheModel(
			cacheId,
			klass.getName(),
			String.valueOf( Const.db.DEFAULT_CACHE_CAPACITY    ),
			String.valueOf( Const.db.DEFAULT_CACHE_FLUSH_CYCLE_SECONDS )
		);

	}


	public void checkEachSqlCache() {

		SqlRepository sqlRepository = new SqlRepository();

		for( SqlNode sqlNode : sqlRepository.getSqls() ) {
			CacheManager.isCacheSql( sqlNode.getSqlId() );
		}

	}

}
