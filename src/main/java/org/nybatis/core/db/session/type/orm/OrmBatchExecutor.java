package org.nybatis.core.db.session.type.orm;

import java.util.List;

/**
 * ORM Batch Executor
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmBatchExecutor<T> {

    OrmBatchExecutor setTransactionSize( int size );

    int insert( List<T> parameterList );

    int update( List<T> parameterList );

    int delete( List<T> parameterList );

}
