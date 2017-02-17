package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.SqlSessionSqliteTest;
import org.nybatis.core.db.sql.reader.DbTableReaderTest;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-16
 */
public class OrmSessionTest {

    private final String ENVIRONMENT_ID = SqlSessionSqliteTest.ENVIRONMENT_ID;
    private final String TABLE_NAME     = SqlSessionSqliteTest.TABLE_NAME;

    @BeforeClass
    public void beforeTest() {
        DatabaseConfigurator.build();
        initTable();
    }

    @AfterMethod
    public void release() {
        TransactionManager.end();
    }

    private void initTable() {
        new SqlSessionSqliteTest().case10_initDummyDataByPreparedStatementBatch();
    }

    @Test( expectedExceptions = SqlConfigurationException.class )
    public void constructErrorCheck_01_Basic() {
        SessionManager.openOrmSession( null, null );
    }

    @Test( expectedExceptions = SqlConfigurationException.class )
    public void constructErrorCheck_02_TableNameIsNotValid() {
        SessionManager.openOrmSession( "InvalidTable", NMap.class );
    }

    private void isOrmSqlExist() {
        Assert.assertTrue( SqlRepository.isExist( sqlIdPrefix() + Const.db.ORM_SQL_INSERT_PK ) );
    }

    @Test
    public void constructByName() {
        SessionManager.openOrmSession( ENVIRONMENT_ID, TABLE_NAME, NMap.class );
        isOrmSqlExist();
    }

    @Test
    public void constructByClass() {

        SessionManager.openOrmSession( TestDomain.class );

        isOrmSqlExist();

        printLoadedOrmSql();

    }

    @Test
    public void testForEntity() {

        OrmSession<TestDomain> ormSession = SessionManager.openOrmSession( TestDomain.class );

        TestDomain testDomain = new TestDomain();
        testDomain.setListId( "RNK00000" );
        testDomain.setProdId( "70" );

        testDomain = ormSession.select( testDomain );

        NLogger.debug( testDomain );
        assertEquals( "PROD-70", testDomain.getProdName() );

        testDomain.setPrice( 3000 );
        testDomain.setProdName( "MERONG" );

        ormSession.update( testDomain );

        testDomain = ormSession.select( testDomain );
        NLogger.debug( ">> before rollback :\n{}", testDomain );
        assertEquals( "MERONG", testDomain.getProdName() );

        ormSession.rollback();

        testDomain = ormSession.select( testDomain );
        NLogger.debug( ">> after rollback :\n{}", testDomain );
        assertEquals( "PROD-70", testDomain.getProdName() );

        ormSession.delete( testDomain );

        testDomain = ormSession.select( testDomain );
        NLogger.debug( ">> after delete :\n{}", testDomain );
        assertEquals( null, testDomain.getListId() );
        assertEquals( null, testDomain.getProdId() );

        ormSession.rollback();

        List<TestDomain> select = ormSession.list().select();

        NLogger.debug( ">> list\n{}", new NList( select ).toDebugString() );

    }

    @Test
    public void testForBatch() {

        OrmSession<TestDomain> ormSession = SessionManager.openOrmSession( TestDomain.class );

        List<TestDomain> entities = new ArrayList<>();

        for( int i = 0; i < 100; i++ ) {

            TestDomain testDomain = new TestDomain();

            testDomain.setListId( "TEST" );
            testDomain.setProdId( "PROD_" + i );
            testDomain.setProdName( testDomain.getProdId() );
            testDomain.setPrice( i );

            entities.add( testDomain );

        }

        ormSession.batch().insert( entities );


        TestDomain param = new TestDomain();
        param.setListId( "TEST" );

        List<TestDomain> select = ormSession.list().select( param );

        NLogger.debug( ">> list\n{}", new NList( select ).toDebugString() );

        ormSession.rollback();

    }

    @Test
    public void testForTableName() {

        OrmSession<TestDomain> ormSession = SessionManager.openOrmSession( TABLE_NAME, TestDomain.class );

        TestDomain testDomain = new TestDomain();
        testDomain.setListId( "RNK00000" );
        testDomain.setProdId( "70" );

        testDomain = ormSession.select( testDomain );

        NLogger.debug( testDomain );
        assertEquals( "PROD-70", testDomain.getProdName() );

        testDomain.setPrice( 3000 );
        testDomain.setProdName( "MERONG" );

        ormSession.update( testDomain );

        testDomain = ormSession.select( testDomain );
        NLogger.debug( ">> before rollback :\n{}", testDomain );
        assertEquals( "MERONG", testDomain.getProdName() );

        ormSession.rollback();

        testDomain = ormSession.select( testDomain );
        NLogger.debug( ">> after rollback :\n{}", testDomain );
        assertEquals( "PROD-70", testDomain.getProdName() );

        ormSession.delete( testDomain );

        testDomain = ormSession.select( testDomain );
        NLogger.debug( ">> after delete :\n{}", testDomain );
        assertEquals( null, testDomain.getListId() );
        assertEquals( null, testDomain.getProdId() );

        ormSession.rollback();

        List<TestDomain> select = ormSession.list().select();

        NLogger.debug( ">> list\n{}", new NList( select ).toDebugString() );

    }

    @Test
    public void setTableNameByClassName() {

        initTable();

        OrmSession<NybatisTest> ormSession = SessionManager.openOrmSession( NybatisTest.class );

        NybatisTest domain = new NybatisTest();
        domain.setListId( "RNK00000" );
        domain.setProdId( "70" );

        domain = ormSession.select( domain );

        NLogger.debug( domain );

        assertEquals( "PROD-70", domain.getProdName() );


        domain = new NybatisTest();
        domain.setListId( "RNK00000" );
        List<NybatisTest> list = ormSession.list().setPage( 5, 10 ).orderBy( "prod_id" ).select( domain );

        assertEquals( 5, list.size() );

        for( NybatisTest row : list ) {
            NLogger.debug( row );
        }

    }

    private NybatisTest getData( String listId, String prodId ) {

        OrmSession<NybatisTest> ormSession = SessionManager.openOrmSession( NybatisTest.class );

        NybatisTest domain = new NybatisTest();
        domain.setListId( listId );
        domain.setProdId( prodId );

        return ormSession.select( domain );

    }

    private void printLoadedOrmSql() {
        new DbTableReaderTest().printLoadedOrmSql( ENVIRONMENT_ID, TABLE_NAME );
    }

    public String sqlIdPrefix() {
        return Const.db.getOrmSqlIdPrefix( ENVIRONMENT_ID, TABLE_NAME );
    }

}