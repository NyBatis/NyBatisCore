package org.nybatis.core.db.session;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.db.session.type.orm.OrmSessionImpl;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.db.transaction.TransactionToken;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

public class SessionManager {

	private static SqlSession createSqlSession( boolean isSeperateSession, String environmentId ) {

		SqlProperties properties = new SqlProperties();
		properties.setEnvironmentId( StringUtil.isNotEmpty( environmentId ) ? environmentId : properties.getEnvironmentId() );

		return new SqlSessionImpl( isSeperateSession ? TransactionToken.createToken() : TransactionToken.getDefaultToken(), properties );

	}

	public static SqlSession openSession( String environmentId ) {
		return createSqlSession( false, environmentId );
	}

	public static SqlSession openSession() {
		return createSqlSession( false, null );
	}

	public static SqlSession openSeperateSession( String environmentId ) {
		return createSqlSession( true, environmentId );
	}

	public static SqlSession openSeperateSession() {
		return createSqlSession( true, null );
	}

	public static <T> OrmSession<T> openOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return openOrmSession( environmentId, tableName, domainClass, false );
	}

	public static <T> OrmSession<T> openOrmSession( String tableName, Class<T> domainClass ) {
		return openOrmSession( null, tableName, domainClass, false );
	}

	public static <T> OrmSession<T> openOrmSession( Class<T> domainClass ) {
		return openOrmSession( null, null, domainClass, false );
	}

	public static <T> OrmSession<T> openSeperateOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return openOrmSession( environmentId, tableName, domainClass, true );
	}

	public static <T> OrmSession<T> openSeperateOrmSession( String tableName, Class<T> domainClass ) {
		return openOrmSession( null, tableName, domainClass, true );
	}

	public static <T> OrmSession<T> openSeperateOrmSession( Class<T> domainClass ) {
		return openOrmSession( null, null, domainClass, true );
	}

	private static <T> OrmSession<T> openOrmSession( String environmentId, String tableName, Class<T> domainClass, boolean seperate ) {

		Assertion.isNotNull( domainClass, new SqlConfigurationException( "Domain class is null." ) );

		String domainTableName, domainEnvironmentId = null;

		if( domainClass.isAnnotationPresent( Table.class ) ) {
			Table table = domainClass.getAnnotation( Table.class );
			domainEnvironmentId = table.environmentId();
			domainTableName     = ( table.value() == null || table.value().isEmpty() ) ? table.name() : table.value();
			if( Const.db.DEFAULT_ENVIRONMENT_ID.equals( domainEnvironmentId ) ) domainEnvironmentId = null;
			if( Const.db.DEFAULT_TABLE_NAME.equals( domainTableName ) ) domainTableName = StringUtil.toUncamel( domainClass.getSimpleName() ).toUpperCase();

		} else {
			domainTableName = StringUtil.toUncamel( domainClass.getSimpleName() ).toUpperCase();
		}

		environmentId = Validator.nvl( environmentId, domainEnvironmentId );
		tableName     = Validator.nvl( tableName,     domainTableName     );

		SqlSession sqlSession = seperate ? openSeperateSession( environmentId ) : openSession( environmentId );

		return new OrmSessionImpl<>( domainClass, (SqlSessionImpl) sqlSession, tableName );

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