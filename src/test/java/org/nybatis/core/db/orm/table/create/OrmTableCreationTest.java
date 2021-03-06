package org.nybatis.core.db.orm.table.create;

import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.orm.table.create.entity.Employee;
import org.nybatis.core.db.orm.table.create.entity.EmployeeModification;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
import org.nybatis.core.db.sql.orm.reader.TableLayoutReader;
import org.nybatis.core.db.sql.orm.sqlmaker.OrmTableSqlMaker;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.nybatis.core.db.datasource.driver.DatabaseName.SQLITE;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class OrmTableCreationTest {

    public static final String environmentId = "maria";

    @BeforeClass
    public void init() {
        DatabaseConfigurator.build( "/config/dbLogicTest/tableCreator/configTableCreation.xml");
    }

    @Test
    public void checkXmlConfiguration() {

        DatabaseAttribute attribute = DatasourceManager.getAttributes( environmentId );
        Assert.assertEquals( attribute.getDatabase(), environmentId );

        boolean enableToCreateTable = TableLayoutRepository.isEnableDDL( environmentId );
        Assert.assertEquals( enableToCreateTable, true );

    }

    @Test
    public void readEntityLayout() {

        Employee employee = new Employee( "key", "name", 10, 1000, "800523-2111023", "dummy", "thinFilm" );
        String json = Reflector.toJson( employee );
        System.out.println( json );

        TableLayout tableLayout = getSampleTableLayout("h2");
        System.out.println( tableLayout );

    }

    private TableLayout getSampleTableLayout( String envirionmentId ) {
        EntityLayoutReader reader = new EntityLayoutReader(envirionmentId);
        return reader.getTableLayout( EmployeeModification.class );
    }

    @Test
    public void readPreviousTableLayout() {

//        printTableLayout( "oracle", "TB_DEV_SQL" );
//        printTableLayout( "oracle", "TB_TABLE" );
//        printTableLayout( "h2",     "TB_TABLE" );
//        printTableLayout( "sqlite", "TB_TABLE" );
        printTableLayout( "maria", "TB_TABLE" );
    }

    private void printTableLayout( String envirionmentId, String tableName ) {

        TableLayout entityLayout = getSampleTableLayout( envirionmentId );

        TableLayoutReader reader = new TableLayoutReader();
        TableLayout tableLayout = reader.getTableLayout( envirionmentId, tableName );

        System.out.printf( ">> is equal : [%s]\n", entityLayout.isEqual(tableLayout) );
        System.out.println( entityLayout );
        System.out.println( tableLayout );

    }

    @Test
    public void creatSql() {

        TableLayout layout = getSampleTableLayout( environmentId );

        OrmTableSqlMaker sqlMaker = new OrmTableSqlMaker( environmentId );
        System.out.println( sqlMaker.sqlCreateTable(layout) );

    }

    @Test
    public void tableModificationTest() {
//        tableModificationTest( "h2" );
//        tableModificationTest( "sqlite" );
//        tableModificationTest( "oracle" );
        tableModificationTest( "maria" );
    }

    private Employee getSampleTuple() {
        Employee employee = new Employee();
        employee.setKey( "RANDOM_KEY" );
        employee.setLastName( "Jung" );
        employee.setDepartment( "Human Resources" );
        employee.setAge( 40 );
        employee.setIncome( 1000.674 );
        return employee;
    }

    @Test
    public void tableRecreationTest() {

        if( ! DatasourceManager.isExist( environmentId ) ) {
            NLogger.info( "environmentId({}) is not exist", environmentId );
            return;
        }

        OrmSession<Employee> session = SessionManager.openOrmSession( Employee.class );

        session.setEnvironmentId( environmentId );

        session.table().set();
        session.table().set();

    }

    private void tableModificationTest( String environmentId ) {

        if( ! DatasourceManager.isExist(environmentId) ) {
            NLogger.info( "environmentId({}) is not exist", environmentId );
            return;
        }

        OrmSession<Employee> session = SessionManager.openOrmSession( Employee.class );

        session.setEnvironmentId( environmentId );
        session.table().drop();
        session.table().set();

        session.insert( getSampleTuple() );
        session.commit();
        Assert.assertEquals( 1, session.list().count() );

        NLogger.debug( session.list().select() );

        // add and change column

        OrmSession<EmployeeModification> anotherSession = SessionManager.openOrmSession( EmployeeModification.class );

        anotherSession.setEnvironmentId( environmentId );
        anotherSession.table().set();

        TableLayout tableLayout = TableLayoutRepository.getLayout( environmentId, EmployeeModification.class );

        NLogger.debug( ">>> after second modification" );
        NLogger.debug( tableLayout );

        Assert.assertTrue( tableLayout.hasColumnName("key")        );
        Assert.assertTrue( tableLayout.hasColumnName("lastName")   );
        Assert.assertTrue( tableLayout.hasColumnName("age")        );
        Assert.assertTrue( tableLayout.hasColumnName("income")     );
        Assert.assertTrue( tableLayout.hasColumnName("id")         );
        Assert.assertTrue( tableLayout.hasColumnName("department") );
        Assert.assertTrue( tableLayout.hasColumnName("subKey")     );
        Assert.assertTrue( tableLayout.hasColumnName("birthDay")   );

        Assert.assertTrue( tableLayout.getColumn("key").isPk() );
        Assert.assertTrue( tableLayout.getColumn("subKey").isPk() );

        Assert.assertEquals( tableLayout.getColumn( "subKey" ).getDefaultValue(), "1" );

        if( session.isNotDatabase(SQLITE) ) {
            Assert.assertEquals( (int) tableLayout.getColumn( "income" ).getSize(), 21 );
        }

        insertDataToModifiedTable( anotherSession );

        // revert table layout
        session.table().drop();
        session.table().set();


    }

    private void insertDataToModifiedTable( OrmSession<EmployeeModification> anotherSession ) {

        EmployeeModification param01 = new EmployeeModification();
        param01.setKey( "1" );
        param01.setSubKey( "2" );
        param01.setLastName( "merong" );

        EmployeeModification param02 = new EmployeeModification();
        param02.setKey( "1" );
        param02.setSubKey( "1" );
        param02.setLastName( "merong" );

        anotherSession.insert( param01 );
        anotherSession.insert( param02 );

        EmployeeModification row = anotherSession.select( param01 );

        Assert.assertEquals( row.getSubKey(), param01.getSubKey() );

        NLogger.debug( row );

    }

}
