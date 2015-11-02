package org.nybatis.core.db.datasource.driver;

import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Driver Manager
 *
 * @author nayasis@gmail.com
 * @since 2015-10-29
 */
public class DriverManager {

    private static Map<String, DriverAttributes> driverRepository = new Hashtable<>();

    public static void add( DriverAttributes driverAttributes ) {
        driverRepository.put( driverAttributes.getDriverType(), driverAttributes );
    }

    public static DriverAttributes get( DataSource datasource ) {

        Connection  connection = null;

        try {

            connection = datasource.getConnection();

            return get( connection );

        } catch( SQLException e ) {
            throw new DatabaseConfigurationException( e );
        } finally {
            if( connection != null ) {
                try {
                    connection.close();
                } catch( SQLException e ) {}
            }
        }

    }


    private static DriverAttributes get( Connection connection ) {

        Connection realConnection = new Reflector().unwrapProxyBean( connection );

        String className = realConnection.getClass().getName();

        NLogger.trace( "--------------------------------------" );
        NLogger.trace( "Connection class name : {}", className );

        for( DriverAttributes previoutAttributes : driverRepository.values() ) {
            NLogger.trace( "--------------------------------------" );
            if( previoutAttributes.isMatched( className ) ) return previoutAttributes;
        }
        NLogger.trace( "--------------------------------------" );

        if( ! driverRepository.containsKey( className ) ) {
            add( new DriverAttributes( className, className ) );
        }

        return driverRepository.get( className );

    }

    public static DriverAttributes get( String driverType ) {
        return driverRepository.get( driverType );
    }

    public static boolean contains( String driverType ) {
        return driverRepository.containsKey( driverType );
    }

    static {

        add( new DriverAttributes("oracle", "oracle\\.jdbc\\.driver" )
            .setPageSqlPre( "SELECT * FROM ( SELECT ROWNUM AS nybatis_page_rownum, NYBATIS_PAGE_VIEW.* FROM (" )
            .setPageSqlPost( ") NYBATIS_PAGE_VIEW WHERE rownum <= #{end} ) WHERE nybatis_page_rownum >= #{start}" ) );

        add( new DriverAttributes( "mysql",  "com\\.mysql\\.jdbc" ) );
        add( new DriverAttributes( "maria",  "org\\.mariadb\\.jdbc" ) );
        add( new DriverAttributes( "sqlite", "org\\.sqlite\\." ) );

        // Not Tested !
        add( new DriverAttributes( "mssql",       "com\\.microsoft\\.jdbc" ) );
        add( new DriverAttributes( "postgresql",  "postgresql\\.driver" ) );
        add( new DriverAttributes( "sybase",      "com\\.sybase\\." ) );
        add( new DriverAttributes( "db2",         "ibm\\.db2\\." ) );
        add( new DriverAttributes( "odbc",        "sun\\.jdbc\\.odbc" ) );

    }

}
