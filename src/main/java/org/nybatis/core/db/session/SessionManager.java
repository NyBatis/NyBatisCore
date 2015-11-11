package org.nybatis.core.db.session;

import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.db.session.type.sql.SqlSession;

public class SessionManager {

	public static SqlSession openSession( String environmentId ) {
		return new SessionCreator().createDefaultSqlSession( environmentId );
	}

	public static SqlSession openSession() {
		return openSession( null );
	}

	public static SqlSession openSeperateSession( String environmentId ) {
		return new SessionCreator().createSeperateSqlSession( environmentId );
	}

	public static SqlSession openSeperateSession() {
		return openSeperateSession( null );
	}

	public static <T> OrmSession<T> openOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return new SessionCreator().createDefaultOrmSession( environmentId, tableName, domainClass );
	}

	public static <T> OrmSession<T> openOrmSession( String tableName, Class<T> domainClass ) {
		return openOrmSession( null, tableName, domainClass );
	}

	public static <T> OrmSession<T> openOrmSession( Class<T> domainClass ) {
		return openOrmSession( null, null, domainClass );
	}

	public static <T> OrmSession<T> openSeperateOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return new SessionCreator().createSeperateOrmSession( environmentId, tableName, domainClass );
	}

	public static <T> OrmSession<T> openSeperateOrmSession( String tableName, Class<T> domainClass ) {
		return openOrmSession( null, tableName, domainClass );
	}

	public static <T> OrmSession<T> openSeperateOrmSession( Class<T> domainClass ) {
		return openOrmSession( null, null, domainClass );
	}

	public static void setGlobalParameter( String key, Object value ) {
		GlobalSqlParameter.put( key, value );
	}

	public static void removeGlobalParameter( String key ) {
		GlobalSqlParameter.remove( key );
	}

	public static void setGlobalEnvironment( String id ) {
		GlobalSqlParameter.setEnvironmentId( id );
	}

	public static void removeGlobalEnvironment() {
		GlobalSqlParameter.removeEnvironmentId();
	}

}