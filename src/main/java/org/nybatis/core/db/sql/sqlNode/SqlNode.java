package org.nybatis.core.db.sql.sqlNode;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.sqlNode.element.RootSqlElement;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;
import org.nybatis.core.xml.node.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlNode {

	private String               sqlId;
	private RootSqlElement       structuredSql;
	private SqlProperties        properties;
	private Map<String, SqlNode> keySqls = null;
	private Set<String>          environmentIds = new LinkedHashSet<>();

	private static final Map<String, SqlNode> NULL_KEY_SQLS = new HashMap<>();

	public SqlNode( String sqlId, RootSqlElement structuredSql, String environment, Node inputXmlSql, Map<String,SqlNode> keySqls ) {

		this.sqlId         = sqlId;
		this.structuredSql = structuredSql;
		this.properties    = new SqlProperties( environment, inputXmlSql );
		this.keySqls       = Validator.nvl( keySqls, NULL_KEY_SQLS );

		addEnvironmentId( environment );

	}

	/**
	 * @return sql string
	 */
	public String getText( QueryParameter param ) {
		return getText( param, false, false );
	}

	/**
	 * @return sql string
	 */
    public String getText( QueryParameter param, boolean isPage, boolean isCount ) {

    	try {

    		String sql = structuredSql.toString( param );

			if( DatasourceManager.isExist( getEnvironmentId() ) ) {

				DatabaseAttribute envAttr = DatasourceManager.getAttributes( getEnvironmentId() );

				if( isPage ) {
					sql = String.format( "%s%s%s", envAttr.getPageSqlPre(), sql, envAttr.getPageSqlPost() );

				} else if( isCount ) {
					sql = String.format( "%s%s%s", envAttr.getCountSqlPre(), sql, envAttr.getCountSqlPost() );

				}

			}

			return StringUtil.compressEnter( sql );

    	} catch( SqlConfigurationException e ) {
    		throw new SqlConfigurationException( e, "Sql(id:{}) is not valid. {}\n{}", sqlId, e.getMessage(), structuredSql.toString() );
    	} catch( SqlParseException e ) {
    		throw new SqlParseException( e, "Sql(id:{}) is not valid. {}\n{}", sqlId, e.getMessage(), structuredSql.toString() );
    	}

    }

    public String getSqlId() {
    	return sqlId;
    }

	public void setSqlId( String sqlId ) {
		this.sqlId = sqlId;
	}

	public void addEnvironmentId( String id ) {
		if( StringUtil.isNotEmpty(id) ) {
			environmentIds.add( id );
		}
	}

	/**
	 * Get environment id.
	 *
	 * if global default environment id is setted by {@link GlobalSqlParameter#setDefaultEnvironmentId( id )}
	 * and it is one of multiple environments,
	 * global default environment is returned.
	 *
	 * @return environment id
	 */
    public String getEnvironmentId() {

		String globalDefaultEnvironmentId = GlobalSqlParameter.getDefaultEnvironmentId();

		if( environmentIds.isEmpty() ) return Validator.nvl( globalDefaultEnvironmentId, DatasourceManager.getDefaultEnvironmentId() );

		if( environmentIds.contains( globalDefaultEnvironmentId ) ) return globalDefaultEnvironmentId;

		return properties.getEnvironmentId();

    }

    public SqlProperties getProperties() {
    	return properties;
    }

	public Map<String, SqlNode> getKeySqls() {
		return keySqls;
	}

	public String toString() {
    	return String.format( "id  : [%s], %s\nSql : \n%s",
				sqlId,
				properties,
				structuredSql.toString()
		);
    }

	public String getSqlSkeleton() {
		return structuredSql.toString();
	}

	public void setMainId( String mainId ) {
		structuredSql.setMainId( mainId );
	}

	public boolean containsEnvironmentId( String environmentId ) {
		return environmentIds.contains( environmentId );
	}

}
