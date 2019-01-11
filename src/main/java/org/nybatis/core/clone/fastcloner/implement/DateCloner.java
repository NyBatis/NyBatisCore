package org.nybatis.core.clone.fastcloner.implement;

import org.nybatis.core.clone.Cloner;
import org.nybatis.core.clone.fastcloner.interfaces.DeepCloner;

import java.util.Date;
import java.util.Map;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class DateCloner implements DeepCloner {
    @Override
    public Object clone( Object object, Cloner cloner, Map valueReference ) {
        Date source = (Date) object;
        return new Date( source.getTime() );
    }
}
