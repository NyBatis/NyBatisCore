package org.nybatis.core.cloneNew.interfaces;

import org.nybatis.core.cloneNew.NewCloner;

import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public interface DeepCloner {
    Object clone( Object object, NewCloner cloner, Map valueReference );
}
