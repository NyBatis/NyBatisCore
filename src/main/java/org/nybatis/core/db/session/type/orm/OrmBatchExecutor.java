package org.nybatis.core.db.session.type.orm;

import java.util.List;

/**
 * ORM Batch Executor
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmBatchExecutor<T> {

    /**
     * Set transaction size
     *
     * @param size  size to commit execution
     * @return self instance
     */
    OrmBatchExecutor<T> setTransactionSize( int size );

    /**
     *
     * @param parameters
     * @return
     */
    int insert( List<?> parameters );

    int update( List<?> parameters );

    int delete( List<?> parameters );

}
