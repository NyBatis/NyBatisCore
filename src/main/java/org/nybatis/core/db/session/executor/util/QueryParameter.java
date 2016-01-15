package org.nybatis.core.db.session.executor.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.JsonPathNotFoundException;
import org.nybatis.core.model.NMap;

import static org.nybatis.core.conf.Const.db.LOOP_PARAM_PREFIX;

/**
 * @author 1002159
 * @since 2016-01-07
 */
public class QueryParameter extends NMap {

    public QueryParameter( Object value ) {
        init( value );
    }

    private void init( Object value ) {

        clear();

        if( value == null ) return;

        if( DbUtils.isPrimitive( value ) ) {

            NMap singleParam = new NMap();
            singleParam.put( Const.db.PARAMETER_SINGLE, value );

            fromBean( singleParam );

        } else {
            fromBean( value );
        }

    }

    public Object get( String key ) {

        try {
            return getByJsonPath( getLoopParamKey( key ) );
        } catch( JsonPathNotFoundException e ) {
            try {
                return getByJsonPath( key );
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
