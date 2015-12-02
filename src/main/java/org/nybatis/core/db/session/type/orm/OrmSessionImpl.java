package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.cache.CacheManager;
import org.nybatis.core.db.session.type.sql.SessionExecutor;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.sql.reader.DbTableReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.validation.Assertion;

/**
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

        properties.setEnvironmentId( sqlSession.getProperties().getEnvironmentId() );
        properties.setTableName( tableName );

        createOrmSql();

    }

    private void createOrmSql() {
        new DbTableReader().read( properties.getEnvironmentId(), properties.getTableName() );
    }

    @Override
    public int insert( Object entity ) {

        Assertion.isNotNull( entity, new SqlConfigurationException( "OrmSession input parameter is null." ) );

        properties.setEntityParameter( entity );
        checkPkNotNull();

        try {
            return getSessionExecutor( properties.sqlIdInsertPk() ).execute();
        } finally {
            refreshCache();
            properties.clear();
        }

    }

    @Override
    public int merge( Object parameter ) {

        T param = new Reflector().toBeanFromBean( parameter, domainClass );

        if( selectMap(param).size() == 0 ) {
            return insert( parameter );
        } else {
            return update( parameter );
        }

    }

    @Override
    public int update( Object entity ) {

        properties.setEntityParameter( entity );

        try {
            return getSessionExecutor( properties.sqlIdUpdatePk() ).execute();
        } finally {
            refreshCache();
            properties.clear();
        }

    }

    @Override
    public int delete( Object parameter ) {

        properties.setEntityParameter( parameter );

        String sqlId = isPkSql( parameter ) ? properties.sqlIdDeletePk() : properties.sqlIdDelete();

        int cnt = getSessionExecutor( sqlId ).execute();

        if( isPkSql( parameter ) ) {
            refreshCache();
        }

        properties.clear();

        return cnt;

    }

    private boolean isPkSql( Object parameter ) {
        return parameter != null && parameter.getClass() == domainClass;
    }

    @Override
    public T select( Object parameter ) {

        properties.setEntityParameter( parameter );

        String sqlId = isPkSql( parameter ) ? properties.sqlIdSelectPk() : properties.sqlIdSelect();

        try {
            return getSessionExecutor( sqlId ).select( domainClass );
        } finally {
            properties.clear();
        }

    }

    @Override
    public NMap selectMap( Object parameter ) {

        properties.setEntityParameter( parameter );

        String sqlId = isPkSql( parameter ) ? properties.sqlIdSelectPk() : properties.sqlIdSelect();

        try {
            return getSessionExecutor( sqlId ).select();
        } finally {
            properties.clear();
        }

    }

    private SessionExecutor getSessionExecutor( String sqlId ) {
        return sqlSession.sqlId( sqlId, properties.getParameter() );
    }

    private void refreshCache() {
        if( SqlRepository.getProperties( properties.sqlIdSelectPk() ).isCacheEnable() ) {
            sqlSession.sqlId( properties.sqlIdSelectPk(), properties.getParameter() ).clearCache().select( domainClass );
        }
    }

    @Override
    public OrmListExecutor<T> list() {
        return new OrmListExecutorImpl<>( domainClass, sqlSession, properties.newInstance() );
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
    public OrmSession<T> disableCache() {
        CacheManager.disableCache( properties.sqlIdSelectPk() );
        return this;
    }

    @Override
    public OrmSession<T> enableCache( String cacheId ) {
        return enableCache( cacheId, null );
    }

    @Override
    public OrmSession<T> enableCache( String cacheId, Integer flushSeconds ) {
        CacheManager.enableCache( properties.sqlIdSelectPk(), cacheId, flushSeconds );
        return this;
    }

    @Override
    public OrmSession<T> clearCache() {
        sqlSession.getProperties().isCacheClear( true );
        return this;
    }

    @Override
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    private void checkPkNotNull() {
        Assertion.isTrue( properties.isPkNotNull(), new SqlConfigurationException( "PK has null value.({})", properties.getPkValues() ) );
    }

}
