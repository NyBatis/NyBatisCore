package org.nybatis.core.db.sql.sqlMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;


public class QueryResolver {

    private enum Comment { NONE, BLOCK, LINE }

    private   List<String>     binarySql   = new LinkedList<>();
    private   List<String>     binaryKeys  = new LinkedList<>();
    private   String           originalSql = "";

    private   Map<String, BindStruct> bindStructs = new HashMap<>();
    private   Map<String, BindParam>  bindParams  = new HashMap<>();


    public QueryResolver() {}

    @SuppressWarnings( "rawtypes" )
    public QueryResolver( String sql, Map param ) {
        Assertion.isNotEmpty( sql, "SQL must not be empty." );
    	originalSql = sql;
    	makeSql( makeDynamicSql(sql, param), param );
    }

    public String getSql() {

    	StringBuilder sql = new StringBuilder();

        int index = 0;

    	for( String line : binarySql ) {

            if( line != null ) {
                sql.append( line );
                continue;
            }

            String key = binaryKeys.get( index++ );

            BindParam bindParam = bindParams.get( key );

            if( bindParam.getType() == SqlType.LIST ) {

                List params = (List) bindParam.getValue();

                List marks = new ArrayList<>();
                for( int i = 0; i < params.size(); i++ ) marks.add( "?" );

                sql.append( StringUtil.join( marks, "," ) );

            } else {
                sql.append( '?' );
            }

    	}

    	return sql.toString();


    }

    public String getOriginalSql() {
    	return originalSql;
    }

    public String getDebugSql() {

    	StringBuilder sql = new StringBuilder();

    	int index = 0;

    	for( String line : binarySql ) {

    		if( line == null ) {
                String key = binaryKeys.get( index++ );
    			sql.append( bindStructs.get( key ).toDebugParam( bindParams.get( key ) ) );
    		} else {
    			sql.append( line );
    		}

    	}

    	return sql.toString();
    }

    public List<BindParam> getBindParams() {

        List<BindParam> params = new ArrayList<>();

        for( String key : binaryKeys ) {
            params.add( bindParams.get( key ) );
        }

        return params;

    }

    public List<BindStruct> getBindStructs() {
        List<BindStruct> params = new ArrayList<>();

        for( String key : binaryKeys ) {
            params.add( bindStructs.get( key ) );
        }

        return params;
    }

    @SuppressWarnings( "rawtypes" )
    public static String makeDynamicSql( String query, Map param ) {

        StringBuilder newQuery = new StringBuilder();

        int previousStartIndex = 0;

        while( true ) {

            int startIndex = getParamStartIndex( query, '$', previousStartIndex );

            if( startIndex == -1 ) break;

            int endIndex = getParamEndIndex( query, startIndex + 2 );

            String key = query.substring( startIndex + 2, endIndex ) ;
            int keyPropIndex = key.indexOf( ':' );
            if( keyPropIndex >= 0 ) key = key.substring( 0, keyPropIndex );

            newQuery.append( query.substring( previousStartIndex, startIndex ) );

            Object val = getValue( param, key );

            if( val == null ) {
                newQuery.append( String.format( "${%s}", key ) );
            } else {
                newQuery.append( val );
            }

            previousStartIndex = endIndex + 1;

        }

        newQuery.append( query.substring( previousStartIndex ) );

        return newQuery.toString();

    }

