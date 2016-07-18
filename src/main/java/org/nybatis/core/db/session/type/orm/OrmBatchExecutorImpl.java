package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.session.type.sql.BatchExecutorImpl;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.model.NMap;

import java.util.List;

/**
 * OrmBatchExecutor implements
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public class OrmBatchExecutorImpl<T> implements OrmBatchExecutor<T> {

    private SqlSessionImpl       sqlSession;
    private OrmSessionProperties properties      = new OrmSessionProperties();
    private Integer              transactionSize = null;

    public OrmBatchExecutorImpl( SqlSessionImpl sqlSession, OrmSessionProperties properties ) {
        this.sqlSession  = sqlSession;
        this.properties  = properties.clone();
    }

    @Override
    public OrmBatchExecutor<T> setTransactionSize( int size ) {
        this.transactionSize = size;
        return this;
    }

    @Override
    public int insert( List<?> parameters ) {
        return executeBatch( properties.sqlIdInsertPk(), parameters );
    }

    @Override
    public int update( List<?> parameters ) {
        return executeBatch( properties.sqlIdUpdatePk(), parameters );
    }

    @Override
    public int delete( List<?> parameters ) {
        return executeBatch( properties.sqlIdDeletePk(), parameters );
    }

    private int executeBatch( String sqlId, List<?> params ) {

        List<NMap> parameters = properties.getParameters( params );

        return new BatchExecutorImpl( sqlSession ).batchSqlId( sqlId, parameters ).setTransactionSize( transactionSize ).execute();

    }

    @Override
    public String getDatabaseName() {
        return new BatchExecutorImpl( sqlSession ).batchSqlId( properties.sqlIdInsertPk(), null ).getDatabaseName();
    }


}
