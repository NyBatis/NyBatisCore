package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.SqlExecutor;
import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;

import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-14
 */
public class ListExecutorImpl implements ListExecutor {

    SqlSessionImpl sqlSession;
    SqlProperties properties;
    SqlBean sqlBean;

    public ListExecutorImpl( SqlSessionImpl sqlSession, SqlProperties properties, SqlBean sqlBean ) {
        this.sqlSession = sqlSession;
        this.properties = properties;
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
    public void select( RowHandler rowHandler ) {
        getExecutor().selectList( rowHandler );
    }

    @Override
    public int count() {
        sqlBean.getProperties().isCountSql( true );
        return getExecutor().select( Integer.class );
    }

    @Override
    public ListExecutor setParameter( Object parameter ) {
        sqlBean.setParameter( parameter );
        return this;
    }

    @Override
    public ListExecutor setFetchSize( int size ) {
        properties.setFetchSize( size );
        return this;
    }

    @Override
    public ListExecutor setLobPrefetchSize( int size ) {
        properties.setLobPrefetchSize( size );
        return this;
    }

    @Override
    public ListExecutor disableCache() {
        properties.isCacheEnable( false );
        return this;
    }

    @Override
    public ListExecutor clearCache() {
        properties.isCacheClear( true );
        return this;
    }

    @Override
    public ListExecutor setPage( int start, int end ) {
        properties.setPageSql( start, end );
        return this;
    }

    private SqlExecutor getExecutor() {
        return new SqlExecutor( sqlSession.getToken(), sqlBean.init( properties ) );
    }

}
