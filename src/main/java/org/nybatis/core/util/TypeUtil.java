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
 * Type Check Util
 *
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
public class TypeUtil {

    public static boolean isMap( Class klass ) {
        return ( klass != null && ClassUtil.getTopSuperClass( klass ) == AbstractMap.class );
    }

    public static boolean isMap( Object object ) {
        return object != null && isMap( object.getClass() );
    }

    public static boolean isList( Class klass ) {
        if( klass == null ) return false;
        Class<?> superClass = ClassUtil.getTopSuperClass( klass );
        return superClass == AbstractCollection.class || superClass == NList.class;
    }

    public static boolean isList( Object object ) {
        return object != null && isList( object.getClass() );
    }

    public static boolean isArray( Class klass ) {
        if( klass == null ) return false;
        return isList( klass ) || klass.isArray();
    }

    public static boolean isArray( Object object ) {
        return object != null && isArray( object.getClass() );
    }

    public static boolean isPrimitive( Class klass ) {
        return new PrimitiveConverter().isPrimitive( klass );
    }

    public static boolean isPrimitive( Object object ) {
        return object == null || isPrimitive( object.getClass() );
    }

    public static List toList( Object object ) {

        List result = null;

        if( object == null ) {
            result = new ArrayList();

        } else {

            if( object instanceof List ) {
                result = (List) object;
            } else if( object instanceof Set ) {
                result = new ArrayList<>( (Set) object );
            } else if( object instanceof NList ) {
                result = ((NList) object).toList();
            } else if( object.getClass().isArray() ) {
                result = Arrays.asList( object );
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

}
