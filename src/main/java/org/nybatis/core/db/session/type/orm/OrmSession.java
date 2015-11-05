package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.model.NMap;

import java.util.Map;

/**
 * ORM session
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmSession<T> {

    /**
     * Insert entity
     *
     * @param entity entity to insert. it must contain PK data.
     * @return affected count
     */
    int insert( T entity );

    /**
     * Insert map entity
     *
     * @param parameter parameter to insert. it must contain PK data.
     * @return affected count
     */
    int insert( Map parameter );

    /**
     * Merge entity
     *
     * @param entity entity to merge. it must contain PK data.
     * @return affected count
     */
    int merge( T entity );

    /**
     * Merge map entity
     *
     * @param parameter parameter to merge. it must contain PK data.
     * @return affected count
     */
    int merge( Map parameter );

    /**
     * Update entity
     *
     * @param entity entity to update
     * @return affected count
     */
    int update( T entity );

    /**
     * Update map entity
     *
     * @param parameter parameter to update
     * @return affected count
     */
    int update( Map parameter );

    /**
     * Delete entity
     *
     * @param entity entity to delete
     * @return affected count
     */
    int delete( T entity );

    /**
     * Delete map entity
     *
     * @param parameter parameter to delete
     * @return affected count
     */
    int delete( Map parameter );

    /**
     * Select entity
     *
     * @param entity entity parameter to select. it must contain PK data.
     * @return selected entity
     */
    T select( T entity );

    /**
     * Select entity
     *
     * @param parameter parameter to select. it must contain PK data.
     * @return selected entity
     */
    T select( Map parameter );

    /**
     * Select entity and get to map data format.
     *
     * @param entity
     * @return
     */
    NMap selectMap( T entity );

    /**
     * Select entity and get to map data format.
     *
     * @param parameter
     * @return
     */
    NMap selectMap( Map parameter );

    /**
     * Get list executor
     *
     * @return ListExecutor
     */
    OrmListExecutor<T> list();

    /**
     * Get batch executor
     *
     * @return BatchExecutor
     */
    OrmBatchExecutor<T> batch();

    /**
     * Set <font color=blue><b>WHERE</b></font> clause used in SQL.
     *
     * It only affects to {@link OrmSession#update(Object)} or {@link OrmSession#delete(Object)}
     *
     * @param sqlExpression sql expression
     * @return self instance
     */
    OrmSession where( String sqlExpression );

    /**
     * Set <font color=blue><b>WHERE</b></font> clause used in SQL.
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
    OrmSession disableCache();

    /**
     * Clear cache
     */
    OrmSession clearCache();

    /**
     * Enable cache
     *
     * @param cacheId
     * @return self instance
     */
    OrmSession enableCache( String cacheId );

    /**
     * Enable cache
     *
     * @param cacheId cacheId
     * @param flushSeconds cache flush cycle (unit:seconds)
     * @return self instance
     */
    OrmSession enableCache( String cacheId, Integer flushSeconds );

    /**
     * Get native sql sqlSession
     *
     * @return native sql sqlSession
     */
    SqlSession getSqlSession();

}
