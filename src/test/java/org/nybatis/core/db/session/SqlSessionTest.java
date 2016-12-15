package org.nybatis.core.db.session;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.cache.CacheManager;
import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.db.session.handler.ConnectionHandler;
import org.nybatis.core.db.session.type.vo.Param;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.sql.ListExecutor;
import org.nybatis.core.db.session.type.sql.SessionExecutor;
import org.nybatis.core.db.session.type.vo.ResultVo;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StopWatcher;
import org.nybatis.core.util.StringUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class SqlSessionTest {

	@BeforeClass
	public void beforeTest() {

	    DatabaseConfigurator.build();

//	    new ConfigurationBuilder( Const.path.getConfigDatabase() + "/config.xml" );

		// define specific mapper like this
//		TypeMapper.put( "sqlite", SqlType.BLOB, new ByteArrayMapper() );

//	    NLogger.debug( new SqlRepository().toString() );

	}

	@Test
	public void printSqlRepository() {
		NLogger.debug( new SqlRepository().toString() );
	}

	@Test
	public void envQA() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap param = new NMap();

		param.put( "tenantId",  "S01" );
		param.put( "rowNum",    "10"  );
		param.put( "orderKey", "list_id" );

		try {

			StopWatcher watcher = new StopWatcher();

			List<NMap> list = sqlSession.sqlId( "connectionTest.dynamic", param ).list().select();

			NLogger.debug( "End {}ms", watcher.elapsedMiliSeconds() );

			NLogger.debug( list );

		} catch( SqlException e ) {
			NLogger.error( e );
		}

	}

	@Test
	public void envLocal() {

	    StopWatcher watcher = new StopWatcher();

	    SqlSession sqlSession = SessionManager.openSession();

	    NMap param = new NMap();

	    param.put( "listId",    "S01" );
	    param.put( "rowNum",    "10"  );
	    param.put( "orderKey",  "list_id"  );

	    try {

	        List<NMap> list = sqlSession.sqlId( "Sqlite.select", param ).list().select();

	        NLogger.debug( "End {}ms", watcher.elapsedMiliSeconds() );

	        watcher.reset();

	        list = sqlSession.sqlId( "Sqlite.select", param ).list().select();

	        NLogger.debug( "End {}ms", watcher.elapsedMiliSeconds() );

	        watcher.reset();

	        list = sqlSession.sqlId( "Sqlite.select", param ).list().select();

	        NLogger.debug( "End {}ms", watcher.elapsedMiliSeconds() );

	        watcher.reset();

	        list = sqlSession.sqlId( "Sqlite.select", param ).list().select();

	        NLogger.debug( "End {}ms", watcher.elapsedMiliSeconds() );

	        NLogger.debug( list );

	    } catch( SqlException e ) {
	        NLogger.error( e );
	    }

	}

	@Test
	public void parameterSetting() {

		SessionManager.setGlobalParameter( "_globalSession_", "nayasis" );

		SqlSession sqlSession = SessionManager.openSession();

	    NMap param = new NMap();

	    param.put( "listId",    "S01" );
	    param.put( "prodId",    "10"  );
	    param.put( "price",     20    );

	    Param voParam = new Param( "S01", "10", 20 );

	    List<NMap> list = null;

	    try {

	        list = sqlSession.sqlId( "Sqlite.selectForParamTest", param ).list().select();
	        NLogger.debug( list );

	        list = sqlSession.sqlId( "Sqlite.selectForParamTest", voParam ).list().select();
	        NLogger.debug( list );

	        list = sqlSession.sqlId( "Sqlite.selectForParamTest", "10" ).list().select();
	        NLogger.debug( list );

	    } catch( SqlException e ) {
	        NLogger.error( e );
	    }

	}

	@Test
	public void select() {

		SqlSession sqlSession = SessionManager.openSession();

		Param param = new Param( "A002" );

		try {

			List<ResultVo> list = sqlSession.sqlId( "Sqlite.selectForList", param ).list().select( ResultVo.class );
			NLogger.debug( list );

			List<String> list02 = sqlSession.sqlId( "Sqlite.selectForList", param).list().select( String.class );
			NLogger.debug( list02 );

			List<NMap> list03 = sqlSession.sqlId( "Sqlite.selectForList", param ).list().select();
			NLogger.debug( list03 );

			NList list04 = sqlSession.sqlId( "Sqlite.selectForList", param ).list().selectNList();
			NLogger.debug( list04 );

			ResultVo row01 = sqlSession.sqlId( "Sqlite.selectForList", param ).select( ResultVo.class );
			NLogger.debug( row01 );

			String row02 = sqlSession.sqlId( "Sqlite.selectForList", param ).select( String.class );
			NLogger.debug( row02 );

			NMap row03 = sqlSession.sqlId( "Sqlite.selectForList", param ).select();
			NLogger.debug( row03.toDebugString() );

		} catch( SqlException e ) {
			NLogger.error( e );
		}

	}

	@Test
	public void globalParameterTest() {

		SqlSession sqlSession = SessionManager.openSession();

		NLogger.debug( sqlSession.sqlId( "Sqlite.selectForList", new Param("A001") ) );

		GlobalSqlParameter.setEnvironmentId( "sqlite03" );

		try {
			NLogger.debug( sqlSession.sqlId( "Sqlite.selectForList", new Param("A001") ) );
		} catch( SqlException e ) {
			return;
		}

		fail( "No Exception raised" );

	}

	@Test
	public void cacheTest() {

		NLogger.debug( new CacheManager() );

		SqlSession sqlSession = SessionManager.openSession();

		ListExecutor listExecutor = sqlSession.sqlId( "Sqlite.selectForList", "RNK00001" ).list();

		for( int i = 0; i < 10; i++ ) {

			Param param = new Param( "A001" );

			if( i == 3 ) {
				listExecutor.clearCache();
			}

			if( i == 8 ) {
				listExecutor.disableCache();
			}

			List<ResultVo> list = listExecutor.select( ResultVo.class );
			NLogger.debug( "index : {} ************************************\n{}", i, list.size() );

		}

	}

	@Test
	public void selectWithoutParam() {

		SqlSession sqlSession = SessionManager.openSession();

		try {

			List<ResultVo> list = sqlSession.sqlId( "Sqlite.selectWithoutParam" ).list().select( ResultVo.class );
			NLogger.debug( list );

		} catch( SqlConfigurationException | SqlException e ) {
			NLogger.error( e );
		}

	}

	@Test
	public void selectKeyForMapParam() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap param = new NMap();

		try {

			List<ResultVo> list = sqlSession.sqlId( "Sqlite.selectKey" , param ).list().select( ResultVo.class );
			NLogger.debug( list );
			NLogger.debug( param );

			assertEquals( param.get( "listId" ),   "A001"     );
			assertEquals( param.get( "rowNum" ),   4          );
			assertEquals( param.get( "keyValue" ), "keyValue" );

		} catch( SqlConfigurationException | SqlException e ) {
			NLogger.error( e );
		}

	}

    @Test
	public void selectKeyForBeanParam() {

		SqlSession sqlSession = SessionManager.openSession();

		Param param = new Param();

		try {

			List<ResultVo> list = sqlSession.sqlId( "Sqlite.selectKey", param ).list().select( ResultVo.class );
			NLogger.debug( list );
			NLogger.debug( param );

			assertEquals( param.listId, "A001" );
			assertEquals( param.rowNum, 4.0, 0 );
			assertEquals( param.keyValue, "keyValue" );


		} catch( SqlConfigurationException | SqlException e ) {
			NLogger.error( e );
		}

	}

	@Test
	public void annotationTest() {

		SqlSession sqlSession = SessionManager.openSession();

		try {

			List<ResultVo> list = sqlSession.sqlId( "Sqlite.selectWithoutParam" ).list().select( ResultVo.class );
			NLogger.debug( list );

		} catch( SqlConfigurationException | SqlException e ) {
			NLogger.error( e );
		}

		try {

			List<ResultVo> list = sqlSession.sqlId( "Sqlite.selectWithoutParam" ).list().select( ResultVo.class );
			NLogger.debug( list );

		} catch( SqlConfigurationException | SqlException e ) {
			NLogger.error( e );
		}

	}

	@Test
	public void callable() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap param = new NMap();

		param.put( "tenantId", "S01"          );
		param.put( "listId",   "TAR000000759" );
		param.put( "listNm",   null );

		try {

//			sqlSession.procedure( "OracleTest.procedure01", param );
//			sqlSession.call( "OracleTest.procedure02", param );
//			sqlSession.call( "OracleTest.function01", param );

			SessionExecutor sessionExecutor = sqlSession.sqlId( "OracleTest.function01", param );

			NLogger.debug( sessionExecutor.call() );
			NLogger.debug( sessionExecutor.call( List.class ) );
			NLogger.debug( sessionExecutor.call( List.class, ResultVo.class ) );
			NLogger.debug( sessionExecutor.call( List.class, String.class ) );

		} catch( SqlException e ) {
			NLogger.error( e );
		}

	}

	@Test
	public void insert() {

		SqlSession sqlSession = SessionManager.openSession();

		Param param = new Param( "A002" );

		try {

			for( int i = 0; i < 10; i++ ) {
				param.prodId = StringUtil.lpad( i, 5, '0' );
				param.price  = i * 1000;

				NLogger.debug( "affected count : {}", sqlSession.sqlId( "Sqlite.insert", param ).execute() );

			}

		} catch( SqlException e ) {
			NLogger.error( e );
		}

		sqlSession.rollback();

	}

	@Test
	public void delete() {

		SqlSession sqlSession = SessionManager.openSession();

		Param param = new Param( "A002" );

		try {

			NLogger.debug( "affected count : {}", sqlSession.sqlId( "Sqlite.delete", param ).execute() );

		} catch( SqlException e ) {
			NLogger.error( e );
		}

		sqlSession.commit();


	}

	@Test( expectedExceptions = SqlException.class )
	public void insertBatchPreparedStatement() {

		SqlSession sqlSession = SessionManager.openSession();

		List<Param> paramList = new ArrayList<>();

		try {

			paramList.add( new Param( "A002", "00001", 1500 ) );
			paramList.add( new Param( "A002", "00002", 2500 ) );
			paramList.add( new Param( "A002", "00003", 3500 ) );

			paramList.add( new Param( "A002", "00004", 4500 ) );
			paramList.add( new Param( "A002", "00005", 5500 ) );
			paramList.add( new Param( "A002", "00006", 6500 ) );

			paramList.add( new Param( "A002", "00007", 7500 ) );
			paramList.add( new Param( "A002", "00008", 8500 ) );
			paramList.add( new Param( "A002", "00009", 9500 ) );

			paramList.add( new Param( "A002", "00010", 1000 ) );
			paramList.add( new Param( "A002", "00011", 1000 ) );
			paramList.add( new Param( "A002", "00012", 1000 ) );

			paramList.add( new Param("A002", "00010", 1000) ); // Duplicated Data

			sqlSession.batchSqlId( "Sqlite.insert", paramList ).execute();

//			sqlSession.commit();
		} catch( Exception e ) {
			NLogger.error( e );
		} finally {
			sqlSession.rollback();
		}

	}

	@Test( expectedExceptions = SqlException.class )
	public void insertBatchStatement() {

		SqlSession sqlSession = SessionManager.openSession();

		NLogger.debug( "root : {}", Const.path.getRoot() );

		List<String> sqlList = new ArrayList<>();

		try {

			for( int i = 0; i < 10; i++ ) {
				sqlList.add( String.format("INSERT INTO PROD( list_id, prod_id, price ) VALUES ( 'A007', '%d', 1000 )", i) );
			}

			sqlList.add( String.format("INSERT INTO PROD_ERR( list_id, prod_id, price ) VALUES ( 'A007', '%d', 1000 )", 0) );

			sqlSession.batchSql( sqlList ).execute();

		} catch( SqlException e ) {
			NLogger.error( e );
			throw e;
		} finally {
			sqlSession.rollback();
		}

	}



	@Test
	public void getConnection() {

		SqlSession sqlSession = SessionManager.openSession();

		String sql01 = "SELECT * FROM PROD";
		String sql02 = "SELECT * FROM PROD where prod = '1'";


		try {

			sqlSession.useConnection( new ConnectionHandler() {
				public void execute( Connection connection ) throws Throwable {

					NLogger.debug( ">> PreparedStatement" );

					PreparedStatement pstmt = connection.prepareStatement( sql01 );

					ResultSet rs = pstmt.executeQuery();

					while( rs.next() ) {
						NLogger.debug( "\t{}, {}, {}", rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ) );
					}

					rs = pstmt.executeQuery();

					while( rs.next() ) {
						NLogger.debug( "\t{}, {}, {}", rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ) );
					}

					rs = pstmt.executeQuery();

					while( rs.next() ) {
						NLogger.debug( "\t{}, {}, {}", rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ) );
					}

					NLogger.debug( ">> Statement" );

					Statement stmt = connection.createStatement();

					rs = stmt.executeQuery( sql01 );

					while( rs.next() ) {
						NLogger.debug( "\t{}, {}, {}", rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ) );
					}

					rs = stmt.executeQuery( sql02 );

					while( rs.next() ) {
						NLogger.debug( "\t{}, {}, {}", rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ) );
					}

					NLogger.debug( ">> CallableStatement" );

					CallableStatement cstmt = connection.prepareCall( sql01 );

					rs = cstmt.executeQuery( sql01 );

					while( rs.next() ) {
						NLogger.debug( "\t{}, {}, {}", rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ) );
					}
				}
			} );

		} catch( Throwable e ) {
	        e.printStackTrace();
        }

	}

	@Test
	public void unpooledDatasource() {

		SessionManager.setCompulsiveEnvironment( "sqliteUnpooled" );

		select();

	}

	@Test
	public void arrayBind() {

		SqlSession sqlSession = SessionManager.openSession( "oracle" );

		NMap param = new NMap();

		param.put( "listId", Arrays.asList( "TAR000000759", "TAR000000758" ) );

		NList list = sqlSession.sql( "SELECT * FROM TB_DP_LIST WHERE LIST_ID IN ( #{listId} )", param ).list().selectNList();

		NLogger.debug( list );

	}

}