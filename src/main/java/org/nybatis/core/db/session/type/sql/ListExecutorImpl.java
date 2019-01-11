package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.SqlExecutor;
import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;

import java.util.List;

/**
 * ListExecutor implements
 *
 * @author nayasis@gmail.com
 * @since 2015-09-14
 */
public class ListExecutorImpl implements ListExecutor {

    SqlSessionImpl sqlSession;
    SqlBean        sqlBean;

    public ListExecutorImpl( SqlSessionImpl sqlSession, SqlBean sqlBean ) {
        this.sqlSession = sqlSession;
        this.sqlBean    = sqlBean;
    }

    @Override
    public NList selectNList() {
        return getExecutor().selectNList();
    }

    @Override
    public List<NMap> select() {
        return getExecutor().selectList();
    }

    @Override
    public <T> List<T> select( Class<T> returnType ) {
        return getExecutor().selectList( returnType );
    }

    @Override
    public <T> T selectOne( Class<T> returnType ) {
        return getExecutor().select( returnType );
    }

    @Override
    public void select( RowHandler rowHandler ) {
        getExecutor().selectList( rowHandler );
    }

    @Override
    public int count() {
        sqlSession.getProperties().isCountSql( true );
        return getExecutor().select( Integer.class );
    }

    @Override
    public ListExecutor setParameter( Object parameter ) {
        sqlBean.setParameter( parameter );
        return this;
    }

    @Override
    public ListExecutor setFetchSize( int size ) {
        sqlSession.getProperties().setFetchSize( size );
        return this;
    }

    @Override
    public ListExecutor setLobPrefetchSize( int size ) {
        sqlSession.getProperties().setLobPrefetchSize( size );
        return this;
    }

    @Override
    public ListExecutor setPage( Integer start, Integer end ) {
        sqlSession.getProperties().setPageSql( start, end );
        return this;
    }

    private SqlExecutor getExecutor() {
        try {
            return new SqlExecutor( sqlSession.getToken(), sqlBean.init( sqlSession.getProperties() ) );
        } finally {
            sqlSession.initProperties();
        }
    }

}
