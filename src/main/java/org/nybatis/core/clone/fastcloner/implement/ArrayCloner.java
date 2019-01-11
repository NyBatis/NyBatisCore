package org.nybatis.core.clone.fastcloner.implement;

import org.nybatis.core.clone.Cloner;
import org.nybatis.core.clone.fastcloner.interfaces.DeepCloner;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * Array cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class ArrayCloner implements DeepCloner {
    @Override
    public Object clone( Object object, Cloner cloner, Map valueReference ) {

        Class<?> klass = object.getClass();
        int length = Array.getLength( object );
        Class<?> type = klass.getComponentType();
        Object newArray = Array.newInstance( type, length );

        if( type.isPrimitive() || cloner.getImmutableChecker().isImmutable(type) ) {
            System.arraycopy( object, 0, newArray, 0, length );
        } else {
            for( int i = 0; i < length; i++ ) {
                Object value = cloner.cloneObject( Array.get(object, i), valueReference );
                Array.set( newArray, i, value );
            }
        }

        return newArray;

    }

}
