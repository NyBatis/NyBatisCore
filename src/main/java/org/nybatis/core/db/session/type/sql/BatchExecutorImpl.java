package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.executor.batch.BatchPreparedStatementExecutor;
import org.nybatis.core.db.session.executor.batch.BatchStatementExecutor;
import org.nybatis.core.db.sql.reader.SqlReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch Executor Implements
 *
 * @author nayasis@gmail.com
 * @since 2015-09-13
 */
public class BatchExecutorImpl implements BatchExecutor {

    private SqlSessionImpl sqlSession;
    private SqlNode        sqlNode;
    private List<Object>   parameters;
    private Integer        transactionSize = null;

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
    public int execute() throws SqlException {

        try {
            if( sqlNode == null ) {
                return new BatchStatementExecutor( sqlSession.getToken(), sqlSession.getProperties() ).executeSql( parameters, transactionSize );
            } else {
                return new BatchPreparedStatementExecutor( sqlSession.getToken(), sqlSession.getProperties() ).executeSql( sqlNode, parameters, transactionSize );
            }
        } finally {
            sqlSession.initProperties();
            parameters = new ArrayList<>();
        }

    }

    @Override
    public BatchExecutor setTransactionSize( Integer size ) {
        transactionSize = size;
        return this;
    }

    @Override
    public String getDatabaseName() {
        DatabaseAttribute attributes = DatasourceManager.getAttributes( getEnvironmentId() );
        return ( attributes == null ) ? "" : StringUtil.nvl( attributes.getDatabase() );
    }

    private String getEnvironmentId() {

        SqlProperties properties = sqlSession.getProperties();

        if( sqlNode == null ) {
            return Validator.nvl( properties.getEnvironmentId(), GlobalSqlParameter.getEnvironmentId(), DatasourceManager.getDefaultEnvironmentId() );
        } else {
            return Validator.nvl( properties.getEnvironmentId(), GlobalSqlParameter.getEnvironmentId(), sqlNode.getEnvironmentId(), DatasourceManager.getDefaultEnvironmentId() );
        }

    }

}
