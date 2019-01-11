package org.nybatis.core.db.session;

import java.util.List;

import org.nybatis.core.conf.Const;
import org.nybatis.core.context.NThreadLocal;
import org.nybatis.core.db.configuration.builder.ConfigurationBuilder;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.session.type.vo.ResultVo;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StopWatch;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SqlSessionMariaTest {

	@BeforeClass
	public void beforeTest() {

	    StopWatch watcher = new StopWatch();

	    new ConfigurationBuilder().readFrom( Const.path.getConfigDatabase() + "/config.xml" );

	    NLogger.debug( "Configuration Read End : {}ms", watcher.elapsedMiliSeconds() );
//	    NLogger.debug( new SqlRepository().toString() );

	}

	@Test ( enabled = false )
	public void mariaSelect() {

		SqlSession sqlSession = SessionManager.openSession();

		try {

			SessionManager.setGlobalParameter( "userId", "merong" );

			NList result = sqlSession.sqlId( "MariaTest.selectActor" ).list().selectNList();

			@SuppressWarnings( "unused" )
            List<ResultVo> selectList = sqlSession.sqlId( "SELECT * FROM TABLE" ).list().select( ResultVo.class );

			NLogger.debug( result );

			NThreadLocal.clear();

		} catch( Exception e ) {
			NLogger.debug( e );
			throw e;
		}

	}

	@Test ( enabled = false )
	public void procedure01() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap result = sqlSession.sqlId( "MariaTest.procedure01" ).call();

		NLogger.debug( result );

	}

	@Test ( enabled = false )
	public void procedure02() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap result = sqlSession.sqlId( "MariaTest.procedure02" ).call();

		NLogger.debug( result.toDebugString() );

	}

	@Test ( enabled = false )
	public void procedure03() {

		SqlSession sqlSession = SessionManager.openSession();

		NMap result = sqlSession.sqlId( "MariaTest.procedure03" ).call();

		NLogger.debug( result.toDebugString() );

	}

}