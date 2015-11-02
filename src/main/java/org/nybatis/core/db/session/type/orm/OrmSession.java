package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.model.NMap;

import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmSession<T> {

    int insert( T entity );

    int insert( Map parameter );

    int merge( T entity );

    int merge( Map parameter );

    int update( T entity );

    int update( Map parameter );

    int delete( T entity );

    int delete( Map parameter );

    T select( T entity );

    T select( Map parameter );

    NMap selectMap( T entity );

    NMap selectMap( Map parameter );

    OrmListExecutor<T> list();

    OrmBatchExecutor<T> batch();

    /**
     * Set <font color=blue><b>WHERE CLAUSE</b></font> used in SQL.
     *
     * It only affects to {@link OrmSession#update(Object)} or {@link OrmSession#delete(Object)}
     *
     * @param sqlExpression sql expression
     * @return self instance
     */
    OrmSession where( String sqlExpression );

    /**
     * Set <font color=blue><b>WHERE CLAUSE</b></font> used in SQL.
     *
     * It only affects to {@link OrmSession#update(Object)} or {@link OrmSession#delete(Object)}
     *
     * @param sqlExpression sql expression
     * @param parameter     parameter only applied in sql expression
     * @return self instance
     */
    OrmSession where( String sqlExpression, Object parameter );

    /**
     * Commit and end transaction if it was activated
     */
    OrmSession commit();

    /**
     * Rollback and end transaction if it was activated
     */
    OrmSession rollback();

    /**
     * Begin transaction forcedly
     */
    OrmSession beginTransaction();

    /**
     * End transaction forcedly
     */
    OrmSession endTransaction();

    /**
     * Check transaction is activate
     *
     * @return is transaction activate
     */
    boolean isTransactionBegun();

    /**
     * Change environment id
     *
     * @param id environment id
     */
    OrmSession changeEnvironmentId( String id );

    /**
     * Cache statements should not be cached at once when has been executed.
     */
    OrmSession disableCacheAtOnce();

    /**
     * Clear cache
     */
    OrmSession clearCache();

    OrmSession setCacheProperties( String cacheId );

    OrmSession setCacheProperties( String cacheId, Integer cacheFlushCycle );

    OrmSession setRowFetchCountProperties( Integer rowFetchCount );

    /**
     * Get native sql sqlSession
     *
     * @return native sql sqlSession
     */
    SqlSession getSqlSession();

}
