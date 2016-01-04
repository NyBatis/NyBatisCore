package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.sql.sqlMaker.QueryResolver;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.TypeUtil;

import java.util.ArrayList;
import java.util.HashMap;
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

	private String getDelimeter( Map param ) {
		return DbUtils.getParameterBindedValue( delimeter, param );
	}

	private String getClose( Map param ) {
		return DbUtils.getParameterBindedValue( close, param );
	}

	private String getOpen( Map param ) {
		return DbUtils.getParameterBindedValue( open, param );
	}

	private Map clone( Map param ) {
		Map newMap = new HashMap();
		newMap.putAll( param );
		return newMap;
	}

	@Override
    public String toString( Map param ) throws SqlParseException {

		boolean isSingleParameter = param.containsKey( Const.db.PARAMETER_SINGLE );

		String paramKey = isSingleParameter ? Const.db.PARAMETER_SINGLE : this.paramKey;

		if( ! TypeUtil.isList(param.get(paramKey))  ) return "";

		List loopParams = TypeUtil.toList( param.get(paramKey) );

		if( loopParams.size() == 0 ) return "";

		boolean delimiterOn = ! StringUtil.isEmpty( getDelimeter( param ) );
		boolean indexKeyOn  = ! StringUtil.isEmpty( indexKey );

		Map templateParam = clone( param );

		StringBuilder loopSql = new StringBuilder();

		for( int i = 0, iCnt = loopParams.size() - 1; i <= iCnt; i++ ) {

			Object loopParam = loopParams.get( i );

			String newSql;

			if( TypeUtil.isPrimitive(loopParam) ) {

				Map localParam = clone( templateParam );

				localParam.put( paramKey, loopParam );
				if( indexKeyOn ) localParam.put( indexKey, i );

				newSql = getSqlTemplate( localParam, isSingleParameter );

				String targetKey = String.format( "%s[%d]", paramKey, i );
				newSql = bindLoopParam( newSql, paramKey, targetKey, loopParam, param );

			} else {

				NMap localParam = DbUtils.toNRowParameter( loopParam, paramKey );

				if( indexKeyOn )
					localParam.put( indexKey, i );

				newSql = getSqlTemplate( localParam, isSingleParameter );

				for( Object sourceKey : localParam.keySet() ) {

					String targetKey = String.format( "%s[%d].%s", paramKey, i, ((String)sourceKey).replace(paramKey + ".", "") );
					newSql = bindLoopParam( newSql, (String)sourceKey, targetKey, localParam.get( sourceKey ), param );

				}

			}

			if( indexKeyOn ) {
				String indexKeyTarget = String.format( "%s[%d].%s", paramKey, i, indexKey );
				newSql = bindLoopParam( newSql, indexKey, indexKeyTarget, i, param );
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

	private String bindLoopParam( String sql, String sourceKey, String targetKey, Object value, Map originalParameter ) {

		String newSql = QueryResolver.makeLoopSql( sql, sourceKey, targetKey );

		if( ! sql.equals( newSql ) ) {
			originalParameter.put( targetKey, value );
		}

		return newSql;

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

	public String getSqlTemplate( Map param, boolean isSingleParameter ) throws SqlParseException {

		String sqlTemplate = super.toString( param );

		sqlTemplate = QueryResolver.makeDynamicSql( sqlTemplate, param );

		if( isSingleParameter ) {
			sqlTemplate = sqlTemplate.replaceAll( "#\\{.+?\\}", String.format("#{%s}", Const.db.PARAMETER_SINGLE) );
		}

		return sqlTemplate;

	}

}
