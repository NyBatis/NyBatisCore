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
     * @param entity entity parameter to insert. it must contain PK data.
     * @return affected count
     */
    int insert( Object entity );

    /**
     * Merge entity
     *
     * @param entity entity parameter to merge. it must contain PK data.
     * @return affected count
     */
    int merge( Object entity );

    /**
     * Update entity.
     *
     * @param entity entity parameter to update.
     * @return affected count
     */
    int update( Object entity );

    /**
     * Delete entity or entities. If entity's class equals to domain class, only PK records affected.
     *
     * @param entity entity parameter to delete.
     * @return affected count
     */
    int delete( Object entity );

    /**
     * Select entity.
     *
     * @param entity entity parameter to select.
     * @return selected entity
     */
    T select( Object entity );

    /**
     * Select entity as map data.
     *
     *
     * @param entity entity parameter to select.
     * @return
     */
    NMap selectMap( Object entity );

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
    OrmSession<T> where( String sqlExpression );

    /**
     * Set <font color=blue><b>WHERE</b></font> clause used in SQL.
     *
     * It only affects to {@link OrmSession#update(Object)} or {@link OrmSession#delete(Object)}
     *
     * @param sqlExpression sql expression
     * @param parameter     parameter only applied in sql expression
     * @return self instance
     */
    OrmSession<T> where( String sqlExpression, Object parameter );

    /**
     * Commit and end transaction if it was activated
     */
    OrmSession<T> commit();

    /**
     * Rollback and end transaction if it was activated
     */
    OrmSession<T> rollback();

    /**
     * Begin transaction forcedly
     */
    OrmSession<T> beginTransaction();

    /**
     * End transaction forcedly
     */
    OrmSession<T> endTransaction();

    /**
     * Check transaction is activate
     *
     * @return is transaction activate
     */
    boolean isTransactionBegun();

    /**
     * Set environment id
     *
     * @param id environment id
     */
    OrmSession<T> setEnvironmentId( String id );

    /**
     * Cache statements should not be cached at once when has been executed.
     */
    OrmSession<T> disableCache();

    /**
     * Clear cache
     */
    OrmSession<T> clearCache();

    /**
     * Enable cache
     *
     * @param cacheId
     * @return self instance
     */
    OrmSession<T> enableCache( String cacheId );

    /**
     * Enable cache
     *
     * @param cacheId cacheId
     * @param flushSeconds cache flush cycle (unit:seconds)
     * @return self instance
     */
    OrmSession<T> enableCache( String cacheId, Integer flushSeconds );

    /**
     * Get native sql sqlSession
     *
     * @return native sql sqlSession
     */
    SqlSession getSqlSession();

}
