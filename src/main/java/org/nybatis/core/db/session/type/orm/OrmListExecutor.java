package org.nybatis.core.db.session.type.orm;

import java.util.List;
import java.util.Map;

/**
 * ORM ListExecutor
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmListExecutor<T> {

    /**
     * Retrieve list
     *
     * @return list
     */
    List<T> select();

    /**
     * Retrieve list
     *
     * @param parameter parameter
     * @return
     */
    List<T> select( Object parameter );

    int count( T parameter );

    /**
     * Add 'where' condition
     *
     * @param sqlExpression sql expression
     * @return self instance
     */
    OrmListExecutor<T> where( String sqlExpression );

    /**
     * Add 'where' condition
     *
     * @param sqlExpression sql expression
     * @param parameter parameter to bind
     * @return self instance
     */
    OrmListExecutor<T> where( String sqlExpression, Object parameter );

    /**
     * Set 'order by' expression
     *
     * @param sqlExpression sql expression
     * @return self instance
     */
    OrmListExecutor<T> orderBy( String sqlExpression );

    /**
     * Set row fetch size temporary.<br/>
     *
     * @param size row fetch size.
     */
    OrmListExecutor<T> setFetchSize( int size );

    /**
     * Set lob pre-fetch size temporary <br>
     *
     * It is oracle-based funtionality.
     *
     * @param size lob prefetch size
     * @return
     */
    OrmListExecutor<T> setLobPrefetchSize( int size );

    /**
     * Cache statements should not be cached at once when has been executed.
     */
    OrmListExecutor<T> disableCache();

    /**
     * Enable cache
     *
     * @param cacheId
     * @return self instance
     */
    OrmListExecutor<T> enableCache( String cacheId );

    /**
     * Enable cache
     *
     * @param cacheId cacheId
     * @param flushSeconds cache flush cycle (unit:seconds)
     * @return self instance
     */
    OrmListExecutor<T> enableCache( String cacheId, Integer flushSeconds );

    /**
     * Clear cache
     */
    OrmListExecutor<T> clearCache();

    OrmListExecutor<T> setPage( int start, int end );

}
