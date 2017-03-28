package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;
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
