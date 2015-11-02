package org.nybatis.core.db.sql.bean;

import org.nybatis.core.db.sql.reader.SqlReader;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.log.NLogger;
import org.testng.annotations.Test;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-15
 */
public class SqlReaderTest {

    @Test
    public void test() {

        String sql = "SELECT 1 FROM DUAL";

        SqlReader reader = new SqlReader();

        SqlNode sqlNode = reader.read( sql );

        NLogger.debug( sqlNode );

    }

}