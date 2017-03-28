package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;
import org.nybatis.core.util.ClassUtil;

import java.util.Map;

/**
 * Set cloner
 *
 * @author nayasis@onestorecorp.com
 * @since 2017-03-28
 */
public class MapCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner ) {

        Map<Object,Object> source = (Map) object;
        Map target = ClassUtil.createInstance( source.getClass() );

        for( Map.Entry e : source.entrySet() ) {

            Object key = cloner.cloneObject( e.getKey() );
            Object val = cloner.cloneObject( e.getValue() );

            target.put( key, val );
        }

        return target;

    }
}
