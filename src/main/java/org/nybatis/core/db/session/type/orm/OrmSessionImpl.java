package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.sql.reader.DbTableReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.validation.Assertion;

import java.util.Map;

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
    public int insert( T entity ) {
        checkNotNull( entity );
        properties.setEntityParameter( entity );
        return insert();
    }

    @Override
    public int insert( Map parameter ) {
        properties.setEntityParameter( parameter );
        return insert();
    }

    private int insert() {
        checkPkNotNull();
        int cnt = sqlSession.sqlId( properties.sqlIdInsert(), properties.getParameter() ).execute();
        refreshCache();
        properties.init();
        return cnt;
    }

    @Override
    public int merge( T entity ) {
        if( selectMap( entity ).size() == 0 ) {
            return insert( entity );
        } else {
            return update( entity );
        }
    }

    @Override
    public int merge( Map parameter ) {
        if( selectMap( parameter ).size() == 0 ) {
            return insert( parameter );
        } else {
            return update( parameter );
        }
    }

    @Override
    public int update( T entity ) {
        checkNotNull( entity );
        properties.setEntityParameter( entity );
        int cnt = update();
        properties.init();
        return cnt;
    }

    @Override
    public int update( Map parameter ) {
        properties.setEntityParameter( parameter );
        return update();
    }

    private int update() {
        int cnt = sqlSession.sqlId( properties.sqlIdUpdate(), properties.getParameter() ).execute();
        refreshCache();
        return cnt;
    }

    private void refreshCache() {
        if( SqlRepository.getProperties( properties.sqlIdSelectSingle() ).isCacheEnable() ) {
            sqlSession.sqlId( properties.sqlIdSelectSingle(), properties.getParameter() ).disableCache().select( domainClass );
        }
    }

    @Override
    public int delete( T entity ) {
        properties.setEntityParameter( entity );
        return delete();
    }

    @Override
    public int delete( Map parameter ) {
        properties.setEntityParameter( parameter );
        return delete();
    }

    private int delete() {

        boolean pkNotNull = properties.isPkNotNull();

        String sqlId = pkNotNull ? properties.sqlIdDeleteSingle() : properties.sqlIdDeleteList();

        int cnt = sqlSession.sqlId( sqlId, properties.getParameter() ).execute();

        if( pkNotNull ) {
            refreshCache();
        }

        properties.init();

        return cnt;

    }

    @Override
    public T select( T entity ) {
        checkNotNull( entity );
        properties.setEntityParameter( entity );
        return select();
    }

    @Override
    public T select( Map parameter ) {
        properties.setEntityParameter( parameter );
        return select();
    }

    private T select() {
        T result = sqlSession.sqlId( properties.sqlIdSelectSingle(), properties.getParameter() ).select( domainClass );
        properties.init();
        return result;
    }

    @Override
    public NMap selectMap( T entity ) {
        checkNotNull( entity );
        properties.setEntityParameter( entity );
        return selectMap();
    }

    @Override
    public NMap selectMap( Map parameter ) {
        properties.setEntityParameter( parameter );
        return selectMap();
    }

    private NMap selectMap() {
        return sqlSession.sqlId( properties.sqlIdSelectSingle(), properties.getParameter() ).select();
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
    public OrmSession<T> changeEnvironmentId( String id ) {
        sqlSession.changeEnvironmentId( id );
        properties.setEnvironmentId( id );
        createOrmSql();
        return this;
    }

    @Override
    public OrmSession<T> disableCache() {
        sqlSession.getProperties().isCacheEnable( false );
        return this;
    }

    @Override
    public OrmSession<T> clearCache() {
        sqlSession.getProperties().isCacheClear( true );
        return this;
    }

    @Override
    public OrmSession<T> enableCache( String cacheId ) {
        return enableCache( cacheId, null );
    }

    @Override
    public OrmSession<T> enableCache( String cacheId, Integer flushSeconds ) {
        SqlRepository.setCacheProperties( properties.sqlIdSelectSingle(), cacheId, flushSeconds );
        return this;
    }

    @Override
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    private void checkNotNull( Object parameter ) {
        Assertion.isNotNull( parameter, new SqlConfigurationException( "OrmSession input parameter is null." ) );
    }

    private void checkPkNotNull() {
        Assertion.isTrue( properties.isPkNotNull(), new SqlConfigurationException( "PK has null value.({})", properties.getPkValues() ) );
    }

}
