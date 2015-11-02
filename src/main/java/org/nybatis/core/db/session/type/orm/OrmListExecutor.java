package org.nybatis.core.db.session.type.orm;

import java.util.List;
import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmListExecutor<T> {

    List<T> select();

    List<T> select( T parameter );

    List<T> select( Map parameter );

    int count( T parameter );

    OrmListExecutor<T> where( String sqlExpression );

    OrmListExecutor<T> where( String sqlExpression, Object parameter );

//    OrmListExecutor<T> removeWhere();

    OrmListExecutor<T> orderBy( String sqlExpression );

    /**
     * Set row fetch count temporary.<br/>
     *
     * @param count row fetch count.
     */
    OrmListExecutor<T> setRowFetchCountAtOnce( int count );

    /**
     * Cache statements should not be cached at once when has been executed.
     */
    OrmListExecutor<T> disableCacheAtOnce();

    /**
     * Clear cache
     */
    OrmListExecutor<T> clearCache();

    OrmListExecutor<T> setPage( int start, int end );

}
