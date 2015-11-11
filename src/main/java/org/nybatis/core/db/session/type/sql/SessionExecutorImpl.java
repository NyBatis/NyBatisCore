package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.cache.CacheManager;
import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.SqlExecutor;
import org.nybatis.core.db.sql.reader.SqlReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.model.NMap;
import org.nybatis.core.validation.Assertion;

/**
 * @author Administrator
 * @since 2015-09-12
 */
public class SessionExecutorImpl implements SessionExecutor {

    SqlSessionImpl sqlSession;
    SqlBean        sqlBean;

    public SessionExecutorImpl( SqlSessionImpl sqlSession ) {
        this.sqlSession = sqlSession;
    }

    public SessionExecutor sqlId( String id ) {
        return sqlId( id, null );
    }

    public SessionExecutor sqlId( String id, Object parameter ) {

        SqlNode sqlNode = SqlRepository.get( id );

        Assertion.isNotNull( sqlNode, "There is no sql id({}) in repository.", id );

        sqlBean = new SqlBean( sqlNode, parameter );

        return this;
    }

    public SessionExecutor sql( String sql ) {
        return sql( sql, null );
    }

    public SessionExecutor sql( String sql, Object parameter ) {

        SqlNode sqlNode = new SqlReader().read( sqlSession.getProperties().getEnvironmentId(), sql );

        sqlBean = new SqlBean( sqlNode, parameter );

        return this;
    }

    private SqlExecutor getExecutor() {
        try {
            return new SqlExecutor( sqlSession.getToken(), sqlBean.init( sqlSession.getProperties() ) );
        } finally {
            sqlSession.initProperties();
        }
    }

    @Override
    public NMap select() {
        return getExecutor().select();
    }

    @Override
    public <T> T select( Class<T> returnType ) {
        return getExecutor().select( returnType );
    }

    @Override
    public ListExecutor list() {
        return new ListExecutorImpl( sqlSession, sqlBean );
    }

    @Override
    public int execute() {
        return getExecutor().update();
    }

    @Override
    public NMap call() {
        return getExecutor().call();
    }

    @Override
    public NMap call( Class<?>... listReturnTypes ) {
        return getExecutor().call( listReturnTypes );
    }

    @Override
    public <T> T call( Class<T> returnType, Class<?>... listReturnTypes ) {
        return getExecutor().call( returnType, listReturnTypes );
    }

    @Override
    public SessionExecutor setParameter( Object parameter ) {
        sqlBean.setParameter( parameter );
        return this;
    }

    @Override
    public SessionExecutor setAutoCommit( boolean enable ) {
        sqlSession.getProperties().isAutocommit( enable );
        return this;
    }

    @Override
    public SessionExecutor disableCache() {
        CacheManager.disableCache( sqlBean.getSqlId() );
        return this;
    }

    @Override
    public SessionExecutor enableCache( String cacheId ) {
        return enableCache( cacheId, null );
    }

    @Override
    public SessionExecutor enableCache( String cacheId, Integer flushCycle ) {
        CacheManager.enableCache( sqlBean.getSqlId(), cacheId, flushCycle );
        return this;
    }

    @Override
    public SessionExecutor clearCache() {
        sqlSession.getProperties().isCacheClear( true );
        return this;
    }

}