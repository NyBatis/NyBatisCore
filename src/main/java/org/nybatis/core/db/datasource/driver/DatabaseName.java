package org.nybatis.core.db.datasource.driver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-17
 */
public enum DatabaseName {

    NULL(    "",           ""                                      ),
    ORACLE(  "oracle",     "oracle\\.jdbc\\.driver"                ),
    MYSQL(   "mysql",      "com\\.mysql\\.jdbc"                    ),
    MARIA(   "maria",      "org\\.mariadb\\.jdbc"                  ),
    SQLITE(  "sqlite",     "org\\.sqlite\\."                       ),
    H2(      "h2",         "org\\.h2\\.jdbc"                       ),
    DERBY(   "derby",      "org\\.apache\\.derby\\.jdbc"           ),
    HSQL(    "hsql",       "org\\.hsqldb\\.jdbcDriver"             ),
    MSSQL(   "mssql",      "com\\.microsoft\\.(sqlserver\\.)?jdbc" ),
    POSTGRE( "postgre",    "postgresql\\.driver"                   ),
    SYBASE(  "sybase",     "com\\.sybase\\."                       ),
    DB2(     "db2",        "ibm\\.db2\\."                          ),
    ODBC(    "odbc",       "sun\\.jdbc\\.odbc"                     )
    ;

    public final String name;
    public final String driverNamePattern;

    private static Map<String,DatabaseName> names = new LinkedHashMap<>();

    static {
        for( DatabaseName db : DatabaseName.values() ) {
            names.put( db.name, db );
        }
    }

    DatabaseName( String name, String driverNamePattern ) {
        this.name = name;
        this.driverNamePattern = driverNamePattern;
    }

    public static DatabaseName get( String name )  {
        if( name == null ) return null;
        return names.get( name.toLowerCase() );
    }

}
