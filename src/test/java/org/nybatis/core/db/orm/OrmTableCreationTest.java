package org.nybatis.core.db.orm;

import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.orm.entity.Employee;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
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


        EntityLayoutReader reader = new EntityLayoutReader();
        TableLayout tableLayout = reader.getTableLayout( Employee.class );

        System.out.println( tableLayout );

    }

}
