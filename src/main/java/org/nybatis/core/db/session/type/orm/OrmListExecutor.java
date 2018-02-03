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
     * Retrieve single row
     *
     * @return first row in list
     */
    T selectOne();

    /**
     * Retrieve single row
     *
     * @param parameter parameter
     * @return first row in list
     */
    T selectOne( Object parameter );

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
     * @return list
     */
    List<T> select( Object parameter );

    /**
     * get list's row count
     *
     * @param parameter parameter
     * @return row count
     */
    int count( Object parameter );

    /**
     * get list's row count
     *
     * @return row count
     */
    int count();

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
     * Set row fetch size temporary.<br>
     *
     * @param size row fetch size.
     * @return self instance
     */
    OrmListExecutor<T> setFetchSize( int size );

    /**
     * Set lob pre-fetch size temporary <br>
     *
     * It is oracle-based funtionality.
     *
     * @param size lob prefetch size
     * @return self instance
     */
    OrmListExecutor<T> setLobPrefetchSize( int size );

    /**
     * Set sql exeution mode to page and it's limit.
     *
     * @param start start rownum
     * @param end   end rownum
     * @return self instance
     */
    OrmListExecutor<T> setPage( Integer start, Integer end );

}
