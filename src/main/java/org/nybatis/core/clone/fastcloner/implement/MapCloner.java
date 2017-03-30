package org.nybatis.core.clone.fastcloner.implement;

import org.nybatis.core.clone.Cloner;
import org.nybatis.core.clone.fastcloner.interfaces.DeepCloner;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.util.ClassUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class MapCloner implements DeepCloner {
    @Override
    public Object clone( Object object, Cloner cloner, Map valueReference ) {

        Map<Object,Object> source = (Map) object;

        Map target;
        try {
            target = ClassUtil.createInstance( source.getClass() );
        } catch( ClassCastingException e ) {
            target = new LinkedHashMap();
        }

        for( Map.Entry e : source.entrySet() ) {

            Object key = cloner.cloneObject( e.getKey(), valueReference );
            Object val = cloner.cloneObject( e.getValue(), valueReference );

            target.put( key, val );
        }

        return target;

    }
}
