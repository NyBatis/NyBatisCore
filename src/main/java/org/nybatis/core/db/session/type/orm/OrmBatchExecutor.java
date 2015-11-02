package org.nybatis.core.db.session.type.orm;

import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmBatchExecutor<T> {

    OrmBatchExecutor setBufferSize( int bufferSize );

    OrmBatchExecutor removeBufferSize();

    int insert( List<T> parameterList );

    int update( List<T> parameterList );

    int delete( List<T> parameterList );

}
