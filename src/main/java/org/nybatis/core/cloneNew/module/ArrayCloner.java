package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;

import java.lang.reflect.Array;

/**
 * Array cloner
 *
 * @author nayasis@onestorecorp.com
 * @since 2017-03-28
 */
public class ArrayCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner ) {

        Class<?> klass = object.getClass();
        int length = Array.getLength( object );
        Class<?> type = klass.getComponentType();
        Object newArray = Array.newInstance( type, length );

        if( type.isPrimitive() || cloner.getImmutableChecker().isImmutable(type) ) {
            System.arraycopy( object, 0, newArray, 0, length );
        } else {
            for( int i = 0; i < length; i++ ) {
                Object value = cloner.cloneObject( Array.get(object, i) );
                Array.set( newArray, i, value );
            }
        }

        return newArray;

    }

}
