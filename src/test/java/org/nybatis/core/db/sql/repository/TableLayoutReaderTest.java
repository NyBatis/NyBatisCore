package org.nybatis.core.db.sql.repository;

import java.sql.DatabaseMetaData;
import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.configuration.builder.EnvironmentBuilder;
import org.nybatis.core.db.sql.orm.reader.TableLayoutReader;
import org.nybatis.core.log.NLogger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class TableLayoutReaderTest {

    @BeforeClass
    public void init() {
        DatabaseConfigurator.build();
    }

    @Test
    public void getTableLayout() throws Throwable {

        new EnvironmentBuilder( "oracle" ).setJdbcDatasource(
            "oracle.jdbc.driver.OracleDriver",
            "jdbc:oracle:thin:@xxx.xxx.xxx.xxx:1521:STGSP",
            "",
            ""
        );

        TableLayoutReader tableLayoutReader = new TableLayoutReader();

        NLogger.debug( tableLayoutReader.getTableLayout( "oracle", "TB_DP_LIST_PROD" ) );

//        NLogger.debug( tableLayoutReader.getTableLayout( "storeDev", "TB_DEV_SQL_HIS" ) );
//        NLogger.debug( tableLayoutReader.getTableLayout( "sqlite",   "PROD" ) );

        DatabaseMetaData metaData = null;

    }

}