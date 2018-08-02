package org.nybatis.core.db.session.executor;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.sqlMaker.BindParam;
import org.nybatis.core.db.sql.sqlMaker.BindStruct;
import org.nybatis.core.db.sql.sqlMaker.QueryResolver;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlBean {

	private static final Logger logger = LoggerFactory.getLogger( Const.db.LOG_SQL );

	private SqlNode        sqlNode        = null;
	private SqlProperties  properties     = null;
	private QueryParameter sqlParam       = null;
	private NMap           inputParam     = new NMap();
	private Set            originalParams = new HashSet<>();

	private QueryResolver queryResolver = null;

	public SqlBean( SqlNode sqlNode ) {
		this( sqlNode, null );
	}

	public SqlBean( SqlNode sqlNode, Object parameter ) {
		this.sqlNode = sqlNode;
		setParameter( parameter );
	}

	/**
	 * Set Parameter<br>
	 *
	 * Do not call this method after build.
	 *
	 * @param parameter parameter to set
	 * @return self instance
	 */
	public SqlBean setParameter( Object parameter ) {
		if( parameter != null ) {
			inputParam.clear();
			originalParams.clear();
			if( DbUtils.isPrimitive(parameter) ) {
				inputParam.put( Const.db.PARAMETER_SINGLE, parameter );
			} else {
				Map<String, Object> newParam = DbUtils.jsonConverter.toMapFrom( parameter );
				inputParam.bind( newParam );
				originalParams.add( parameter );
			}
		}
		return this;
	}

	public SqlBean addParameter( Object parameter ) {
		if( parameter != null ) {
			if( DbUtils.isPrimitive(parameter) ) {
				inputParam.put( Const.db.PARAMETER_SINGLE, parameter );
			} else {
				Map<String, Object> newParam = DbUtils.jsonConverter.toMapFrom( parameter );
				Reflector.merge( newParam, inputParam, false );
				originalParams.add( parameter );
			}
		}
		return this;
	}

	public SqlBean addParameter( String key, Object parameter ) {
		inputParam.put( key, parameter );
		if( ! DbUtils.isPrimitive(parameter) ) {
			originalParams.add( parameter );
		}
		return this;
	}

	public SqlBean init( SqlProperties properties ) {

		this.properties = properties.merge( sqlNode.getProperties() );
		this.sqlParam   = new QueryParameter( inputParam ).addGlobalParameters();

		if( properties.isPageSql() ) {

			Integer pageStart = this.properties.getPageSqlStart();
			Integer pageEnd   = this.properties.getPageSqlEnd();

			if( pageStart != null && pageEnd != null ) {

				int tmp   = pageStart;
				pageStart = Math.min( pageStart, pageEnd );
				pageEnd   = Math.max( tmp,       pageEnd );

				sqlParam.put( DatabaseAttribute.PAGE_PARAM_START, pageStart );
				sqlParam.put( DatabaseAttribute.PAGE_PARAM_END,   pageEnd );
				sqlParam.put( DatabaseAttribute.PAGE_PARAM_OFFSET, Math.max( pageStart - 1, 0) );
				sqlParam.put( DatabaseAttribute.PAGE_PARAM_COUNT,  Math.abs( pageEnd - pageStart ) + 1 );

			}

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

		printParameterLog();

		String query = sqlNode.getText( sqlParam, properties.isPageSql(), properties.isCountSql() );

		try {

			queryResolver = new QueryResolver( query, sqlParam );
			return this;

		} catch( StringIndexOutOfBoundsException | SqlParseException e ) {
			throw new SqlParseException( e, "{} Error on parameter binding because of invalid sql parameter syntax.\n>> Error Message :\n{}\n>> Error SQL :\n{}", toString(), e.getMessage(), query );

		} catch( IllegalArgumentException | SqlConfigurationException e ) {
			throw new SqlConfigurationException( e, "{} {}", toString(), e.getMessage() );
		}

	}

	private void printParameterLog() {
		if( ! logger.isTraceEnabled() ) return;
		String json = sqlParam == null ? "{ }" : sqlParam.toJson( true );
		logger.trace( ">> {} parameter\n{}", toString(), json );
	}

	private void setEnvironmentId( String environmentId ) {
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
		return Validator.nvl( properties.getEnvironmentId(), GlobalSqlParameter.getEnvironmentId(), sqlNode.getEnvironmentId(), DatasourceManager.getDefaultEnvironmentId() );
	}

	public String getSqlId() {
		return sqlNode == null ? null : sqlNode.getSqlId();
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

	public NMap getInputParams() {
		return inputParam;
	}

	public SqlBean mergeSelectKeys( NMap result ) {
		if( result != null && ! result.isEmpty() ) {
			Reflector.merge( result, inputParam );
			for( Object originalParam : originalParams ) {
				Reflector.merge( result, originalParam );
			}
		}
		return this;
	}

	public NMap getParams() {
		return sqlParam;
	}

	public int getUniqueKeyQuery() {
		return String.format( "%s::%s::%s", getSqlId(), getEnvironmentId(), getSql() ).hashCode();
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
	 * @see (@link org.nybatis.core.db.datasource.driver.DatabaseName}
	 */
	private void setDatabaseParameter() {
		sqlParam.put( Const.db.PARAMETER_DATABASE, getDatabase() );
	}

	private String getDatabase() {
		return DatasourceManager.getAttributes( properties.getRepresentativeEnvironmentId() ).getDatabase();
	}

}
