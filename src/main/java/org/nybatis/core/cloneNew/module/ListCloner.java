package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.util.ClassUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * List cloner
 *
 * @author nayasis@onestorecorp.com
 * @since 2017-03-28
 */
public class ListCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner, Map valueReference ) {

        List source = (List) object;

        Class<? extends List> klass = source.getClass();

        List target = null;
        // it is very special ArrayList that does not reveal to public
        if( "java.util.Arrays$ArrayList".equals(klass.getName()) ) {
            target = new ArrayList();
        } else {
            target = ClassUtil.createInstance( klass );
        }

//        try {
//            target = ClassUtil.createInstance( source.getClass() );
//        } catch( ClassCastingException e ) {
//            target = new ArrayList();
//        }

        for( Object val : source ) {
            target.add( cloner.cloneObject( val, valueReference ) );
        }

        return target;

    }
}
