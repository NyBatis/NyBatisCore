package org.nybatis.core.db.session.executor;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.session.executor.util.DbExecUtils;
import org.nybatis.core.db.sql.sqlMaker.BindParam;
import org.nybatis.core.db.sql.sqlMaker.BindStruct;
import org.nybatis.core.db.sql.sqlMaker.QueryResolver;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;

import java.util.List;
import java.util.Map;

public class SqlBean {

	private SqlNode       sqlNode       = null;
	private SqlProperties properties    = null;
	private NMap          sqlParam      = null;
	private Object        inputParam    = null;

	private QueryResolver queryResolver = null;

	public SqlBean( SqlNode sqlNode ) {
		this( sqlNode, null );
	}

	public SqlBean( SqlNode sqlNode, Object parameter ) {
		this.sqlNode    = sqlNode;
		this.inputParam = parameter;
	}

	/**
	 * Set Parameter<br/>
	 *
	 * Do not call this method after build.
	 *
	 * @param parameter parameter to set
	 * @return self instance
	 */
	public SqlBean setParameter( Object parameter ) {
		this.inputParam = parameter;
		return this;
	}

	public SqlBean init( SqlProperties properties ) {

		this.properties = properties.merge( sqlNode.getProperties() );

		this.sqlParam   = DbExecUtils.getParameterMergedWithGlobalParam( inputParam );

		if( properties.isPageSql() ) {
			sqlParam.put( DatabaseAttribute.PAGE_PARAM_START, this.properties.getPageSqlStart() );
			sqlParam.put( DatabaseAttribute.PAGE_PARAM_END,   this.properties.getPageSqlEnd()   );
		}

		if( properties.hasOrmDynamicSql() ) {
			sqlParam.put( Const.db.ORM_PARAMETER_WHERE,    this.properties.getOrmDynamicSqlWhere() );
			sqlParam.put( Const.db.ORM_PARAMETER_ORDER_BY, this.properties.getOrmDynamicSqlOrderBy() );
		}

		setEnvironmentId( properties.getEnvironmentId() );

		return this;

	}

	public SqlBean build() {

		setDatabaseParameter();

		String query = sqlNode.getText( sqlParam, properties.isPageSql(), properties.isCountSql() );

		try {

			queryResolver = new QueryResolver( query, sqlParam );

			return this;

		} catch( StringIndexOutOfBoundsException e ) {
			throw new SqlParseException( e, "{} Error on parameter binding because of invalid sql parameter syntax.({})\n>> Error SQL :\n{}", toString(), e.getMessage(), query );

		} catch( IllegalArgumentException | SqlConfigurationException e ) {
			throw new SqlConfigurationException( e, "{} {}", toString(), e.getMessage() );
		}

	}

	private void setEnvironmentId( String environmentId ) {

		if( environmentId == null ) {
			throw new DatabaseConfigurationException( "Database configuration does not loaded." );
		}

		properties.setEnvironmentId( environmentId );

	}

	public String toString() {

		String sqlId = getSqlId();

		if( sqlId == null ) {
			return String.format( "SQL(datasource:%s)", getEnvironmentId() );
		} else {
			return String.format( "SQL(id:%s, datasource:%s)", sqlId, getEnvironmentId() );
		}

	}

	public Map<String, SqlNode> getKeySqls() {
		return sqlNode.getKeySqls();
	}

	public String getEnvironmentId() {
		return properties.getEnvironmentId();
	}

	public String getSqlId() {
		return sqlNode.getSqlId();
	}

	public String getSql() {
		return queryResolver.getSql();
	}

	public String getDebugSql() {
		return queryResolver.getDebugSql();
	}

	public String getOrignalSql() {
		return queryResolver.getOriginalSql();
	}

	public List<BindParam> getBindParams() {
		return queryResolver.getBindParams();
	}

	public List<BindStruct> getBindStructs() {
		return queryResolver.getBindStructs();
	}

	public SqlProperties getProperties() {
		return properties;
	}

	public DatabaseAttribute getDatasourceAttribute() {
	    return DatasourceManager.getAttributes( getEnvironmentId() );
    }

	public Object getInputParams() {
		return inputParam;
	}

	public NMap getParams() {
		return sqlParam;
	}

	public int getUniqueKeyQuery() {
		return String.format( "%s::%s::%s", getSqlId(), getEnvironmentId(), getSql() ).hashCode();
	}

	private int getUniqueKeyParameter() {

		NMap map = new NMap();

		for( BindParam bindParam : getBindParams() ) {
			map.put( bindParam.getKey(), bindParam.getValue() );
		}

		return map.getValueHash();

	}

	private Integer baseCacheKey = null;

	public Integer getCacheKey( String methodName ) {

		if( baseCacheKey == null ) {
			baseCacheKey = String.format( "%s::%d::%d", getEnvironmentId(), getUniqueKeyQuery(), getUniqueKeyParameter() ).hashCode();
		}

		return String.format( "%s::%s", baseCacheKey, methodName ).hashCode();

	}

	/**
	 * Set database parameter to original parameters
	 *
	 * <pre>
	 * { "nybatis.database" : "oracle" }
	 * or
	 * { "nybatis.database" : "mysql" }
	 * or
	 * { "nybatis.database" : "sqlite" }
	 * or
	 * { "nybatis.database" : "unknown" }
	 * or ...
	 * </pre>
	 *
	 */
	private void setDatabaseParameter() {
		sqlParam.put( Const.db.PARAMETER_DATABASE, getDatabase() );
	}

	private String getDatabase() {
		return DatasourceManager.getAttributes( properties.getEnvironmentId() ).getDatabase();
	}

}
