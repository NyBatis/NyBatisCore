package org.nybatis.core.clone.fastcloner.interfaces;

import org.nybatis.core.clone.Cloner;

import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public interface DeepCloner {
    Object clone( Object object, Cloner cloner, Map valueReference );
}
