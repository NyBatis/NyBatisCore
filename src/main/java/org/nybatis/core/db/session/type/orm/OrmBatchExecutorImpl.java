package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.session.type.sql.BatchExecutorImpl;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.model.NMap;

import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public class OrmBatchExecutorImpl<T> implements OrmBatchExecutor<T> {

    private SqlSessionImpl sqlSession;
    private OrmSessionProperties properties  = new OrmSessionProperties();
    private Integer              bufferSize  = null;

    public OrmBatchExecutorImpl( SqlSessionImpl sqlSession, OrmSessionProperties properties ) {
        this.sqlSession  = sqlSession;
        this.properties  = properties.clone();
    }

    @Override
    public OrmBatchExecutor setBufferSize( int bufferSize ) {
        this.bufferSize = bufferSize;
        return this;
    }

    @Override
    public OrmBatchExecutor removeBufferSize() {
        bufferSize = null;
        return this;
    }

    @Override
    public int insert( List<T> parameterList ) {
        return executeBatch( properties.sqlIdInsert(), parameterList );
    }

    @Override
    public int update( List<T> parameterList ) {
        return executeBatch( properties.sqlIdUpdate(), parameterList );
    }

    @Override
    public int delete( List<T> parameterList ) {
        return executeBatch( properties.sqlIdDelete(), parameterList );
    }

    private int executeBatch( String sqlId, List<?> params ) {

        List<NMap> parameters = properties.getParameters( params );

        return new BatchExecutorImpl( sqlSession ).batchSqlId( sqlId, parameters ).execute( bufferSize );

    }

}
