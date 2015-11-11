package org.nybatis.core.db.sql.reader;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.sql.repository.Column;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.repository.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.validation.Assertion;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Database Table Layout Reader
 *
 * <pre>
 * It reads table layout to make ORM based CRUD query.
 * </pre>
 *
 * @author nayasis@gmail.com
 * @since 2015-09-08
 */
public class DbTableReader {

    private Lock readLocker = new ReentrantLock();

    public void read( String environmentId, String tableName ) {
        read( environmentId, tableName, null, null );
    }

    public void read( String environmentId, String tableName, String cacheId, Integer flush ) {

        Assertion.isNotNull( environmentId, new SqlConfigurationException( "environmentId is null. (environmentId : {}, tableName:{})", environmentId, tableName ) );
        Assertion.isNotNull( tableName, new SqlConfigurationException( "tableName is null. (environmentId : {}, tableName:{})", environmentId, tableName ) );

        readLocker.lock();

        try {

            if( TableLayoutRepository.isExist(environmentId, tableName) ) return;

            TableLayout layout = TableLayoutRepository.getLayout( environmentId, tableName );

            if( layout.isEmpty() ) {
                throw new SqlConfigurationException( "There is no table. (environmentId:{}, tableName:{})", environmentId, tableName );
            }

            String sqlIdPrefix = Const.db.getOrmSqlIdPrefix( environmentId, tableName );

            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_SELECT_SINGLE, selectSingleSql( layout ), cacheId, flush );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_SELECT_MULTI,  selectListSql( layout ),   cacheId, flush );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_UPDATE,        updateSql( layout ),       cacheId, flush );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_DELETE,        deleteSql( layout ),       cacheId, flush );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_INSERT,        insertSql( layout ),       cacheId, flush );

        } finally {
            readLocker.unlock();
        }

    }

    private void read( String environmentId, String mainId, String subId, String xmlSql, String cacheId, Integer flush ) {

        String sqlId = mainId + subId;

        if( SqlRepository.isExist( sqlId ) ) return;

        SqlReader reader = new SqlReader();

        try {

            SqlNode sqlNode = reader.read( environmentId, sqlId, xmlSql );

            SqlProperties properties = sqlNode.getProperties();

            if( cacheId != null ) properties.setCacheId( cacheId );
            if( flush   != null ) properties.setFetchSize( flush );

            sqlNode.setMainId( mainId );

            SqlRepository.put( sqlId, sqlNode );

        } catch( ParseException | IoException | SqlParseException | DatabaseConfigurationException e ) {
            throw new SqlConfigurationException( e, "Error on making sql ({})", subId );
        }

    }

    private String selectSingleSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format( "SELECT /*+ %s.%s.%s */ * FROM %s WHERE 1=1\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_SELECT_SINGLE, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName() ) );

        for( Column column : layout.getPkColumns() ) {
            sb.append( String.format(
                    getTestNode("#{%s} != null","AND %s = #{%s%s}"),
                    getOverrideKey(column.getKey()), column.getName(), getOverrideKey(column.getKey()), column.getDataTypeForSqlMaking()
            ));
        }

        sb.append( getOverrideWhereNode() );
        sb.append( getOverrideOrderbyNode() );

        return sb.toString();

    }

    private String selectListSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format( "SELECT /*+ %s.%s.%s */ * FROM %s WHERE 1=1\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_SELECT_MULTI, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName() ) );

        for( Column column : layout.getColumns() ) {
            sb.append( String.format(
                    getTestNode("#{%s} != null","AND %s = #{%s%s}"),
                    getOverrideKey(column.getKey()), column.getName(), getOverrideKey(column.getKey()), column.getDataTypeForSqlMaking()
            ));
        }

        sb.append( getOverrideWhereNode() );
        sb.append( getOverrideOrderbyNode() );

        return sb.toString();

    }

    private String updateSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format("UPDATE /*+ %s.%s.%s */ %s SET\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_UPDATE, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName()) );
        sb.append( "<group delimeter=\",\">\n" );

        for( Column column : layout.getColumns() ) {

            if( column.isPk() ) continue;

            sb.append(String.format(
                getTestNode("#{%s} != null", "  %s = #{%s%s}" ),
                getOverrideKey(column.getKey()), column.getName(), getOverrideKey(column.getKey()), column.getDataTypeForSqlMaking()
            ));

        }

        sb.append( "</group>\n" );
        sb.append( "WHERE 1 = 1\n" );

        for( Column column : layout.getPkColumns() ) {
            sb.append( String.format(
                    getTestNode( "#{%s} != null","AND %s = #{%s%s}"),
                    getOverrideKey(column.getKey()), column.getName(), getOverrideKey(column.getKey()), column.getDataTypeForSqlMaking()
            ));
        }

        sb.append( getOverrideWhereNode() );

        return sb.toString();

    }

    private String deleteSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format( "DELETE /*+ %s.%s.%s */ FROM %s WHERE 1=1\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_DELETE, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName() ) );

        for( Column column : layout.getColumns() ) {
            sb.append( String.format(
                    getTestNode( "#{%s} != null", "AND %s = #{%s}" ),
                    getOverrideKey( column.getKey() ), column.getName(), getOverrideKey( column.getKey() )
            ));
        }

        sb.append( getOverrideWhereNode() );

        return sb.toString();

    }

    private String insertSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        StringBuilder structureDefine = new StringBuilder();
        StringBuilder structureValues = new StringBuilder();

        boolean isFirst = true;

        for( Column column : layout.getColumns() ) {

            if( column.isPk() ) {

                structureDefine.append(String.format( (isFirst ? " " : ",") + " %s\n", column.getName() ));
                structureValues.append(String.format( (isFirst ? " " : ",") + " #{%s%s}\n", getOverrideKey( column.getKey() ), column.getDataTypeForSqlMaking() ));

            } else {

                structureDefine.append( String.format(
                        getTestNode( "#{%s} != null", (isFirst ? " " : ",") + " %s" ),
                        getOverrideKey(column.getKey()), column.getName()
                ));

                structureValues.append(String.format(
                        getTestNode("#{%s} != null", (isFirst ? " " : ",") + " #{%s%s}"),
                        getOverrideKey(column.getKey()), getOverrideKey(column.getKey()), column.getDataTypeForSqlMaking()
                ));

            }

            isFirst = false;
        }

        sb.append( String.format( "INSERT /*+ %s.%s.%s */ INTO %s (\n%s) VALUES (\n%s)",
                Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_INSERT, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName(),
                structureDefine,
                structureValues
        ) );


        return sb.toString();

    }

    private String getOverrideKey( String camelKey ) {
        return Const.db.ORM_PARAMETER_ENTITY + camelKey;
    }

    private String getTestNode( String test, String text ) {
        return String.format( "<if test=\"%s\">%s</if>\n", test, text );
    }

    private String getOverrideWhereNode() {
        return String.format(getTestNode( "#{%s} != empty", "${%s}" ), Const.db.ORM_PARAMETER_WHERE, Const.db.ORM_PARAMETER_WHERE );
    }

    private String getOverrideOrderbyNode() {
        return String.format(getTestNode( "#{%s} != empty", "${%s}" ), Const.db.ORM_PARAMETER_ORDER_BY, Const.db.ORM_PARAMETER_ORDER_BY );
    }

}
