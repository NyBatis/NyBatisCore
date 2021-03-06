package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.session.type.sql.SessionExecutor;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.sql.orm.sqlmaker.OrmSqlMaker;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.validation.Assertion;

/**
 * Orm Session Implement
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public class OrmSessionImpl<T> implements OrmSession<T> {

    private SqlSessionImpl       sqlSession;
    private OrmSessionProperties properties = new OrmSessionProperties();
    private Class<T>             domainClass;

    public OrmSessionImpl( Class<T> domainClass, SqlSessionImpl sqlSession, String tableName ) {
        this.domainClass = domainClass;
        init( sqlSession, tableName );
    }

    public void init( SqlSessionImpl sqlSession, String tableName ) {
        Assertion.isNotNull( tableName, new SqlConfigurationException( "TableName is null." ) );
        this.sqlSession = sqlSession;
        properties.setEnvironmentId( sqlSession.getProperties().getRepresentativeEnvironmentId() );
        properties.setTableName( tableName );
        createOrmSql();
    }

    public OrmSession<T> clone() {
        OrmSessionImpl<T> ormSession = new OrmSessionImpl<>( domainClass, sqlSession, properties.getTableName() );
        ormSession.properties = properties.clone();
        return ormSession;
    }

    @Override
    public boolean isDatabase( DatabaseName... dbName ) {
        return sqlSession.isDatabase( dbName );
    }

    @Override
    public boolean isNotDatabase( DatabaseName... dbName ) {
        return ! isDatabase( dbName );
    }

    private void createOrmSql() {
        new OrmSqlMaker().readTable( properties.getEnvironmentId(), properties.getTableName(), false );
    }

    @Override
    public int insert( Object entity ) {
        Assertion.isNotNull( entity, new SqlConfigurationException( "OrmSession input parameter is null." ) );
        properties.setEntityParameter( entity );
        checkPkNotNull();
        try {
            return getSessionExecutor( properties.sqlIdInsertPk() ).execute();
        } finally {
            properties.clear();
        }
    }

    @Override
    public int merge( Object parameter ) {
        boolean previousPkAllowance = properties.allowNonPkParameter();
        properties.allowNonPkParameter( false );
        try {
            int updateCount = update( parameter );
            if( updateCount == 0 ) {
                return insert( parameter );
            }
            return updateCount;
        } finally {
            properties.allowNonPkParameter( previousPkAllowance );
        }
    }

    @Override
    public int update( Object entity ) {
        properties.setEntityParameter( entity );
        checkPkNotNull();
        return update();
    }

    @Override
    public int updateAll( Object entity ) {
        properties.allowNonPkParameter( true );
        return update( entity );
    }

    @Override
    public int update() {
        try {
            return getSessionExecutor( properties.sqlIdUpdatePk() ).execute();
        } finally {
            properties.clear();
        }
    }

    @Override
    public int updateAll() {
        properties.allowNonPkParameter( true );
        return update();
    }

    @Override
    public int delete( Object parameter ) {
        properties.setEntityParameter( parameter );
        checkPkNotNull();
        return delete();
    }

    @Override
    public int deleteAll( Object entity ) {
        properties.allowNonPkParameter( true );
        return delete( entity );
    }

    @Override
    public int delete() {
        String sqlId = isPkSql() ? properties.sqlIdDeletePk() : properties.sqlIdDelete();
        int cnt = getSessionExecutor( sqlId ).execute();
        properties.clear();
        return cnt;
    }

    @Override
    public int deleteAll() {
        properties.allowNonPkParameter( true );
        return delete();
    }

    private boolean isPkSql() {
        return ! properties.allowNonPkParameter();
    }

    @Override
    public T select( Object parameter ) {
        properties.setEntityParameter( parameter );
        checkPkNotNull();
        String sqlId = isPkSql() ? properties.sqlIdSelectPk() : properties.sqlIdSelect();
        try {
            return getSessionExecutor( sqlId ).select( domainClass );
        } finally {
            properties.clear();
        }
    }

    @Override
    public NMap selectMap( Object parameter ) {
        properties.setEntityParameter( parameter );
        String sqlId = isPkSql() ? properties.sqlIdSelectPk() : properties.sqlIdSelect();
        try {
            return getSessionExecutor( sqlId ).select();
        } finally {
            properties.clear();
        }
    }

    private SessionExecutor getSessionExecutor( String sqlId ) {
        return sqlSession.sqlId( sqlId, properties.getParameter() );
    }

    @Override
    public OrmListExecutor<T> list() {
        return new OrmListExecutorImpl<>( domainClass, sqlSession, properties.newInstance().allowNonPkParameter(true) );
    }

    @Override
    public OrmTableHandler<T> table() {
        return new OrmTableHandlerImpl<>( sqlSession, properties.clone(), domainClass );
    }

    @Override
    public OrmBatchExecutor<T> batch() {
        return new OrmBatchExecutorImpl<>( sqlSession, properties.newInstance() );
    }

    @Override
    public OrmSession<T> where( String sqlExpression ) {
        return where( sqlExpression, null );
    }

    @Override
    public OrmSession<T> where( String sqlExpression, Object parameter ) {
        properties.allowNonPkParameter( true );
        properties.addWhere( sqlExpression, parameter );
        return this;
    }

    @Override
    public OrmSession<T> commit() {
        sqlSession.commit();
        return this;
    }

    @Override
    public OrmSession<T> rollback() {
        sqlSession.rollback();
        return this;
    }

    @Override
    public OrmSession<T> beginTransaction() {
        sqlSession.beginTransaction();
        return this;
    }

    @Override
    public OrmSession<T> endTransaction() {
        sqlSession.endTransaction();
        return this;
    }

    @Override
    public boolean isTransactionBegun() {
        return sqlSession.isTransactionBegun();
    }

    @Override
    public OrmSession<T> setEnvironmentId( String id ) {
        sqlSession.setEnvironmentId( id );
        properties.setEnvironmentId( id );
        createOrmSql();
        return this;
    }

    @Override
    public String getEnvironmentId() {
        return sqlSession.getEnvironmentId();
    }

    @Override
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    @Override
    public OrmSession<T> allowNonPkParameter( boolean enable ) {
        properties.allowNonPkParameter( enable );
        return this;
    }

    private void checkPkNotNull() {
        if( properties.allowNonPkParameter() ) return;
        Assertion.isTrue( properties.isPkNotNull(), new SqlConfigurationException( "PK has null value.({})", properties.getPkValues() ) );
    }

    @Override
    public String getDatabaseName() {
        return getSessionExecutor( properties.sqlIdSelect() ).getDatabaseName();
    }

}
