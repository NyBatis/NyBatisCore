package org.nybatis.core.clone.module.clone.interfaces;

import org.nybatis.core.clone.NewCloner;

import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public interface DeepCloner {
    Object clone( Object object, NewCloner cloner, Map valueReference );
}
