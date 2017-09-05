package org.nybatis.core.db.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.testng.annotations.Test;

/**
 * @author nayasis@onestorecorp.com
 * @since 2017-09-05
 */
public class DerbyTest {

    private void setDBSystemDir() {

        // Decide on the db system directory: /.addressbook/
        String userHomeDir = System.getProperty("user.home", ".");
//        String systemDir = userHomeDir + "/.addressbook";

        // Set the db system directory.
        System.setProperty("derby.system.home", userHomeDir );

    }

    @Test
    public void test() throws SQLException {

//        setDBSystemDir();

        Connection dbConnection = null;
        String strUrl = "jdbc:derby:SampleDb;create=true";

        try {
            dbConnection = DriverManager.getConnection(strUrl);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }

    }


}
