package org.nybatis.core.clone.fastcloner.implement;

import org.nybatis.core.clone.Cloner;
import org.nybatis.core.clone.fastcloner.interfaces.DeepCloner;
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
    public Object clone( Object object, Cloner cloner, Map valueReference ) {

        Calendar source = (Calendar) object;
        Calendar target = ClassUtil.createInstance( source.getClass() );

        target.setTimeInMillis( source.getTimeInMillis() );
        target.setTimeZone( source.getTimeZone() );

        return source;

    }
}
