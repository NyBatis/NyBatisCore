package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.sqlMaker.QueryResolver;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForEachSqlElement extends SqlElement {

	private List<SqlElement> children = new ArrayList<>();

	private String paramKey;
	private String open;
	private String close;
	private String delimeter;
	private String indexKey;

	public ForEachSqlElement( String key, String open, String close, String delimeter, String indexKey ) {

		this.paramKey    = StringUtil.trim( key ).replaceFirst( "^#\\{", "" ).replaceFirst( "\\}$", "" );
		this.open        = StringUtil.trim( open );
		this.close       = StringUtil.trim( close );
		this.delimeter   = StringUtil.trim( delimeter );
		this.indexKey    = StringUtil.trim( indexKey ).replaceFirst( "^#\\{", "" ).replaceFirst( "\\}$", "" );

	}

	@Override
    public String toString( QueryParameter inputParam ) throws SqlParseException {

		boolean delimiterOn    = StringUtil.isNotEmpty( getDelimeter( inputParam ) );
		boolean indexKeyOn     = StringUtil.isNotEmpty( indexKey );
		boolean hasSingleParam = hasSingleParameter( inputParam );

		List params = getParams( inputParam, hasSingleParam );

		if( Validator.isEmpty(params) ) return "";

		StringBuilder sql = new StringBuilder();

		for( int i = 0, iCnt = params.size() - 1; i <= iCnt; i++ ) {

			QueryParameter param = clone( inputParam ).setForEachInnerParam( paramKey, params.get(i) );

			if( indexKeyOn ) {
				param.put( indexKey, i );
			}

			String innerSql = getInnerSql( param );

			String targetKey = String.format( "%s[%d]", paramKey, i );
			innerSql = convertKeyToJsonPath( innerSql, paramKey, targetKey );
			innerSql = bindSingleParamKey( innerSql, hasSingleParam );

			if( indexKeyOn ) {
				innerSql = setIndexKey( innerSql, i, inputParam );
			}

			sql.append( innerSql );

			if( delimiterOn && i != iCnt && ! StringUtil.isBlank(innerSql) ) {
				sql.append( ' ' ).append( getDelimeter( inputParam ) ).append( ' ' );
			}

		}

		if( StringUtil.isBlank(sql) ) {
			return sql.toString();
		} else {
			return String.format( "%s %s %s", getOpen( inputParam ), sql, getClose( inputParam ) );
		}

	}

	private String setIndexKey( String sql, int index, QueryParameter inputParam ) {

		if( StringUtil.isEmpty( indexKey ) ) return sql;

		String targetKey = String.format( "%s[%d].%s", paramKey, index, indexKey );

		int beforeSize = sql.length();

		sql = convertKeyToJsonPath( sql, indexKey, targetKey );

		int afterSize = sql.length();

		if( beforeSize != afterSize ) {
			inputParam.put( targetKey, index );
		}

		return sql;

	}

	private String convertKeyToJsonPath( String sql, String sourceKey, String targetKey ) {
		return sql.replaceAll( String.format( "#\\{%s(\\..+?)?\\}", sourceKey ), String.format( "#{%s$1}", targetKey ) );
	}


	private void toString( StringBuilder buffer, SqlElement node, int depth ) {

		String tab = StringUtil.lpad( "", depth * 2, ' ' );

		if( node instanceof IfSqlElement ) {

			IfSqlElement ifNode = (IfSqlElement) node;

			for( SqlElement child : ifNode.children() ) {
				toString( buffer, child, depth + 1 );
			}

		} else {
			buffer.append( String.format( "%s%s", tab, node.toString() ) );
		}

	}

	public String toString() {

		StringBuilder sb = new StringBuilder();

		for( SqlElement node : children ) {
			toString( sb, node, 0 );
		}

		return sb.toString();

	}

	private String getDelimeter( Map param ) {
		return StringUtil.bindParam( delimeter, param );
	}

	private String getClose( Map param ) {
		return StringUtil.bindParam( close, param );
	}

	private String getOpen( Map param ) {
		return StringUtil.bindParam( open, param );
	}

	private QueryParameter clone( Map param ) {
		QueryParameter newMap = new QueryParameter();
		newMap.putAll( param );
		return newMap;
	}

	private String getInnerSql( QueryParameter param ) throws SqlParseException {

		String sqlTemplate = super.toString( param );

		return QueryResolver.makeDynamicSql( sqlTemplate, param );

	}

	private boolean hasSingleParameter( NMap param ) {
		return param.containsKey( Const.db.PARAMETER_SINGLE );
	}

	private List getParams( QueryParameter inputParam, boolean hasSingleParam ) {
		Object value = getValue( inputParam, hasSingleParam );
		return Types.toList( value );
	}

	private Object getValue( QueryParameter param, boolean hasSingleParam ) {

		Object val = param.get( paramKey );

		if( val == null && hasSingleParam ) {
			String modifiedParamKey = paramKey.replaceFirst( "^.+?(\\..+?)?$", String.format( "%s$1", Const.db.PARAMETER_SINGLE ) );
			val = param.get( modifiedParamKey );
		}

		return val;

	}

	private String bindSingleParamKey( String sql, boolean hasSingleParam ) {

		if( hasSingleParam ) {
			return sql.replaceAll( "#\\{.+?(\\[.+?\\])?(\\..+?)?\\}", String.format( "#{%s$1$2}", Const.db.PARAMETER_SINGLE) );
		} else {
			return sql;
		}

	}

}
