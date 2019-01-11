package org.nybatis.core.db.sql.sqlNode;

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

	/**
	 * Dynamic Properties
	 */
	private Boolean countSql         = null;
	private Integer pageSqlStart     = null;
	private Integer pageSqlEnd       = null;
	private String  ormSqlWhere      = null;
	private String  ormSqlOrderBy    = null;

	public SqlProperties() {}

	public SqlProperties( String environmentId, Node element ) {

		// ** xml sample
		// <sql id="selectKey" fetch="50" lobPrefetch="1000" >

		this.environmentId = environmentId;

		setFetchSize( element.getAttrIgnoreCase( "fetch" ) );
		setLobPrefetchSize( element.getAttrIgnoreCase( "lobPrefetch" ) );

	}

	public void clear() {
		countSql           = null;
		pageSqlStart       = null;
		pageSqlEnd         = null;
		ormSqlWhere        = null;
		ormSqlOrderBy      = null;
	}

	public SqlProperties merge( SqlProperties properties ) {

		SqlProperties newProperties = properties.clone();

		if( environmentId   != null ) newProperties.environmentId   = environmentId;
		if( fetchSize       != null ) newProperties.fetchSize       = fetchSize;
		if( lobPrefetchSize != null ) newProperties.lobPrefetchSize = lobPrefetchSize;
		if( countSql        != null ) newProperties.countSql        = countSql;
		if( pageSqlStart    != null ) newProperties.pageSqlStart    = pageSqlStart;
		if( pageSqlEnd      != null ) newProperties.pageSqlEnd      = pageSqlEnd;

		return newProperties;

	}

	public String getEnvironmentId() {
		return environmentId;
	}

	/**
	 * get representative environment id.
	 *
	 * <pre>
	 *
	 * it can be determined like below. (priority is top to bottom.)
	 *
	 *   - the one whici sqlId is joined
	 *   - the one which is assigned by SqlSession or OrmSession
	 *   - GlobalSqlParameter's environment id
	 *   - GlobalSqlParameer's default environment id
	 *   - default environment's id
	 * </pre>
	 *
	 * @return representative environment id
	 */
	public String getRepresentativeEnvironmentId() {
		return Validator.nvl( environmentId, GlobalSqlParameter.getEnvironmentId(), GlobalSqlParameter.getDefaultEnvironmentId(), DatasourceManager.getDefaultEnvironmentId() );
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

	public Integer getPageSqlStart() {
		return pageSqlStart;
	}

	public Integer getPageSqlEnd() {
		return pageSqlEnd;
	}

	public void setPageSql( Integer start, Integer end ) {

		if( start == null || end == null ) {
			start = 1;
			end   = Integer.MAX_VALUE;
		} else if( start == null ) {
			start = 1;
		} else if( end == null ) {
			end   = Integer.MAX_VALUE;
		}

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

		return sb.toString();

	}

	public SqlProperties clone() {
		return Reflector.clone( this );
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
