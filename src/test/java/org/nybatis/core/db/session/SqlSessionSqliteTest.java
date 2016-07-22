package org.nybatis.core.db.session;

import org.nybatis.core.db.cache.CacheManager;
import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.session.type.sql.ListExecutor;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.vo.ResultVo;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SqlSessionSqliteTest {

	public static final String TABLE_NAME     = "NYBATIS_TEST";
	public static final String ENVIRONMENT_ID = "sqlite03";

	private SqlSession getSession() {
		return SessionManager.openSession( ENVIRONMENT_ID );
	}

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

	@Test( sequential = true )
	public void case00_printSqlRepository() {
		NLogger.debug( new SqlRepository().toString() );
	}

	@Test( sequential = true, expectedExceptions = SqlException.class )
	public void case00_nullParameterBindingError() {

		SqlSession sqlSession = getSession();

		try {
			sqlSession.sql( "INSERT INTO ${tableName} ( list_id, prod_id, prod_name ) VALUES ( #{listId}, #{prodId}, #{prodName} )" ).execute();
		} catch( Exception e ) {
			NLogger.error( e );
			throw e;
		}

	}

	private void case10_initTable() {

		SqlSession sqlSession = getSession();

		sqlSession.sql( String.format( "DROP TABLE IF EXISTS %s", TABLE_NAME ) ).execute();
//		sqlSession.sql( String.format( "CREATE TABLE IF NOT EXISTS %s ( list_id TEXT, prod_id TEXT, price NUMBER, prod_name TEXT, image BLOB, PRIMARY KEY(list_id, prod_id) )", TABLE_NAME ) ).execute();		sqlSession.sql( String.format( "DROP TABLE IF EXISTS %s", TABLE_NAME ) ).execute();
//		sqlSession.sql( String.format( "DROP TABLE %s", TABLE_NAME ) ).execute();
		sqlSession.sql( String.format( "CREATE TABLE %s ( list_id TEXT, prod_id TEXT, price NUMBER, prod_name TEXT, image BLOB, PRIMARY KEY(list_id, prod_id) )", TABLE_NAME ) ).execute();
		sqlSession.commit();

	}

	@Test( sequential = true )
	public void case10_initDummyDataByStatementBatch() {

		case10_initTable();

		SqlSession sqlSession = getSession();

		List<String> sqlList = new ArrayList<>();

		for( int i = 0; i < 100; i++ ) {

			String sql = String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( '%s', '%s', '%s' )",
					TABLE_NAME,
					"RNK" + StringUtil.lpad( i % 10, '0', 5 ),
					i,
					"PROD-" + i
			);

			sqlList.add( sql );

		}

		sqlSession.batchSql( sqlList ).setTransactionSize( 10 ).execute();

		case10_select();

	}

	@Test( sequential = true )
	public void case10_initDummyDataByPreparedStatementBatch() {

		case10_initTable();

		SqlSession sqlSession = getSession();

		List<NMap> parameters = new ArrayList<>();

		for( int i = 0; i < 100; i++ ) {

			NMap param = new NMap();

			param.put( "tableName", TABLE_NAME );
			param.put( "listId", "RNK" + StringUtil.lpad( i % 10, '0', 5 ) );
			param.put( "prodId", i );
			param.put( "prodName", "PROD-" + i );

			parameters.add( param );

		}

		String sql = "INSERT INTO ${tableName} ( list_id, prod_id, prod_name ) VALUES ( #{listId}, #{prodId}, #{prodName} )";

		sqlSession.batchSql( sql, parameters ).setTransactionSize( 10 ).execute();

		case10_select();

	}

	@Test( sequential = true )
	public void case10_initDummyData() {

		case10_initTable();

		SqlSession sqlSession = getSession();

		for( int i = 0; i < 100; i++ ) {

			NMap param = new NMap();

			param.put( "tableName", TABLE_NAME );
			param.put( "listId", "RNK" + StringUtil.lpad( i % 10, '0', 5 ) );
			param.put( "prodId", i );
			param.put( "prodName", "PROD-" + i );

			sqlSession.sql( "INSERT INTO ${tableName} ( list_id, prod_id, prod_name ) VALUES ( #{listId}, #{prodId}, #{prodName} )", param ).execute();

		}

		sqlSession.commit();

		case10_select();

	}

	@Test( sequential = true )
	public void case10_select() {

		SqlSession sqlSession = getSession();

		int count = sqlSession.sql( "SELECT count(1) FROM ${tableName}", TABLE_NAME ).select( Integer.class );

		NLogger.debug( "count : {}", count );

		Assert.assertEquals( count, 100 );

		NMap param = new NMap();

		param.put( "table", TABLE_NAME );
		param.put( "prodIds", Arrays.asList( 98, 99, 100 ) );

		NList list = sqlSession.sql( "SELECT * FROM ${table} WHERE prod_id in ( #{prodIds} )", param ).list().selectNList();

		NLogger.debug( list );

		Assert.assertEquals( 2, list.size() );

	}

	@Test( sequential = true )
	public void case10_transactionTest_01() {

		SqlSession sqlSession = getSession();

		Map<String, String> param = new HashMap<>();

		param.put( "tableName", TABLE_NAME );
		param.put( "prodId", "10" );

		String SQL_SELECT = "SELECT * FROM ${tableName} WHERE prod_id = #{prodId}";
		String SQL_DELETE = "DELETE   FROM ${tableName} WHERE prod_id = #{prodId}";

		for( int i = 0; i < 1000; i++ ) {

			NLogger.debug( "loop : {}", i );

			assertTrue( sqlSession.sql( SQL_SELECT, param ).select( Map.class ).size() != 0 );

			sqlSession.sql( SQL_DELETE, param ).execute();

			assertTrue( sqlSession.sql( SQL_SELECT, param ).select( Map.class ).size() == 0 );

			sqlSession.rollback();

		}

	}

	@Test( sequential = true )
	public void case10_transactionTest_02_CommitTest_01_DeleteAndCommit() {

		case10_initDummyDataByPreparedStatementBatch();

		SqlSession sqlSession = getSession();

		Map<String, String> param = new HashMap<>();

		param.put( "tableName", TABLE_NAME );
		param.put( "prodId",    "10" );

		String SQL_SELECT = "SELECT * FROM ${tableName} WHERE prod_id = #{prodId}";
		String SQL_DELETE = "DELETE   FROM ${tableName} WHERE prod_id = #{prodId}";

		assertTrue( sqlSession.sql( SQL_SELECT, param ).select( Map.class ).size() != 0 );

		sqlSession.sql( SQL_DELETE, param ).execute();

		assertTrue( sqlSession.sql( SQL_SELECT, param ).select( Map.class ).size() == 0 );

		sqlSession.commit();

	}

	@Test( sequential = true )
	public void case10_transactionTest_03_CommitTest_02_CheckCommitted() {

		SqlSession sqlSession = getSession();

		Map<String, String> param = new HashMap<>();

		param.put( "tableName", TABLE_NAME );
		param.put( "prodId", "10" );

		String SQL_SELECT = "SELECT * FROM ${tableName} WHERE prod_id = #{prodId}";

		NLogger.debug( ">>> [{}]", sqlSession.sql( SQL_SELECT, param ).select( Map.class ) );

		assertTrue( sqlSession.sql( SQL_SELECT, param ).select( Map.class ).size() == 0 );

	}

	@Test( sequential = true )
	public void case11_cache() {

		NLogger.debug( new CacheManager() );

		SqlSession sqlSession = SessionManager.openSession();

		ListExecutor listExecutor = sqlSession.sqlId( "Sqlite.selectForList", "RNK00001" ).list();

		for( int i = 0; i < 10; i++ ) {

			if( i == 3 ) {
				listExecutor.clearCache();
			}

			if( i == 8 ) {
				listExecutor.disableCache();
			}

			List<ResultVo> list = listExecutor.select( ResultVo.class );
			NLogger.debug( "index : {}, count : {}", i, list.size() );

		}

	}

	@Test( sequential = true, expectedExceptions = SqlException.class )
	public void case11_batchErrorLog() {

		case10_initTable();

		SqlSession sqlSession = getSession();

		List<String> sqlList = new ArrayList<>();

		for( int i = 0; i < 100; i++ ) {

			String sql = String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( '%s', '%s', '%s' )",
					TABLE_NAME,
					"RNK" + StringUtil.lpad( i, '0', 5 ),
					i,
					"PROD-" + i
			);

			sqlList.add( sql );

		}

		String sql = String.format( "INSERT INTO %s ( list_id, prod_id, prod_name ) VALUES ( '%s', '%s', '%s' )",
				TABLE_NAME,
				"RNK" + StringUtil.lpad( 10, '0', 5 ),
				10,
				"PROD-" + 10
		);

		sqlList.add( 13, sql );

		try {
			sqlSession.batchSql( sqlList ).setTransactionSize( 10 ).execute();
		} catch( Exception e ) {
			NLogger.debug( e );
			throw e;
		}


	}

	/**
	 * expected null parameter can be binded at sql without parameter type configuration like VARCHAR or DATE.
	 */
	@Test
	public void case12_NullParamAtWhereClause() {

		case10_initTable();

		SqlSession session = getSession();

		String sql = StringUtil.format( "SELECT * FROM {} WHERE list_id = #{listId}", TABLE_NAME );

		NMap param = new NMap();
		param.put( "listId", null );

		NList list = session.sql( sql, param ).list().selectNList();

		NLogger.debug( list );

		assertEquals( list.size(), 0 );

	}

}