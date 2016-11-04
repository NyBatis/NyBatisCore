package org.nybatis.core.model;

import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Validator;

import java.util.List;
import java.util.Map;

/**
 * Json path mapper
 *
 * @author nayasis@gmail.com
 * @since 2016-11-04
 */
public class JsonPathMapper {

    /**
     * change key structure to Map. <br><br>
     *
     * Map can contains POJO and then JsonPath is not working on it.
     *
     * it change only POJO to Map sturcture.
     *
     * @param map map value
     * @return
     */
    public Map toJsonPath( Map map ) {

        if( Validator.isEmpty(map) ) return map;

        Map newMap = new NMap();

        for( Object key : map.keySet() ) {
            Object val = map.get( key );
            newMap.put( key, convertValue(val) );
        }

        return newMap;

    }

    private Object convertValue( Object value ) {

        if( value == null || Types.isPrimitive(value) ) return value;
        if( Types.isString(value) ) return value.toString();
        if( Types.isMap(value) ) return toJsonPath( (Map) value );

        Class klass = value.getClass();

        if( klass == byte[].class || klass == Byte[].class ) return value;

        if( Types.isArrayOrList(klass) ) {
            List list = Types.toList( value );
            for( int i = 0, iCnt = list.size(); i < iCnt; i++ ) {
                list.set( i, convertValue( list.get(i) ) );
            }
            return list;
        }

        return new NMap( value );

    }

}
