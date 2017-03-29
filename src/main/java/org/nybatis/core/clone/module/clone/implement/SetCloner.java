package org.nybatis.core.clone.module.clone.implement;

import org.nybatis.core.clone.NewCloner;
import org.nybatis.core.clone.module.clone.interfaces.DeepCloner;
import org.nybatis.core.util.ClassUtil;

import java.util.Map;
import java.util.Set;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class SetCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner, Map valueReference ) {

        Set source = (Set) object;
        Set target = ClassUtil.createInstance( source.getClass() );

        for( Object val : source ) {
            target.add( cloner.cloneObject( val, valueReference ) );
        }

        return target;

    }
}
