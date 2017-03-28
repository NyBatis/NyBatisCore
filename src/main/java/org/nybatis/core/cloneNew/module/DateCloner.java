package org.nybatis.core.cloneNew.module;

import org.nybatis.core.cloneNew.NewCloner;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;

import java.util.Date;

/**
 * Set cloner
 *
 * @author nayasis@onestorecorp.com
 * @since 2017-03-28
 */
public class DateCloner implements DeepCloner {
    @Override
    public Object clone( Object object, NewCloner cloner ) {
        Date source = (Date) object;
        return new Date( source.getTime() );
    }
}
