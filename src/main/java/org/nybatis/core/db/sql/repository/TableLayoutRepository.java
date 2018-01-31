package org.nybatis.core.db.sql.repository;

import java.util.HashMap;
import java.util.Map;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
import org.nybatis.core.db.sql.orm.reader.TableLayoutReader;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;

/**
 * TableLayout Repository to handle ORM
 *
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class TableLayoutRepository {

    private static Map<String, TableLayout> tableLayoutRepository  = new HashMap<>();
    private static Map<String, Boolean>     ddlEnable              = new HashMap<>();
    private static Map<String, Boolean>     ddlRecreation          = new HashMap<>();

    private static Object lock = new Object();

    /**
     * check enable to DDL with entity
     *
     * @param environmentId environment id
     * @return true if DDL is enable.
     */
    public static boolean isEnableDDL( String environmentId ) {
        return ddlEnable.getOrDefault( environmentId, false );
    }

    /**
     * set enable to DDL with entity
     *
     * @param environmentId environment id
     * @param enable        flag
     */
    public static void setEnableDDL( String environmentId, boolean enable ) {
        synchronized( lock ) {
            ddlEnable.put( environmentId, enable );
        }
    }

    public static boolean isRecreationDDL( String environmentId ) {
        return ddlRecreation.getOrDefault( environmentId, false );
    }

    public static void setRecreationDDL( String environmentId, boolean enable ) {
        synchronized( lock ) {
            ddlRecreation.put( environmentId, enable );
        }
    }

    /**
     * check entity exists as TABLE in database
     *
     * @param environmentId environment id
     * @param tableName     DB table name
     * @return true if entity exists as table
     */
    public static boolean isExist( String environmentId, String tableName ) {
        return tableLayoutRepository.containsKey( getKey( environmentId, tableName ) );
    }

    /**
     * check entity exists as TABLE in database
     *
     * @param environmentId environment id
     * @param domainClass   table entity class
     * @return true if entity exists as table
     */
    public static boolean isExist( String environmentId, Class domainClass ) {
        return isExist( environmentId, EntityLayoutReader.getTableName(domainClass) );
    }

    private static String getKey( String environmentId, String tableName ) {
        return String.format( ".%s::%s", environmentId, tableName );
    }

    /**
     * get table layout
     *
     * @param environmentId environment id
     * @param tableName     table name
     * @return table layout
     * @throws SqlConfigurationException occurs when configuration is not acceptable.
     */
    public static TableLayout getLayout( String environmentId, String tableName ) {

        String key = getKey( environmentId, tableName );

        synchronized( lock ) {
            if( ! tableLayoutRepository.containsKey( key ) ) {
                try {
                    TableLayout tableLayout = new TableLayoutReader().getTableLayout( environmentId, tableName );
                    if( ! tableLayout.isEmpty() ) {
                        tableLayoutRepository.put( key, tableLayout );
                        NLogger.trace( "Table Layout Loaded in Nybatis. (environmentId:{}, tableName:{})", environmentId, tableName );
                    }
                } catch( Exception e ) {
                    NLogger.warn( "Table Layout failed to be Loaded in Nybatis. (environmentId:{}, tableName:{})", environmentId, tableName );
                    NLogger.trace( e );
                }
            }
        }

        return tableLayoutRepository.get( key );

    }

    /**
     * get table layout
     *
     * @param environmentId environment id
     * @param domainClass   table entity class
     * @return table layout
     * @throws SqlConfigurationException occurs when configuration is not acceptable
     */
    public static TableLayout getLayout( String environmentId, Class domainClass ) {
        return getLayout( environmentId, EntityLayoutReader.getTableName(domainClass) );
    }

    /**
     * clear table layout
     *
     * @param environmentId environment id
     * @param tableName     table name
     */
    public static void clearLayout( String environmentId, String tableName ) {
        tableName = StringUtil.toUpperCase( tableName );
        String key = getKey( environmentId, tableName );
        synchronized( lock ) {
            tableLayoutRepository.remove( key );
        }
    }

    /**
     * clear table layout
     *
     * @param environmentId environment id
     * @param domainClass   table entity class
     */
    public static void clearLayout( String environmentId, Class domainClass ) {
        clearLayout( environmentId, EntityLayoutReader.getTableName(domainClass) );
    }


}
