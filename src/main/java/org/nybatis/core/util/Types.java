package org.nybatis.core.util;

import org.nybatis.core.model.NList;
import org.nybatis.core.model.PrimitiveConverter;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Type Check Utility
 *
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
public class Types {

    public static boolean isMap( Class klass ) {
        return ( klass != null && ClassUtil.getTopSuperClass( klass ) == AbstractMap.class );
    }

    public static boolean isNotMap( Class klass ) {
        return ! isNotMap( klass );
    }

    public static boolean isMap( Object object ) {
        return object != null && isMap( object.getClass() );
    }

    public static boolean isNotMap( Object object ) {
        return ! isNotMap( object );
    }

    public static boolean isList( Class klass ) {
        if( klass == null ) return false;
        Class<?> superClass = ClassUtil.getTopSuperClass( klass );
        return superClass == AbstractCollection.class || superClass == NList.class;
    }

    public static boolean isNotList( Class klass ) {
        return ! isNotList( klass );
    }

    public static boolean isList( Object object ) {
        return object != null && isList( object.getClass() );
    }

    public static boolean isNotList( Object object ) {
        return ! isNotList( object );
    }

    public static boolean isArray( Class klass ) {
        if( klass == null ) return false;
        return klass.isArray();
    }

    public static boolean isArray( Object object ) {
        return object != null && isArray( object.getClass() );
    }

    public static boolean isNotArray( Class klass ) {
        return ! isArray( klass );
    }

    public static boolean isNotArray( Object object ) {
        return ! isArray( object );
    }

    public static boolean isArrayOrList( Class klass ) {
        if( klass == null ) return false;
        return isList( klass ) || klass.isArray();
    }

    public static boolean isNotArrayOrList( Class klass ) {
        return ! isArrayOrList( klass );
    }

    public static boolean isArrayOrList( Object object ) {
        return object != null && isArrayOrList( object.getClass() );
    }

    public static boolean isNotArrayOrList( Object object ) {
        return ! isArrayOrList( object );
    }

    public static boolean isPrimitive( Class klass ) {
        PrimitiveConverter converter = new PrimitiveConverter();
        return converter.isPrimitive( klass ) || converter.isWrapper( klass );
    }

    public static boolean isBoolean( Object object ) {
        return object != null && isBoolean( object.getClass() );
    }

    public static boolean isNotBoolean( Object object ) {
        return ! isBoolean( object );
    }

    public static boolean isBoolean( Class klass ) {
        return klass == Boolean.class || klass == boolean.class;
    }

    public static boolean isNotBoolean( Class klass ) {
        return ! isBoolean( klass );
    }

    public static boolean isNotPrimitive( Class klass ) {
        return ! isPrimitive( klass );
    }

    public static boolean isPrimitive( Object object ) {
        return object == null || isPrimitive( object.getClass() );
    }

    public static boolean isNotPrimitive( Object object ) {
        return ! isPrimitive( object );
    }

    public static List toList( Object object ) {

        List result = null;

        if( object == null ) {
            result = new ArrayList();

        } else {

            if( object instanceof List ) {
                result = new ArrayList<>( (List) object );
            } else if( object instanceof Set ) {
                result = new ArrayList<>( (Set) object );
            } else if( object instanceof NList ) {
                result = ((NList) object).toList();
            } else if( object.getClass().isArray() ) {
                result = Arrays.asList( (Object[]) object );
            }

        }

        return result;

    }

    public static boolean isMapIncludedArray( Object object ) {

        List list = toList( object );

        if( object == null ) return false;

        for( Object e : list ) {
            if( isMap(e) ) return true;
        }

        return false;

    }

    public static boolean isString( Object value ) {
        return value != null && ( value instanceof String || value instanceof StringBuffer || value instanceof  StringBuilder );
    }

    public static boolean isNotString( Object value ) {
        return ! isString( value );
    }

}
