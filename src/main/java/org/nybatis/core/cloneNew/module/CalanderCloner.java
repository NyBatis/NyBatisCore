package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;
import org.nybatis.core.util.ClassUtil;

import java.time.LocalDate;
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
