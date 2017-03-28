package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;
import org.nybatis.core.util.ClassUtil;

import java.util.Collection;

/**
 * Set cloner
 *
 * @author nayasis@onestorecorp.com
 * @since 2017-03-28
 */
public class CollectionCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner ) {

        Collection source = (Collection) object;
        Collection target = ClassUtil.createInstance( source.getClass() );

        for( Object val : source ) {
            target.add( cloner.cloneObject( val ) );
        }

        return target;

    }
}
