package org.nybatis.core.db.session;

import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.session.handler.ConnectionHandler;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.log.NLogger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class OrmSqlSessionTest {

	@BeforeClass
	public void beforeTest() {
	    DatabaseConfigurator.build();
	}

	@Test
	public void testSqlite() throws Throwable {

		SqlSession sqlSession = SessionManager.openSession();

		sqlSession.useConnection( new ConnectionHandler() {
			public void execute( Connection connection ) throws Throwable {

				Statement statement = connection.createStatement();

				ResultSet rs = statement.executeQuery( "SELECT * FROM PROD" );

				NLogger.debug( toList( rs ) );

				ResultSetMetaData rsMeta = rs.getMetaData();

				DatabaseMetaData metaData = connection.getMetaData();

				String tableName = "PROD";

				NLogger.debug( toList( metaData.getPrimaryKeys( null, null, tableName ) ) );
				NLogger.debug( toList( metaData.getColumns( null, null, tableName, null ) ) );

			}
		} );

	}

	@Test
	public void testOracle() throws Throwable {

		SqlSession sqlSession = SessionManager.openSession( "storeDev" );

		sqlSession.useConnection( new ConnectionHandler() {
			public void execute( Connection connection ) throws Throwable {

				DatabaseMetaData metaData = connection.getMetaData();

				String tableName = "TB_DEV_SQL_HIS";

				NLogger.debug( toList( metaData.getPrimaryKeys( null, null, tableName ) ) );
				NLogger.debug( toList( metaData.getColumns( null, null, tableName, null ) ) );


			}
		} );

	}

}