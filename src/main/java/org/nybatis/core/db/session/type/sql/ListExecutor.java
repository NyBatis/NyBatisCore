package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.annotation.SupportCache;
import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;

import java.util.List;

/**
 * ListExecutor
 *
 * @author nayasis@gmail.com
 * @since 2015-09-14
 */
public interface ListExecutor {

    /**
     * Retrieve list
     *
     * @return list NList list result
     */
    @SupportCache
    NList selectNList();

    /**
     * Retrieve list
     *
     * @return list
     */
    @SupportCache
    List<NMap> select();

    /**
     * Retrieve list
     *
     * @param returnType  Map or Bean (if you want to return row), Primitive (if you want to return value)
     * @return list consist with row or value
     */
    @SupportCache
    <T> List<T> select( Class<T> returnType );

    /**
     * Retrieve list with RowHandler
     *
     * @param rowHandler  RowHandler to treat single row data
     */
    @SupportCache
    void select( RowHandler rowHandler );

    /**
     * Set sql execution mode to count and get result
     *
     * @return result row's count
     */
    int count();

    /**
     * Set parameter
     *
     * @param parameter parameter to set
     * @return self instance
     */
    ListExecutor setParameter( Object parameter );

    /**
     * Set row fetch size temporary.<br>
     *
     * @param size row fetch size.
     * @return self instance
     */
    ListExecutor setFetchSize( int size );

    /**
     * Set lob pre-fetch size temporary <br>
     *
     * It is oracle-based funtionality.
     *
     * @param size lob prefetch size
     * @return self instance
     */
    ListExecutor setLobPrefetchSize( int size );

    /**
     * Cache statements should not be cached at once when has been executed.
     *
     * @return self instance
     */
    ListExecutor disableCache();

    /**
     * Clear cache
     *
     * @return self instance
     */
    ListExecutor clearCache();

    /**
     * Set sql exeution mode to page and it's limit.
     *
     * @param start start rownum
     * @param end   end rownum
     * @return self instance
     */
    ListExecutor setPage( int start, int end );

}