    @SuppressWarnings( "rawtypes" )
    private void makeSql( String sql, Map param ) {

        QuotChecker quotChecker = new QuotChecker();

        int previousStartIndex = 0;

        while( true ) {

            int startIndex = getParamStartIndex( sql, '#', previousStartIndex, quotChecker );

            if( startIndex == -1 ) break;

            int endIndex = getParamEndIndex( sql, startIndex + 2 );

            String key = sql.substring( startIndex + 2, endIndex );

            String prevPhrase = sql.substring( previousStartIndex, startIndex );

            binarySql.add( prevPhrase );

            if( ! hasKey( param, key ) ) {
                bindParams.put( key, null );

            } else {

                BindStruct bindStruct = new BindStruct( key, quotChecker.isOn() );
                BindParam  bindParam  = bindStruct.toBindParam( getValue(param, bindStruct.getKey()) );

                bindStructs.put( key, bindStruct );
                bindParams.put( key, bindParam );

                if( bindParam.getType() == SqlType.LIST ) {

                    int index = 0;

                    for( Object o : (List) bindParam.getValue() ) {

                        String subKey = String.format( "%s_%d", key, index++ );

                        BindStruct bindStructSub = new BindStruct( subKey, bindStruct.getType(), bindStruct.isOut(), quotChecker.isOn() );
                        BindParam  bindParamSub  = bindStruct.toBindParam( o );

                        bindStructs.put( subKey, bindStructSub );
                        bindParams.put( subKey, bindParamSub );

                    }

                }

            }

            BindParam bindParam = bindParams.get( key );

            if( bindParam == null ) {
                binarySql.add( String.format( "#{%s}", key ) );

            } else if( bindParam.getType() == SqlType.LIST ) {

                int size = ( (List) bindParam.getValue() ).size();

                for( int i = 0; i < size; i++ ) {

                    String subKey = String.format( "%s_%d", bindParam.getKey(), i );

                    binaryKeys.add( subKey );

                    if( i != 0 ) binarySql.add( "," );

                    binarySql.add( null );

                }

            } else {

                binaryKeys.add( key );
                binarySql.add( null );

            }

            previousStartIndex = endIndex + 1;

        }

        binarySql.add( sql.substring( previousStartIndex ) );

    }

    private boolean hasKey( Map param, String key ) {

        if( key.contains( ":" ) ) {
            key = key.substring( 0, key.indexOf( ":" ) );
        }

        if( param.containsKey(key) ) return true;
        return param.containsKey( Const.db.PARAMETER_SINGLE );

    }

    private static Object getValue( @SuppressWarnings( "rawtypes" ) Map param, String key ) {

        Object value = param.get( key );

        if( value != null ) {
            Class klass = value.getClass();
            if( klass == StringBuffer.class || klass == StringBuilder.class ) {
                value = value.toString();
            }
        } else if( param.containsKey( Const.db.PARAMETER_SINGLE ) ) {
            value = param.get( Const.db.PARAMETER_SINGLE );
        }

        return value;

    }

    public static String makeLoopSql( String sql, String sourceKey, String targetKey ) {

        StringBuilder sb = new StringBuilder();

        int previousStartIndex = 0;

        while( true ) {

            int startIndex = getParamStartIndex( sql, '#', previousStartIndex );

            if( startIndex == -1 ) break;

            int endIndex = getParamEndIndex( sql, startIndex + 2 );

            String key = sql.substring( startIndex + 2, endIndex );

            String prevPhrase = sql.substring( previousStartIndex, startIndex );

            sb.append( prevPhrase );

            if( key.equals(sourceKey) ) {
                sb.append( "#{" ).append( targetKey ).append( "}" );
            } else {
                sb.append( "#{" ).append( key ).append( "}" );
            }

            previousStartIndex = endIndex + 1;

        }

        sb.append( sql.substring( previousStartIndex ) );

        return sb.toString();

    }

    private static int getParamStartIndex( String sql, char findChar, int startIndex ) {
        return getParamStartIndex( sql, findChar, startIndex, null );
    }

    private static int getParamStartIndex( String sql, char findChar, int startIndex, QuotChecker quotChecker ) {

        Comment status = Comment.NONE;

        for( int i = startIndex, iCnt = sql.length() - 2; i < iCnt; i++ ) {

            char currC = sql.charAt( i );
            char nextC = sql.charAt( i + 1 );

            if( quotChecker != null ) {
                quotChecker.check( currC );
            }

            switch( status ) {

                case NONE :

                    if( currC == findChar && nextC == '{' ) return i;

                    // '/*' 형식의 주석시작지점 검색
                    if( currC == '/' && nextC == '*' ) {
                        status = Comment.BLOCK;
                        i++;

                    // '--' 형식의 주석시작지점 검색
                    } else if( currC == '-' && nextC == '-' ) {
                        status = Comment.LINE;
                        i++;

                    }

                    break;

                case BLOCK :

                    if( currC == '*' && nextC == '/' ) {
                        status = Comment.NONE;
                        i++;
                    }

                    break;

                case LINE :

                    if( currC == '\n' ) {
                        status = Comment.NONE;
                    }

                    break;

            }

        }

        return -1;

    }

    private static int getParamEndIndex( String sql, int startIndex ) {

        for( int i = startIndex, iCnt = sql.length(); i < iCnt; i++ ) {
            if( sql.charAt( i ) == '}' ) return i;
        }

        return sql.length();

    }

}
