package org.nybatis.core.db.session;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.db.session.type.orm.OrmSessionImpl;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.db.transaction.TransactionToken;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

import java.lang.annotation.Annotation;

/**
 * Session Creator
 *
 * @author nayasis@gmail.com
 */
public class SessionCreator {

	public SqlSession createSqlSession( String token, String environmentId ) {

		SqlProperties properties = new SqlProperties();

		if( StringUtil.isNotEmpty(environmentId) ) {
			properties.setEnvironmentId( environmentId );
		}

		return new SqlSessionImpl( token, properties );

	}

	public SqlSession createDefaultSqlSession( String environmentId ) {
		return createSqlSession( TransactionToken.getDefaultToken(), environmentId );
	}

	public SqlSession createSeperateSqlSession( String environmentId ) {
		return createSqlSession( TransactionToken.createToken(), environmentId );
	}

	public <T> OrmSession<T> createOrmSession( String token, String environmentId, String tableName, Class<T> domainClass ) {

		Assertion.isNotNull( domainClass, new SqlConfigurationException( "Domain class is null." ) );

		String domainTableName, domainEnvironmentId = null;

		Table tableAnnotation = getTableAnnotaion( domainClass );

		if( tableAnnotation != null ) {
			domainEnvironmentId = tableAnnotation.environmentId();
			domainTableName     = Validator.nvl( tableAnnotation.value(), tableAnnotation.name() );
			if( Const.db.DEFAULT_ENVIRONMENT_ID.equals( domainEnvironmentId ) ) domainEnvironmentId = null;
			if( Const.db.DEFAULT_TABLE_NAME.equals( domainTableName ) )
				domainTableName = StringUtil.toUncamel( domainClass.getSimpleName() ).toUpperCase();

		} else {
			domainTableName = StringUtil.toUncamel( domainClass.getSimpleName() ).toUpperCase();
		}

		environmentId = Validator.nvl( environmentId, domainEnvironmentId );
		tableName     = Validator.nvl( tableName,     domainTableName     );

		SqlSession sqlSession = createSqlSession( token, environmentId );

		return new OrmSessionImpl<>( domainClass, (SqlSessionImpl) sqlSession, tableName );

	}

	private Table getTableAnnotaion( Class domainClass ) {

		if( domainClass == null ) return null;

		Class klass = domainClass;

		while( true ) {

			if( klass.isAnnotationPresent( Table.class ) )
				return (Table) klass.getAnnotation( Table.class );

			klass = klass.getSuperclass();
			if( klass == Object.class ) return null;

		}

	}

	public <T> OrmSession<T> createDefaultOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return createOrmSession( TransactionToken.getDefaultToken(), environmentId, tableName, domainClass );
	}

	public <T> OrmSession<T> createSeperateOrmSession( String environmentId, String tableName, Class<T> domainClass ) {
		return createOrmSession( TransactionToken.createToken(), environmentId, tableName, domainClass );
	}


}