package org.nybatis.core.db.session.type.sql;

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
    SqlProperties properties;
    SqlBean sqlBean;

    public SessionExecutorImpl( SqlSessionImpl sqlSession ) {
        this.sqlSession = sqlSession;
        this.properties = sqlSession.getProperties().clone();
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

        SqlNode sqlNode = new SqlReader().read( properties.getEnvironmentId(), sql );

        sqlBean = new SqlBean( sqlNode, parameter );

        return this;
    }

    private SqlExecutor getExecutor() {
        return new SqlExecutor( sqlSession.getToken(), sqlBean.init( properties ) );
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
        return new ListExecutorImpl( sqlSession, properties, sqlBean );
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
    public SessionExecutor setAutoCommitAtOnce( boolean yn ) {
        properties.isAutocommit( yn );
        return this;
    }

    @Override
    public SessionExecutor disableCacheAtOnce() {
        properties.isCacheEnable( false );
        return this;
    }

    @Override
    public SessionExecutor clearCache() {
        properties.isCacheClear( true );
        return this;
    }
}
