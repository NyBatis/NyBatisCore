package org.nybatis.core.db.sql.sqlNode;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.sql.sqlNode.element.RootSqlElement;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;
import org.nybatis.core.xml.node.Node;

import java.util.HashMap;
import java.util.Map;

public class SqlNode {

	private String               sqlId;
	private RootSqlElement structuredSql;
	private SqlProperties        properties;
	private Map<String, SqlNode> keySqls = null;

	private static final Map<String, SqlNode> NULL_KEY_SQLS = new HashMap<>();

	public SqlNode( String sqlId, RootSqlElement structuredSql, String environment, Node inputXmlSql, Map<String,SqlNode> keySqls ) {

		this.sqlId         = sqlId;
		this.structuredSql = structuredSql;
		this.properties    = new SqlProperties( environment, inputXmlSql );
		this.keySqls       = Validator.nvl( keySqls, NULL_KEY_SQLS );

	}

	/**
	 * @return the text
	 */
	public String getText( Map<?,?> param ) {
		return getText( param, false, false );
	}

	/**
	 * @return the text
	 */
    public String getText( Map<?,?> param, boolean isPage, boolean isCount ) {

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

	/**
	 * @return the dbResource
	 */
    public String getEnvironmentId() {
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

	public void setMainId( String mainId ) {
		structuredSql.setMainId( mainId );
	}

}
