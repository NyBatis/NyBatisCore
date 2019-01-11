package org.nybatis.core.db.session.type.orm;

import java.util.Collection;
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
     * insert data on add-batch mode.
     *
     * @param parameters    data to insert
     * @return affected counts
     */
    int insert( Collection<?> parameters );

    /**
     * update data on add-batch mode
     * @param parameters    data to update
     * @return  affected counts
     */
    int update( Collection<?> parameters );

    /**
     * delete data on add-batch mode
     *
     * @param parameters    data to delete
     * @return  affected counts
     */
    int delete( Collection<?> parameters );

    /**
     * Get name of database connected with session.
     *
     * @return database name
     */
    String getDatabaseName();

}
