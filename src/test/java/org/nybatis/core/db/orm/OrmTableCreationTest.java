package org.nybatis.core.db.orm;

import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.orm.entity.Employee;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
import org.nybatis.core.db.sql.orm.reader.TableLayoutReader;
import org.nybatis.core.db.sql.orm.sqlmaker.OrmTableSqlMaker;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.reflection.Reflector;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class OrmTableCreationTest {

    public static final String envirionmentId = "h2";

    @BeforeClass
    public void init() {
        DatabaseConfigurator.build( "/config/dbLogicTest/tableCreator/configTableCreation.xml");
    }

    @Test
    public void checkXmlConfiguration() {

        DatabaseAttribute attribute = DatasourceManager.getAttributes( "h2" );
        Assert.assertEquals( attribute.getDatabase(), envirionmentId );

        boolean enableToCreateTable = TableLayoutRepository.isEnableToCreateTable( envirionmentId );
        Assert.assertEquals( enableToCreateTable, true );

    }

    @Test
    public void readEntityLayout() {

        Employee employee = new Employee( "key", "name", 10, 1000, "800523-2111023", "dummy", "thinFilm" );
        String json = Reflector.toJson( employee );
        System.out.println( json );

        TableLayout tableLayout = getSampleTableLayout();
        System.out.println( tableLayout );

    }

    private TableLayout getSampleTableLayout() {
        EntityLayoutReader reader = new EntityLayoutReader();
        return reader.getTableLayout( Employee.class );
    }

    @Test
    public void readPreviousTableLayout() {
        printTableLayout( "oracle",    "TB_DEV_SQL" );
//        printTableLayout( "callChain", "TB_SQL" );
    }

    private void printTableLayout( String envirionmentId, String tableName ) {
        TableLayoutReader reader = new TableLayoutReader();
        TableLayout tableLayout = reader.getTableLayout( envirionmentId, tableName );
        System.out.println( tableLayout );
    }

    @Test
    public void creatSql() {

        TableLayout layout = getSampleTableLayout();

        OrmTableSqlMaker sqlMaker = new OrmTableSqlMaker( envirionmentId );
        System.out.println( sqlMaker.sqlCreateTable(layout) );

    }

}
