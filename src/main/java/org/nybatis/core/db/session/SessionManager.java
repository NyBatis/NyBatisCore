package org.nybatis.core.db.session;

import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.db.session.type.sql.SqlSession;

/**
 * Session Manager
 *
 * @author nayasis@gmail.com
 */
public class SessionManager {

	/**
	 * Open Sql Session
	 *
	 * @param environmentId database environment id
	 * @return SqlSession
	 */
	public static SqlSession openSession( String environmentId ) {
		return new SessionCreator().createDefaultSqlSession( environmentId );
	}

	/**
	 * Open Sql Session with default environment id
	 *
	 * @return SqlSession
	 */
	public static SqlSession openSession() {
		return openSession( null );
	}

	/**
	 * Open seperated Sql Session for nested transaction handling.
	 *
	 * @param environmentId database environment id
	 * @return seperated SqlSession (Connection is different from another SqlSession)
	 */
	public static SqlSession openSeperateSession( String environmentId ) {
		return new SessionCreator().createSeperateSqlSession( environmentId );
	}

	/**
	 * Open seperated Sql Session with default environment id. it can handle nested transaction.
	 *
	 * @return seperated SqlSession (Connection is different from another SqlSession)
	 */
	public static SqlSession openSeperateSession() {
		return openSeperateSession( null );
	}

	/**
	 * Open ORM Session.
	 *
	 * @param environmentId database environment id
	 * @param tableName     database table name
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	public static <T> OrmSession<T> openOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return new SessionCreator().createDefaultOrmSession( environmentId, tableName, domainClass );
	}

	/**
	 * Open ORM Session.
	 * environment id is determined by <b>environmentId</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class.
	 *
	 * @param tableName     database table name
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	public static <T> OrmSession<T> openOrmSession( String tableName, Class<T> domainClass ) {
		return openOrmSession( null, tableName, domainClass );
	}

	/**
	 * Open ORM Session.
	 * table name is determined by <b>name</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class or domain class's uncamel name.
	 * and environment id is determined by <b>environmentId</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class.
	 *
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	public static <T> OrmSession<T> openOrmSession( Class<T> domainClass ) {
		return openOrmSession( null, null, domainClass );
	}

	/**
	 * Open seperated ORM Session for nested transaction handling.
	 *
	 * @param environmentId database environment id
	 * @param tableName     database table name
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	public static <T> OrmSession<T> openSeperateOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return new SessionCreator().createSeperateOrmSession( environmentId, tableName, domainClass );
	}

	/**
	 * Open seperated ORM Session for nested transaction handling.
	 * environment id is determined by <b>environmentId</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class.
	 *
	 * @param tableName     database table name
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	public static <T> OrmSession<T> openSeperateOrmSession( String tableName, Class<T> domainClass ) {
		return openOrmSession( null, tableName, domainClass );
	}

	/**
	 * Open seperated ORM Session for nested transaction handling.
	 * table name is determined by <b>name</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class or domain class's uncamel name.
	 * and environment id is determined by <b>environmentId</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class.
	 *
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	public static <T> OrmSession<T> openSeperateOrmSession( Class<T> domainClass ) {
		return openOrmSession( null, null, domainClass );
	}

	/**
	 * Set global parameter to bind with sql.
	 * 
	 * @param key	key
	 * @param value	value
	 */
	public static void setGlobalParameter( String key, Object value ) {
		GlobalSqlParameter.put( key, value );
	}

	/**
	 * Remove global parameter binding to sql.
	 * 
	 * @param key	key
	 */
	public static void removeGlobalParameter( String key ) {
		GlobalSqlParameter.remove( key );
	}

	/**
	 * Set compulsive environment id
	 *
	 * @param id environment id to run sql with it forcibly.
	 */
	public static void setCompulsiveEnvironment( String id ) {
		GlobalSqlParameter.setEnvironmentId( id );
	}

	/**
	 * Remove compulsive environment id setting.
	 */
	public static void removeCompulsiveEnvironment() {
		GlobalSqlParameter.removeEnvironmentId();
	}

	/**
	 * Set default environment id.
	 *
	 * @param id default environment id to run sql with it.
	 */
	public static void setDefaultEnvironment( String id ) {
		GlobalSqlParameter.setDefaultEnvironmentId( id );
	}

	/**
	 * Remove default environment id setting.
	 */
	public static void removeDefaultEnvironment() {
		GlobalSqlParameter.removeDefaultEnvironmentId();
	}

}