package org.nybatis.core.db.sql.sqlNode;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.cache.CacheManager;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;
import org.nybatis.core.xml.node.Node;

public class SqlProperties {

	/**
	 * Basic Properties
	 */
	private String  environmentId    = null;
	private Integer fetchSize        = null;
	private Integer lobPrefetchSize  = null;
	private Boolean cacheEnable      = null;
	private String  cacheId          = null;
	private Integer cacheFlushCycle  = null;

	/**
	 * Dynamic Properties
	 */
	private Boolean cacheClear         = null;
	private Boolean autocommit         = null;
	private Boolean countSql           = null;
	private Integer pageSqlStart       = null;
	private Integer pageSqlEnd         = null;
	private String  ormSqlWhere        = null;
	private String  ormSqlOrderBy      = null;

	public SqlProperties() {}

	public SqlProperties( String environmentId, Node element ) {

		// ** xml sample
		// <sql id="selectKey" fetch="50" cache="cacheId" flush="60" lobPrefetch="1000" >

		this.environmentId = environmentId;

		setFetchSize( element.getAttrIgnoreCase( "fetch" ) );
		setLobPrefetchSize( element.getAttrIgnoreCase( "lobPrefetch" ) );
		setCacheId( element.getAttrIgnoreCase( "cache" ) );
		setCacheFlushCycle( element.getAttrIgnoreCase( "flush" ) );

	}

	public SqlProperties merge( SqlProperties properties ) {

		SqlProperties newProperties = properties.clone();

		if( environmentId      != null ) newProperties.environmentId = environmentId;
		if( fetchSize != null ) newProperties.fetchSize = fetchSize;
		if( lobPrefetchSize != null ) newProperties.lobPrefetchSize = lobPrefetchSize;
		if( cacheEnable        != null ) newProperties.cacheEnable = cacheEnable;
		if( cacheId            != null ) newProperties.cacheId = cacheId;
		if( cacheFlushCycle    != null ) newProperties.cacheFlushCycle = cacheFlushCycle;
		if( cacheClear         != null ) newProperties.cacheClear = cacheClear;
		if( autocommit         != null ) newProperties.autocommit = autocommit;
		if( countSql           != null ) newProperties.countSql = countSql;
		if( pageSqlStart       != null ) newProperties.pageSqlStart = pageSqlStart;
		if( pageSqlEnd         != null ) newProperties.pageSqlEnd = pageSqlEnd;

		return newProperties;

	}

	public String getRawEnvironmentId() {
		return environmentId;
	}

	public String getEnvironmentId() {
		return Validator.nvl(
				environmentId,
				Validator.nvl( GlobalSqlParameter.getEnvironmentId(), DatasourceManager.getDefaultEnvironmentId() )
		);
	}

	public void setEnvironmentId( String environmentId ) {
		this.environmentId = environmentId;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public boolean hasSpecificFetchSize() {
		return fetchSize != null;
	}

	public void setFetchSize( Integer count ) {
		this.fetchSize = count;
	}

	public void setFetchSize( String count ) {
		if( StringUtil.isEmpty(count) ) return;
		try {
			this.fetchSize = Integer.parseInt( count );
		} catch( NumberFormatException e ) {}
	}

	public boolean hasSpecificLobPreFetchSize() {
		return lobPrefetchSize != null;
	}

	public Integer getLobPrefetchSize() {
		return lobPrefetchSize;
	}

	public void setLobPrefetchSize( Integer count ) {
		this.lobPrefetchSize = count;
	}

	public void setLobPrefetchSize( String count ) {
		if( StringUtil.isEmpty(count) ) return;
		try {
			this.lobPrefetchSize = Integer.parseInt( count );
		} catch( NumberFormatException e ) {}
	}

	public boolean isCacheEnable() {
		return isTrue( cacheEnable );
	}

	public void isCacheEnable( Boolean yn ) {
		this.cacheEnable = yn;
	}

	public String getCacheId() {
		return cacheId;
	}

	public void setCacheId( String cacheId ) {
		if( ! CacheManager.hasCacheModel( cacheId ) ) return;
		this.cacheId     = cacheId;
		this.cacheEnable = true;
	}

	/**
	 * Get cache flush cycle
	 *
	 * @return cache flush cycle (unit:seconds)
	 */
	public int getCacheFlushCycle() {
		return Validator.nvl( cacheFlushCycle, Const.db.DEFAULT_CACHE_FLUSH_CYCLE );
	}

	public boolean hasSpecificCacheCycle() {
		return cacheFlushCycle != null;
	}

	/**
	 * Set cache flush cycle
	 *
	 * @param seconds flush cycle (unit:seconds)
	 */
	public void setCacheFlushCycle( Integer seconds ) {
		this.cacheFlushCycle = seconds;
	}

	public void setCacheFlushCycle( String seconds ) {
		if( StringUtil.isEmpty(seconds) ) return;
		try {
			this.cacheFlushCycle = Integer.parseInt( seconds );
		} catch( NumberFormatException e ) {}
	}

	public boolean isCacheClear() {
		return cacheClear != null && cacheClear == true;
	}

	public void isCacheClear( Boolean yn ) {
		this.cacheClear = yn;
	}

	public boolean isAutocommit() {
		return isTrue( autocommit );
	}

	public void isAutocommit( Boolean yn ) {
		this.autocommit = yn;
	}

	public void isAutoCommit( String yn ) {
		if( StringUtil.isEmpty(yn) ) return;
		this.autocommit = "true".equalsIgnoreCase( yn ) || "y".equalsIgnoreCase( yn );
	}

	public Integer getPageSqlStart() {
		return pageSqlStart;
	}

	public Integer getPageSqlEnd() {
		return pageSqlEnd;
	}

	public void setPageSql( int start, int end ) {
		pageSqlStart = Math.min( start, end );
		pageSqlEnd   = Math.max( start, end );
	}

	public boolean isPageSql() {
		return pageSqlStart != null && pageSqlEnd != null;
	}

	public void disablePageSql() {
		pageSqlStart = null;
		pageSqlEnd   = null;
	}

	public boolean isCountSql() {
		return isTrue( countSql );
	}

	public void isCountSql( Boolean yn ) {
		this.countSql = yn;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append( String.format("environmentId:[%s]", getEnvironmentId()) );

		if( hasSpecificFetchSize() ) {
			sb.append( String.format(", fetch:[%d]", getFetchSize()) );
		}

		if( isCacheEnable() ) {
			sb.append( String.format(", cache:[%s]", getCacheId()) );
			sb.append( String.format(", cacheFlushCycle:[%d]", getCacheFlushCycle()) );
		}

		if( isAutocommit() ) {
			sb.append( ", autocommit" );
		}

		return sb.toString();

	}

	public SqlProperties clone() {
		return new Reflector().clone( this );
	}

	private boolean isTrue( Boolean value ) {
		return value != null && value == true;
	}

	public String getOrmDynamicSqlWhere() {
		return ormSqlWhere;
	}

	public void setOrmDynamicSqlWhere( String where ) {
		if( StringUtil.isBlank( where ) ) return;
		this.ormSqlWhere = StringUtil.trim( where );
	}

	public String getOrmDynamicSqlOrderBy() {
		return ormSqlOrderBy;
	}

	public void setOrmDynamicSqlOrderBy( String orderBy ) {
		if( StringUtil.isBlank( orderBy ) ) return;
		this.ormSqlOrderBy = StringUtil.trim( orderBy );
	}

	public boolean hasOrmDynamicSql() {
		return this.ormSqlWhere != null || this.ormSqlOrderBy != null;
	}



}
