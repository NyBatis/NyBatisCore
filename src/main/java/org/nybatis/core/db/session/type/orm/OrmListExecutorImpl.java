package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.exception.unchecked.InvalidArgumentException;

import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public class OrmListExecutorImpl<T> implements OrmListExecutor<T> {

    private SqlSessionImpl       sqlSession;
    private OrmSessionProperties properties  = new OrmSessionProperties();
    private Class<T>             domainClass = null;

    public OrmListExecutorImpl( Class<T> domainClass, SqlSessionImpl sqlSession, OrmSessionProperties properties ) {
        this.domainClass = domainClass;
        this.sqlSession  = sqlSession;
        this.properties  = properties.clone();
    }

    @Override
    public List<T> select() {
        properties.setEntityParameter( null );
        return sqlSession.sqlId( properties.sqlIdSelect(), properties.getParameter() ).list().select( domainClass );
    }

    @Override
    public List<T> select( Object parameter ) {
        checkParameterValidation( parameter );
        properties.setEntityParameter( parameter );
        return sqlSession.sqlId( properties.sqlIdSelect(), properties.getParameter() ).list().select( domainClass );
    }

    private void checkParameterValidation( Object parameter ) {
        if( parameter != null && parameter instanceof Class ) {
            throw new InvalidArgumentException( "The parameter [{}] must be query parameter. OrmSession's select parameter doesn't represent reflection of return result;", parameter );
        }
    }

    @Override
    public int count( Object parameter ) {
        checkParameterValidation( parameter );
        properties.setEntityParameter( parameter );
        return sqlSession.sqlId( properties.sqlIdSelect(), properties.getParameter() ).list().count();
    }

    @Override
    public int count() {
        return count( null );
    }

    @Override
    public OrmListExecutor<T> where( String sqlExpression ) {
        properties.addWhere( sqlExpression, null );
        return this;
    }

    @Override
    public OrmListExecutor<T> where( String sqlExpression, Object parameter ) {
        properties.addWhere( sqlExpression, parameter );
        return this;
    }

    @Override
    public OrmListExecutor<T> orderBy( String sqlExpression ) {
        properties.setOrderBy( sqlExpression );
        return this;
    }

    @Override
    public OrmListExecutor<T> setFetchSize( int size ) {
        sqlSession.getProperties().setFetchSize( size );
        return this;
    }

    @Override
    public OrmListExecutor<T> setLobPrefetchSize( int size ) {
        sqlSession.getProperties().setLobPrefetchSize( size );
        return this;
    }

    @Override
    public OrmListExecutor<T> setPage( Integer start, Integer end ) {
        sqlSession.getProperties().setPageSql( start, end );
        return this;
    }

}
