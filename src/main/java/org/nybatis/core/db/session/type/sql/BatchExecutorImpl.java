package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.session.executor.batch.BatchPreparedStatementExecutor;
import org.nybatis.core.db.session.executor.batch.BatchStatementExecutor;
import org.nybatis.core.db.sql.reader.SqlReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @since 2015-09-13
 */
public class BatchExecutorImpl implements BatchExecutor {

    SqlSessionImpl sqlSession;
    SqlNode        sqlNode;
    List<Object>   parameters;

    public BatchExecutorImpl( SqlSessionImpl sqlSession ) {
        this.sqlSession = sqlSession;
    }

    public BatchExecutorImpl batchSqlId( String id, List<?> parameters ) {

        Assertion.isTrue( SqlRepository.isExist( id ), "There is no sql id({}) in repository.", id );

        this.sqlNode    = SqlRepository.get( id );
        this.parameters = Validator.nvl( parameters, new ArrayList() );

        return this;
    }

    public BatchExecutorImpl batchSql( List<String> sqlList ) {

        if( sqlList == null ) sqlList = new ArrayList<>();

        parameters = new ArrayList<>();

        SqlReader sqlReader = new SqlReader();

        for( String sql : sqlList ) {
            SqlNode sqlNode = sqlReader.read( sql );
            parameters.add( sqlNode );
        }

        return this;
    }

    public BatchExecutorImpl batchSql( String sql, List<?> parameters ) {

        Assertion.isNotEmpty( sql, "Query must not be empty." );

        this.sqlNode    = new SqlReader().read( sql );
        this.parameters = Validator.nvl( parameters, new ArrayList() );

        return this;
    }

    @Override
    public int execute( Integer transactionSize ) {
        return executeBatch( transactionSize );
    }

    @Override
    public int execute() {
        return executeBatch( null );
    }

    private int executeBatch( Integer bufferSize ) {
        try {
            if( sqlNode == null ) {
                return new BatchStatementExecutor( sqlSession.getToken(), sqlSession.getProperties() ).executeSql( parameters, bufferSize );
            } else {
                return new BatchPreparedStatementExecutor( sqlSession.getToken(), sqlSession.getProperties() ).executeSql( sqlNode, parameters, bufferSize );
            }
        } finally {
            sqlSession.initProperties();
            parameters = new ArrayList<>();
        }

    }

}
