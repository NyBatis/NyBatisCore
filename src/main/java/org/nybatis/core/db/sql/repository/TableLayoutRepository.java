package org.nybatis.core.db.sql.repository;

import org.nybatis.core.db.sql.reader.table.TableLayout;
import org.nybatis.core.db.sql.reader.table.TableLayoutReader;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.log.NLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * TableLayout Repository to handle ORM
 *
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class TableLayoutRepository {

    private static Map<String, TableLayout> tableLayoutRepository    = new HashMap<>();
    private static Map<String, Boolean>     tableCreationPossibility = new HashMap<>();

    private static Object lock = new Object();

    public static boolean isEnableToCreateTable( String enviornmentId ) {
        return tableCreationPossibility.getOrDefault( enviornmentId, false );
    }

    public static void setEnableToCreateTable( String environmentId, boolean possibility ) {
        synchronized( lock ) {
            tableCreationPossibility.put( environmentId, possibility );
        }
    }

    public static boolean isExist( String environmentId, String tableName ) {
        return tableLayoutRepository.containsKey( getKey( environmentId, tableName ) );
    }

    private static String getKey( String environmentId, String tableName ) {
        return String.format( ".%s::%s", environmentId, tableName );
    }

    public static TableLayout getLayout( String environmentId, String tableName ) {

        String key = getKey( environmentId, tableName );

        synchronized( lock ) {
            if( ! tableLayoutRepository.containsKey( key ) ) {
                TableLayout tableLayout = new TableLayoutReader().getTableLayout( environmentId, tableName );
                if( ! tableLayout.isEmpty() ) {
                    tableLayoutRepository.put( key, tableLayout );
                    NLogger.debug( "Table Layout Loaded in Nybatis. (environmentId:{}, tableName:{})", environmentId, tableName );
                }
            }
        }

        TableLayout layout = tableLayoutRepository.get( key );

        if( layout == null ) {
            throw new SqlConfigurationException( "Fail to find table layout. (environmentId:{}, tableName:{})", environmentId, tableName );
        }

        return layout;

    }

}
