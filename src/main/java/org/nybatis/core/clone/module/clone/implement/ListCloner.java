package org.nybatis.core.clone.module.clone.implement;

import org.nybatis.core.clone.NewCloner;
import org.nybatis.core.clone.module.clone.interfaces.DeepCloner;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.util.ClassUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * List cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class ListCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner, Map valueReference ) {

        List source = (List) object;

        Class<? extends List> klass = source.getClass();

        List target;

        try {
            target = ClassUtil.createInstance( klass );
        } catch( ClassCastingException e ) {
            target = new ArrayList();
        }

        for( Object val : source ) {
            target.add( cloner.cloneObject( val, valueReference ) );
        }

        return target;

    }
}
