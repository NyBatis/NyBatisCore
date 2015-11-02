package org.nybatis.core.db.sql.repository;

import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.log.NLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class TableLayoutRepository {

    private static Map<String, TableLayout> tableLayoutRepository = new HashMap<>();

    private static Object lock = new Object();

    public static boolean isExist( String environmentId, String tableName ) {
        return tableLayoutRepository.containsKey( getKey( environmentId, tableName ) );
    }

    private static String getKey( String environmentId, String tableName ) {
        return environmentId + "::" + tableName;
    }

    public static TableLayout getLayout( String environmentId, String tableName ) {

        String key = getKey( environmentId, tableName );

        synchronized( lock ) {
            if( ! tableLayoutRepository.containsKey( key ) ) {
                TableLayout tableLayout = new TableLayoutReader().getTableLayout( environmentId, tableName );
                tableLayoutRepository.put( key, tableLayout );
                NLogger.debug( "Table Layout Loaded in Nybatis. (environmentId:{}, tableName:{})", environmentId, tableName );
            }
        }

        TableLayout layout = tableLayoutRepository.get( key );

        if( layout.isEmpty() ) {
            throw new SqlConfigurationException( "Fail to find table layout. (environmentId:{}, tableName:{})", environmentId, tableName );
        }

        return layout;

    }

}
