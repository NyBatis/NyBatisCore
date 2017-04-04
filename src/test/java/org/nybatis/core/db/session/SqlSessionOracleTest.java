package org.nybatis.core.db.session;

import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.NList;
import org.nybatis.core.util.StopWatcher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SqlSessionOracleTest {

	@BeforeClass
	public void beforeTest() {

	    StopWatcher watcher = new StopWatcher();

	    DatabaseConfigurator.build();

	    NLogger.debug( "Configuration Read End : {}ms", watcher.elapsedMiliSeconds() );

	}

	@Test ( enabled = false )
	public void select() {

		SqlSession sqlSession = SessionManager.openSession();

		NList result = sqlSession.sqlId( "OracleTest.select" ).list().selectNList();
		NLogger.debug( result );

	}

	@Test ( enabled = false )
	public void selectCursor() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap result = sqlSession.sqlId( "OracleTest.selectCursor" ).select();
		NLogger.debug( result );

	}

	@Test ( enabled = false )
	public void procedure01() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap param = new NMap();

		param.put( "tenantId", "S01"          );
		param.put( "listId",   "TAR000000759" );
		param.put( "listNm",   null );

		NMap result = sqlSession.sqlId( "OracleTest.procedure01", param ).call();

		NLogger.debug( result );

	}

	@Test ( enabled = false )
	public void procedure02() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap result = sqlSession.sqlId( "OracleTest.procedure02" ).call();

		NLogger.debug( result.toDebugString() );

	}

	@Test ( enabled = false )
	public void function01() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap result = sqlSession.sqlId( "OracleTest.function01" ).call();

		NLogger.debug( result.toDebugString() );

	}

	@Test( expectedExceptions = SqlException.class )
	public void function01_NoOutReturn() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap result = sqlSession.sqlId( "OracleTest.function01-NoOutReturn" ).call();

		NLogger.debug( result.toDebugString() );

	}

	@Test ( enabled = false )
	public void pageSql() {

		SqlSession sqlSession = SessionManager.openSession();

//		NList result01 = sqlSession.selectTable( "OracleTest.paged" );
//
//		NLogger.debug( "Done !!" );

//		NLogger.debug( result01 );
		NLogger.debug( sqlSession.sqlId( "OracleTest.paged").list().setPage( 20, 30 ).selectNList() );

	}

}