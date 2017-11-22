package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.model.NMap;

/**
 * ORM session
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmSession<T> extends Cloneable {

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
     * Update entities.
     *
     * @return affected count
     */
    int update();

    /**
     * Delete entity or entities. If entity's class equals to domain class, only PK records affected.
     *
     * @param entity entity parameter to delete.
     * @return affected count
     */
    int delete( Object entity );

    /**
     * Delete entities.
     *
     * @return affected count
     */
    int delete();

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
     * @return map contains entity's value
     */
    NMap selectMap( Object entity );

    /**
     * Get list executor
     *
     * @return ListExecutor
     */
    OrmListExecutor<T> list();


    /**
     * get ORM table handler
     *
     * @return OrmTableHandler
     */
    OrmTableHandler<T> table();

    /**
     * Get batch executor
     *
     * @return BatchExecutor
     */
    OrmBatchExecutor<T> batch();

    /**
     * Set <font style="color:blue;bold;">WHERE</font> clause used in SQL.
     *
     * It only affects to {@link OrmSession#update(Object)} or {@link OrmSession#delete(Object)}
     *
     * @param sqlExpression sql expression
     * @return self instance
     */
    OrmSession<T> where( String sqlExpression );

    /**
     * Set <font style="color:blue;bold;">WHERE</font> clause used in SQL.
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
     *
     * @return self instance
     */
    OrmSession<T> commit();

    /**
     * Rollback and end transaction if it was activated
     *
     * @return self instance
     */
    OrmSession<T> rollback();

    /**
     * Begin transaction forcidly
     *
     * @return self instance
     */
    OrmSession<T> beginTransaction();

    /**
     * End transaction forcidly
     *
     * @return self instance
     */
    OrmSession<T> endTransaction();

    /**
     * Check transaction is activate
     *
     * @return true if transaction is activated.
     */
    boolean isTransactionBegun();

    /**
     * Set environment id
     *
     * @param id    environment id
     * @return self instance
     */
    OrmSession<T> setEnvironmentId( String id );

    /**
     * get representative environment id
     *
     * @return environment id
     */
    String getEnvironmentId();

    /**
     * Get native sql sqlSession
     *
     * @return native sql sqlSession
     */
    SqlSession getSqlSession();

    /**
     * allow Non-PK parameter (default : false)
     *
     * <pre>
     *   default is false to prevent unintended massive delete or update DB data.
     * </pre>
     *
     * @param enable    flag to allow Non-PK parameter
     * @return self instance
     */
    OrmSession<T> allowNonPkParameter( boolean enable );

    /**
     * Get name of database connected with session.
     *
     * @return database name
     */
    String getDatabaseName();

    /**
     * clone current instance.
     *
     * @return cloned sql session
     */
    OrmSession<T> clone();

    /**
     * check if session environment's database type is included
     *
     * @param dbName database type name
     * @return true if type is matched
     */
    boolean isDatabase( DatabaseName... dbName );

    /**
     * check not if session environment's database type is included
     *
     * @param dbName database type name
     * @return false if type is matched
     */
    boolean isNotDatabase( DatabaseName... dbName );

}
