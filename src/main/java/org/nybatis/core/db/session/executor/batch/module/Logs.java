package org.nybatis.core.db.session.executor.batch.module;

import org.nybatis.core.db.session.executor.SqlBean;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-08
 */
public interface Logs {

    void   set( Object key, SqlBean sqlBean );
    int    getParamSize( Object key );
    void   clear();
    String getLog( Object key );

}
