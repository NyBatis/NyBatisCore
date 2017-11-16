package org.nybatis.core.db.sql.reader.table;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.constant.NullValue;
import org.nybatis.core.db.sql.reader.SqlReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.UncheckedIOException;
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

            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_INSERT_PK, insertPkSql( layout ) );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_SELECT_PK, selectPkSql( layout ) );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_SELECT,    selectSql( layout )   );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_UPDATE_PK, updatePkSql( layout ) );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_DELETE_PK, deletePkSql( layout ) );
            read( environmentId, sqlIdPrefix, Const.db.ORM_SQL_DELETE,    deleteSql( layout )   );

        } finally {
            readLocker.unlock();
        }

    }

    private void read( String environmentId, String mainId, String subId, String xmlSql ) {

        String sqlId = mainId + subId;

        if( SqlRepository.isExist( sqlId ) ) return;

        SqlReader reader = new SqlReader();

        try {

            SqlNode sqlNode = reader.read( environmentId, sqlId, xmlSql );

            SqlProperties properties = sqlNode.getProperties();

            sqlNode.setMainId( mainId );

            SqlRepository.put( sqlId, sqlNode );

        } catch( ParseException | UncheckedIOException | SqlParseException | DatabaseConfigurationException e ) {
            throw new SqlConfigurationException( e, "Error on making sql ({})", subId );
        }

    }

    private String selectPkSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format( "SELECT /*+ %s.%s.%s */ * FROM %s WHERE 1=1\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_SELECT_PK, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName() ) );

        for( Column column : layout.getPkColumns() ) {

            String paramName  = getOverrideKey( column.getKey() );
            String columnName = column.getName();

            sb.append( String.format(
                    getTestNode("#{%s} != null && #{%s} !='%s'","AND %s = #{%s%s}"),
                    paramName, paramName, NullValue.STRING,
                    columnName, paramName, column.getDataTypeForSqlMaking()
            ));

        }

        sb.append( getOverrideWhereNode() );
        sb.append( getOverrideOrderbyNode() );

        return sb.toString();

    }

    private String selectSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format( "SELECT /*+ %s.%s.%s */ * FROM %s WHERE 1=1\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_SELECT, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName() ) );

        for( Column column : layout.getColumns() ) {

            String paramName  = getOverrideKey( column.getKey() );
            String columnName = column.getName();

            sb.append( String.format(
                    getTestNode("#{%s} != null && #{%s} !='%s'","AND %s = #{%s%s}"),
                    paramName, paramName, NullValue.STRING,
                    columnName, paramName, column.getDataTypeForSqlMaking()
            ));

            sb.append( String.format(
                    getTestNode("#{%s} == '%s'","AND %s IS NULL"),
                    paramName, NullValue.STRING,
                    columnName
            ));

        }

        sb.append( getOverrideWhereNode() );
        sb.append( getOverrideOrderbyNode() );

        return sb.toString();

    }

    private String updatePkSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format("UPDATE /*+ %s.%s.%s */ %s SET\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_UPDATE_PK, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName()) );
        sb.append( "<group delimeter=\",\">\n" );

        for( Column column : layout.getColumns() ) {

            if( column.isPk() ) continue;

            String paramName  = getOverrideKey( column.getKey() );
            String columnName = column.getName();

            sb.append(String.format(
                getTestNode("#{%s} != null && #{%s} != '%s'", "  %s = #{%s%s}" ),
                paramName, paramName, NullValue.STRING,
                    columnName, paramName, column.getDataTypeForSqlMaking()
            ));

            sb.append( String.format(
                    getTestNode("#{%s} == '%s'"," %s = NULL"),
                    paramName, NullValue.STRING,
                    columnName
            ));

        }

        sb.append( "</group>\n" );
        sb.append( "WHERE 1 = 1\n" );

        for( Column column : layout.getPkColumns() ) {
            String paramName  = getOverrideKey( column.getKey() );
            String columnName = column.getName();
            sb.append( String.format(
                    getTestNode("#{%s} != null && #{%s} !='%s'","AND %s = #{%s%s}"),
                    paramName, paramName, NullValue.STRING,
                    columnName, paramName, column.getDataTypeForSqlMaking()
            ));
        }

        sb.append( getOverrideWhereNode() );

        return sb.toString();

    }

    private String deleteSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format( "DELETE /*+ %s.%s.%s */ FROM %s WHERE 1=1\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_DELETE, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName() ) );

        for( Column column : layout.getColumns() ) {

            String paramName  = getOverrideKey( column.getKey() );
            String columnName = column.getName();

            sb.append( String.format(
                    getTestNode("#{%s} != null && #{%s} !='%s'","AND %s = #{%s%s}"),
                    paramName, paramName, NullValue.STRING,
                    columnName, paramName, column.getDataTypeForSqlMaking()
            ));

            sb.append( String.format(
                    getTestNode("#{%s} == '%s'","AND %s IS NULL"),
                    paramName, NullValue.STRING,
                    columnName
            ));

        }

        sb.append( getOverrideWhereNode() );

        return sb.toString();

    }

    private String deletePkSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        sb.append( String.format( "DELETE /*+ %s.%s.%s */ FROM %s WHERE 1=1\n", Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_DELETE, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName() ) );

        for( Column column : layout.getPkColumns() ) {

            String paramName  = getOverrideKey( column.getKey() );
            String columnName = column.getName();

            sb.append( String.format(
                    getTestNode("#{%s} != null && #{%s} !='%s'","AND %s = #{%s%s}"),
                    paramName, paramName, NullValue.STRING,
                    columnName, paramName, column.getDataTypeForSqlMaking()
            ));

        }

        sb.append( getOverrideWhereNode() );

        return sb.toString();

    }

    private String insertPkSql( TableLayout layout ) {

        StringBuilder sb = new StringBuilder();

        StringBuilder structureDefine = new StringBuilder();
        StringBuilder structureValues = new StringBuilder();

        structureDefine.append( "<group delimeter=\",\">\n" );
        structureValues.append( "<group delimeter=\",\">\n" );

        for( Column column : layout.getColumns() ) {

            String paramName  = getOverrideKey( column.getKey() );
            String columnName = column.getName();

            structureDefine.append( String.format(
                    getTestNode( "#{%s} != null || #{%s} == '%s'", " %s" ),
                    paramName, paramName, NullValue.STRING,
                    columnName
            ));

            structureValues.append(String.format(
                    getTestNode("#{%s} != null && #{%s} !='%s'", " #{%s%s}"),
                    paramName, paramName, NullValue.STRING,
                    paramName, column.getDataTypeForSqlMaking()
            ));

            structureValues.append( String.format(
                    getTestNode("#{%s} == '%s'","NULL"),
                    paramName, NullValue.STRING
            ));

        }

        structureDefine.append( "</group>\n" );
        structureValues.append( "</group>\n" );

        sb.append( String.format( "INSERT /*+ %s.%s.%s */ INTO %s (\n%s) VALUES (\n%s)",
                Const.db.ORM_SQL_PREFIX + Const.db.ORM_SQL_INSERT_PK, layout.getEnvironmentId(), layout.getTableName(), layout.getTableName(),
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