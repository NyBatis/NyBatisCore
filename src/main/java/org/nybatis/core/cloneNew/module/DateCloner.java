package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;

import java.util.Date;
import java.util.Map;

/**
 * Set cloner
 *
 * @author nayasis@onestorecorp.com
 * @since 2017-03-28
 */
public class DateCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner, Map valueReference ) {
        Date source = (Date) object;
        return new Date( source.getTime() );
    }
}
