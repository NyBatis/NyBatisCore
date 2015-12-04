package org.nybatis.core.db.session;

import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class SqlSessionMultiEnvironmentTest {

	public static final String TABLE_NAME     = "NYBATIS_TEST";

	@BeforeClass
	public void beforeTest() {

	    DatabaseConfigurator.build();

		/**
		 * 1. 특정 파일에서 DatabastConfiguration을 읽으려면 다음과 같다.
		 *
		 * DatabaseConfigurator.build( "/usr/app/config/dbConfig.xml" );
		 *
		 * 2. Maven test 디렉토리에서 src 디렉토리의 설정을 바라보게 하려면 다음과 같다.
		 *
		 *    (1) basePath 경로를 변경해준다.
		 *        Const.path.setBase( Const.path.getBase().replaceFirst( "/test-classes", "/classes" )  );
		 *
		 *    (2) Configuration 파일을 읽어들인다.
		 *        DatabaseConfigurator.build();
		 *
		 */

	}

	@AfterMethod
	public void releaseConnection() {
		TransactionManager.commit();
	}

	@BeforeMethod
	public void initTableForMultiEnvironment() {

		SqlSession session = SessionManager.openSeperateSession();

		session.sql( String.format( "DROP TABLE IF EXISTS %s", TABLE_NAME ) ).execute();
		session.sql( String.format( "CREATE TABLE %s ( list_id TEXT, prod_id TEXT, price NUMBER, prod_name TEXT, image BLOB, PRIMARY KEY(list_id, prod_id) )", TABLE_NAME ) ).execute();

		session.sql( String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( '%s', '%s', '%s' )", TABLE_NAME, "LIST01", "PROD01", "Product01") ).execute();

		session.setEnvironmentId( "sqlite02" );

		session.sql( String.format( "DROP TABLE IF EXISTS %s", TABLE_NAME ) ).execute();
		session.sql( String.format( "CREATE TABLE %s ( list_id TEXT, prod_id TEXT, price NUMBER, prod_name TEXT, image BLOB, PRIMARY KEY(list_id, prod_id) )", TABLE_NAME ) ).execute();

		session.sql( String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( '%s', '%s', '%s' )", TABLE_NAME, "LIST02", "PROD02", "Product02") ).execute();

		session.setEnvironmentId( "sqlite03" );

		session.sql( String.format( "DROP TABLE IF EXISTS %s", TABLE_NAME ) ).execute();
		session.sql( String.format( "CREATE TABLE %s ( list_id TEXT, prod_id TEXT, price NUMBER, prod_name TEXT, image BLOB, PRIMARY KEY(list_id, prod_id) )", TABLE_NAME ) ).execute();

		session.sql( String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( '%s', '%s', '%s' )", TABLE_NAME, "LIST03", "PROD03", "Product03") ).execute();

		session.commit();
	}

	@Test( sequential = true )
	public void case01_Basic() {

		SqlSession session = SessionManager.openSession();

		Assert.assertEquals( getSelectForMultiEnvironmentTest(session).get( "listId" ), "LIST01" );

		session = SessionManager.openSession( "sqlite02" );

		Assert.assertEquals( getSelectForMultiEnvironmentTest( session ).get( "listId" ), "LIST02" );


	}

	private NMap getSelectForMultiEnvironmentTest( SqlSession session ) {
		return session.sql( "SELECT * FROM " + TABLE_NAME ).select();
	}

	@Test( sequential = true )
	public void case02_DefaultEnvironmentChange() {

		SqlSession session = SessionManager.openSession();

		Assert.assertEquals( getSelectForMultiEnvironmentTest(session).get( "listId" ), "LIST01" );

		NLogger.debug( getSelectForMultiEnvironmentTest( session ) );

		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite02" );

		Assert.assertEquals( getSelectForMultiEnvironmentTest( session ).get( "listId" ), "LIST02" );

		NLogger.debug( getSelectForMultiEnvironmentTest( session ) );


	}

	@Test( sequential = true )
	public void case03_DefaultEnvironmentChangeInXmlSql() {

		/**
		 * SQL_ID : FileName.id
		 *
		 * SQL_ID owns it's environments. it can be detemined by {@link GlobalSqlParameter#setDefaultEnvironmentId(String)}
		 * but when there is no global default environment setting, default value is configuration's default environment.
		 *
		 * When the value set by {@link GlobalSqlParameter#setDefaultEnvironmentId(String)} is not included in SQL_ID's environments,
		 * it is ignored and SQL_ID executed in it's possessed environment id.
		 */

		SqlSession session = SessionManager.openSession();

		// determined by first setted environment
		Assert.assertEquals( session.sqlId( "Sqlite.selectForMultiEnvironmentTest" ).select().get( "listId" ), "LIST01" );

		// affected by defaultEnvironment setting
		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite02" );

		Assert.assertEquals( session.sqlId( "Sqlite.selectForMultiEnvironmentTest" ).select().get( "listId" ), "LIST02" );


		// Not affected by defaultEnvironment setting
		Assert.assertEquals( session.sqlId( "Sqlite03.selectForMultiEnvironmentTest" ).select().get( "listId" ), "LIST03" );

		// Affected by compulsiveEnvironment setting
		GlobalSqlParameter.setCompulsiveEnvironmentId( "sqlite02" );

		Assert.assertEquals( session.sqlId( "Sqlite03.selectForMultiEnvironmentTest" ).select().get( "listId" ), "LIST02" );

	}

	private void clearTable() {

		SqlSession session = SessionManager.openSeperateSession();

		session.setEnvironmentId( "sqlite01" ).sql( String.format( "DELETE FROM %s", TABLE_NAME ) ).execute();
		session.setEnvironmentId( "sqlite02" ).sql( String.format( "DELETE FROM %s", TABLE_NAME ) ).execute();

		session.commit();

	}

	@Test( sequential = true )
	public void case03_statementBatch() {

		clearTable();

		SqlSession session = SessionManager.openSession();

		List<String> statements = new ArrayList<>();

		for( int i = 1; i <= 5; i++ ) {
			statements.add( String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( 'LIST0%d', 'PROD0%d', 'Product0%d' )", TABLE_NAME, i, i, i ) );
		}

		session.batchSql( statements ).execute();

		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite02" );

		statements = new ArrayList<>();

		for( int i = 1; i <= 10; i++ ) {
			statements.add( String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( 'LIST0%d', 'PROD0%d', 'Product0%d' )", TABLE_NAME, i, i, i ) );
		}

		session.batchSql( statements ).execute();

		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite01" );

		Assert.assertEquals( session.sqlId( "Sqlite.selectForMultiEnvironmentTest" ).list().count(),  5 );


		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite02" );

		Assert.assertEquals( session.sqlId( "Sqlite.selectForMultiEnvironmentTest" ).list().count(), 10 );

	}

	@Test( sequential = true )
	public void case04_preparedBatch() {

		clearTable();

		String sql = String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( #{listId}, #{prodId}, #{prodName} )", TABLE_NAME );

		SqlSession session = SessionManager.openSession();

		List<NMap> params = new ArrayList<>();

		for( int i = 1; i <= 5; i++ ) {

			NMap param = new NMap();
			param.put( "listId",   "LIST0"    + i );
			param.put( "prodId",   "PROD0"    + i );
			param.put( "prodName", "Product0" + i );

			params.add( param );

		}

		session.batchSql( sql, params ).execute();

		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite02" );

		params = new ArrayList<>();

		for( int i = 1; i <= 10; i++ ) {

			NMap param = new NMap();
			param.put( "listId",   "LIST0"    + i );
			param.put( "prodId",   "PROD0"    + i );
			param.put( "prodName", "Product0" + i );

			params.add( param );

		}

		session.batchSql( sql, params ).execute();

		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite01" );

		Assert.assertEquals( session.sqlId( "Sqlite.selectForMultiEnvironmentTest" ).list().count(),  5 );


		GlobalSqlParameter.setDefaultEnvironmentId( "sqlite02" );

		Assert.assertEquals( session.sqlId( "Sqlite.selectForMultiEnvironmentTest" ).list().count(), 10 );

	}

}