package org.nybatis.core.db.session.executor.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.JsonPathNotFoundException;
import org.nybatis.core.model.NMap;

/**
 * @author 1002159
 * @since 2016-01-07
 */
public class QueryParameter {

    NMap param = new NMap();

    public QueryParameter( Object value ) {
        init( value );
    }

    public QueryParameter( String key, Object value ) {

        NMap newValue = new NMap();
        newValue.put( key, value );

        init( newValue );

    }

    private void init( Object value ) {

        param.clear();

        if( value == null ) return;

        if( DbUtils.isPrimitive( value ) ) {

            NMap singleParam = new NMap();
            singleParam.put( Const.db.PARAMETER_SINGLE, value );

            param.fromBean( singleParam );

        } else {
            param.fromBean( value );
        }

    }

    public Object getValue( String key ) throws JsonPathNotFoundException {

        if( param.containsKey( key ) ) {
            return param.get( key );
        } else {

            try {
                return JsonPath.read( param, key );
            } catch( PathNotFoundException e ) {
                throw new JsonPathNotFoundException( e.getMessage() );
            } catch( IllegalArgumentException e ) {
                throw new JsonPathNotFoundException( e.getMessage() );
            }

        }

    }


}
