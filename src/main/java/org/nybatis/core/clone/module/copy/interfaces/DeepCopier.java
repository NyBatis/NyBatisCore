package org.nybatis.core.clone.module.copy.interfaces;

import org.nybatis.core.clone.NewCopier;

/**
 * Copier interface
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public interface DeepCopier {
    void copy( Object object, NewCopier copier, boolean isMerge );
}
