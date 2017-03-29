package org.nybatis.core.clone.module.clone.implement;

import org.nybatis.core.clone.NewCloner;
import org.nybatis.core.clone.module.clone.interfaces.DeepCloner;
import org.nybatis.core.util.ClassUtil;

import java.util.*;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class CalanderCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner, Map valueReference ) {

        Calendar source = (Calendar) object;
        Calendar target = ClassUtil.createInstance( source.getClass() );

        target.setTimeInMillis( source.getTimeInMillis() );
        target.setTimeZone( source.getTimeZone() );

        return source;

    }
}
