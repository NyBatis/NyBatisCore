package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.sql.sqlMaker.QueryResolver;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.exception.unchecked.JsonPathNotFoundException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.nybatis.core.conf.Const.db.LOOP_PARAM_PREFIX;

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

	private String getDelimeter( Map param ) {
		return DbUtils.getParameterBindedValue( delimeter, param );
	}

	private String getClose( Map param ) {
		return DbUtils.getParameterBindedValue( close, param );
	}

	private String getOpen( Map param ) {
		return DbUtils.getParameterBindedValue( open, param );
	}

	private NMap clone( Map param ) {
		NMap newMap = new NMap();
		newMap.putAll( param );
		return newMap;
	}

	@Override
    public String toString( NMap param ) throws SqlParseException {

		Object val = getValue( param );

		if( ! TypeUtil.isList(val)  ) return "";

		List params = TypeUtil.toList( val );

		if( params.size() == 0 ) return "";

		boolean delimiterOn = StringUtil.isNotEmpty( getDelimeter(param) );
		boolean indexKeyOn  = StringUtil.isNotEmpty( indexKey );

		NMap tempParam = clone( param );

		StringBuilder loopSql = new StringBuilder();

		for( int i = 0, iCnt = params.size() - 1; i <= iCnt; i++ ) {

			Object localParam = params.get( i );

			tempParam.put( makeLoopParamKey( paramKey ), localParam );

			if( indexKeyOn )
				tempParam.put( indexKey, i );

			String newSql = getSqlTemplate( tempParam );

			String targetKey = String.format( "%s[%d]", paramKey, i );
			newSql = bindLoopParam( newSql, paramKey, targetKey );

			if( indexKeyOn ) {
				String indexKeyTarget = String.format( "%s[%d].%s", paramKey, i, indexKey );
				String indexNewSql = bindLoopParam( newSql, indexKey, indexKeyTarget );

				if( indexNewSql.length() != newSql.length() ) {
					param.put( indexKeyTarget, i );
				}

			}

			loopSql.append( newSql );

			if( delimiterOn && i != iCnt && ! StringUtil.isBlank(newSql) ) {
				loopSql.append( ' ' ).append( getDelimeter( param ) ).append( ' ' );
			}

		}

		if( StringUtil.isBlank(loopSql) ) {
			return loopSql.toString();
		} else {
			return String.format( "%s %s %s", getOpen( param ), loopSql, getClose( param ) );
		}

	}

	private String bindLoopParam( String sql, String sourceKey, String targetKey ) {
		return sql.replaceAll( String.format( "#\\{%s(\\..+?)?\\}", sourceKey ), String.format( "#{%s$1}", targetKey ) );
	}


	private void toString( StringBuilder buffer, SqlElement node, int depth ) {

		String tab = StringUtil.lpad( "", ' ', depth * 2 );

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

	public String getSqlTemplate( NMap param ) throws SqlParseException {

		String sqlTemplate = super.toString( param );

		sqlTemplate = QueryResolver.makeDynamicSql( sqlTemplate, param );

		if( hasSingleParameter(param) ) {
			sqlTemplate = sqlTemplate.replaceAll( "#\\{.+?(\\[.+?\\])?(\\..+?)?\\}", String.format("#{%s$1$2}", Const.db.PARAMETER_SINGLE) );
		}

		return sqlTemplate;

	}

	private boolean hasSingleParameter( NMap param ) {
		return param.containsKey( Const.db.PARAMETER_SINGLE );
	}



	private Object getValue( NMap param ) {

		Object val = getValue( param, paramKey );

		if( val == null && hasSingleParameter(param) ) {
			String modifiedParamKey = paramKey.replaceFirst( "^.+?(\\..+?)?$", String.format( "%s%1", Const.db.PARAMETER_SINGLE ) );
			val = getValue( param, modifiedParamKey );
		}

		return val;

	}

	private Object getValue( NMap param, String paramKey ) {

		try {
			return param.getByJsonPath( getLoopParamKey( paramKey ) );
		} catch( JsonPathNotFoundException e ) {
			try {
				return param.getByJsonPath( paramKey );
			} catch( JsonPathNotFoundException e1 ) {
				return null;
			}
		}

	}

	private String makeLoopParamKey( String paramKey ) {
		return LOOP_PARAM_PREFIX + paramKey.replaceAll( "\\.", "::" );
	}

	private String getLoopParamKey( String paramKey ) {

		StringBuilder sb = new StringBuilder();

		sb.append( LOOP_PARAM_PREFIX );

		String[] split = paramKey.split( "\\." );

		int iCnt = split.length;

		for( int i = 0; i < iCnt; i++ ) {

			sb.append( split[i] );

			if( i < iCnt - 2 ) {
				sb.append( "::" );
			} else if( i < iCnt - 1 ) {
				sb.append( "." );
			}

		}

		return sb.toString();

	}

}
