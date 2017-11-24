package org.nybatis.core.db.sql.orm.reader;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import org.nybatis.core.db.etc.SqlLogHider;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.handler.ConnectionHandler;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.sql.orm.vo.TableColumn;
import org.nybatis.core.db.sql.orm.vo.TableIndex;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

import static org.nybatis.core.db.datasource.driver.DatabaseName.*;

/**
 * Table Layout Reader
 *
 * @author nayasis@gmail.com
 * @since 2015-09-08
 */
public class TableLayoutReader {

    public TableLayout getTableLayout( String environmentId, String tableName ) {

        final TableLayout[] layout = new TableLayout[1];

        SqlSession session = SessionManager.openSession( environmentId );

        Table table = new Table( tableName );

        new SqlLogHider().hideDebugLog( () -> {
            layout[0] = readColumns( session, table );
            layout[0].setEnvironmentId( environmentId );
            layout[0].setName( tableName );

            try {
                if( session.isDatabase(SQLITE) ) {
                    getSqliteIndices( session ).forEach( tableIndex -> layout[0].addIndex( tableIndex ) );
                } else {
                    getIndices( session, table, layout[0].getPkName() ).forEach( tableIndex -> layout[0].addIndex( tableIndex ) );
                }
            } catch( Exception e ) {
                NLogger.info( "it is not support to retrieve index info from table({}) in environment(id:{})", table.name, environmentId );
                NLogger.trace( e );
                layout[0].setSupportToReadIndex( false );
            }

            if( session.isDatabase(ORACLE) ) {
                for( TableIndex index : layout[0].getIndices() ) {
                    replaceIndexNameOnOracle( session, index );
                }
            }
        });

        return layout[0];

    }

    private Set<String> getPkColumnNames( SqlSession session, Table table ) {

        Map<Integer,String> pkColumnNames = new TreeMap<>();

        if( session.isDatabase(SQLITE) ) {
            List<NMap> list = session.sql( String.format( "PRAGMA TABLE_INFO('%s')", table.name ) ).list().select();
            for( NMap row : list ) {
                int  seqPk  = row.getInt( "pk" );
                int  seqCl  = row.getInt( "cid" );
                String name = StringUtil.toCamel( row.getString("name") );
                if( seqPk > 0 ) {
                    pkColumnNames.put( seqPk * 1000 + seqCl, name );
                }
            }
        } else {
            session.useConnection( new ConnectionHandler() {
                public void execute( Connection connection ) throws Throwable {
                    DatabaseMetaData metaData = connection.getMetaData();
                    int index = 1;
                    for( NMap pk : toList( metaData.getPrimaryKeys( null, table.scheme, table.name ), false ) ) {
                        pkColumnNames.put( index++, StringUtil.toCamel(pk.getString("columnName")) );
                    }
                }
            });
        }

        return new LinkedHashSet<>( pkColumnNames.values() );

    }

    private TableLayout readColumns( SqlSession session, Table table ) {

        TableLayout layout = new TableLayout();
        layout.setEnvironmentId( session.getEnvironmentId() );
        Set<String> pkColumnNames = getPkColumnNames( session, table );

        session.useConnection( new ConnectionHandler() {
            public void execute( Connection connection ) throws Throwable {
                DatabaseMetaData metaData = connection.getMetaData();

                for( NMap pk : toList( metaData.getPrimaryKeys( null, table.scheme, table.name ), false ) ) {
                    layout.setPkName( pk.getString( "pkName" ) );
                    break;
                }

                for( NMap column : toList( metaData.getColumns( null, table.scheme, table.name, null ), false ) ) {

                    TableColumn c = new TableColumn( layout );
                    c.setKey( StringUtil.toCamel( column.getString( "columnName" ) ) );
                    c.setDataType( column.getInt( "dataType" ), column.getString( "typeName" ) );
                    c.setNotNull( column.getInt( "nullable" ) <= 0 );
                    c.setPk( pkColumnNames.contains( c.getKey() ) );
                    c.setSize( column.getInt( "columnSize" ) );
                    c.setDefaultValue( column.getString( "columnDef" ) );
                    int precision = column.getInt( "decimalDigits" );
                    if( precision > 0 ) {
                        c.setPrecison( precision );
                    }

                    layout.addColumn( c );

                }
            }
        });

        return layout;

    }

    private List<TableIndex> getIndices( SqlSession session, Table table, String pkName ) {

        List<TableIndex> result = new ArrayList<>();

        session.useConnection( new ConnectionHandler() {
            public void execute( Connection connection ) throws Throwable {
                DatabaseMetaData metaData = connection.getMetaData();
                // read non-unique index
                readIndex( toList( metaData.getIndexInfo(null, table.scheme, table.name, false, false), false ) );
                // read unique index
                readIndex( toList( metaData.getIndexInfo(null, table.scheme, table.name, true,  false), false ) );
            }

            private void readIndex( NList indexList ) throws SQLException {

                Map<String,Map<Integer,String>> indices = new LinkedHashMap<>();

                for( NMap index : indexList ) {

                    String  indexName  = index.getString( "indexName" );
                    boolean nonUnique  = true;
                    String  columnName = StringUtil.toCamel( index.getString( "columnName" ) );
                    String  ascOrDesc  = index.getString("ascOrDesc");
                    int     position   = index.getInt( "ordinalPosition" );

                    if( StringUtil.isEmpty(indexName) || indexName.equals(pkName) ) continue;

                    if( session.isDatabase(H2) ){
                        nonUnique = index.getBoolean( "nonUnique" );
                    } else {
                        nonUnique = ( index.getInt( "nonUnique" ) == 1 );
                    }

                    if( session.isDatabase(H2) ){
                        if( nonUnique == false && indexName.startsWith( "PRIMARY_KEY_" ) ) continue;
                    }

                    if( ! indices.containsKey(indexName) ) {
                        indices.put( indexName, new TreeMap<>() );
                    }

                    Map<Integer, String> indexMap = indices.get( indexName );
                    if( StringUtil.isNotEmpty(ascOrDesc) ) {
                        if( "D".equalsIgnoreCase(ascOrDesc) ) ascOrDesc = "desc";
                        if( ascOrDesc.equalsIgnoreCase( "desc" ) ) {
                            columnName += " " + ascOrDesc;
                        }
                    }
                    indexMap.put( position, columnName );

                }

                for( String indexName : indices.keySet() ) {
                    Map<Integer, String> columnInfo = indices.get( indexName );
                    TableIndex index = new TableIndex( indexName, new LinkedHashSet<>( columnInfo.values() ) );
                    result.add( index );
                }
            }

        });

        return result;

    }


    private void replaceIndexNameOnOracle( SqlSession sqlSession, TableIndex index ) {
        if( ! hasSysCreatedIndexName(index) ) return;
        Map<String, String> indexNames = getOralceCreatedIndexName( sqlSession, index.getName() );
        Set<String> newColumnNames = new LinkedHashSet<>();
        for( String columnName : index.getColumnNames() ) {
            if( columnName.startsWith("sysNc") ) {
                if( indexNames.containsKey(columnName) ) {
                    newColumnNames.add( indexNames.get(columnName) );
                    continue;
                }
            }
            newColumnNames.add( columnName );
        }
        index.setColumnNames( newColumnNames );
    }

    private boolean hasSysCreatedIndexName( TableIndex index ) {
        for( String columnName : index.getColumnNames() ) {
            if( columnName.startsWith("sysNc") ) return true;
        }
        return false;
    }

    private Map<String,String> getOralceCreatedIndexName( SqlSession sqlSession, String indexName ) {

        String sql =
            "SELECT  column_name, column_expression, descend\n" +
            "FROM    USER_IND_COLUMNS      A\n" +
            "JOIN    USER_IND_EXPRESSIONS  B\n" +
            "        USING( index_name, table_name, column_position ) \n" +
            "WHERE   index_name  = #{indexName}";

        Map<String,String> names = new HashMap<>();

        for( NMap row : sqlSession.sql( sql ).addParameter( "indexName", indexName ).list().select() ) {

            String srcKey = StringUtil.toCamel( row.getString("columnName") );
            String trgKey = StringUtil.toCamel( row.getString("columnExpression").replaceFirst( "\"(.+?)\"","$1" ) );
            String desc   = StringUtil.toLowerCase( row.getString( "descend" ) );
            if( "desc".equals("desc") ) {
                trgKey += " " + desc;
            }

            names.put( srcKey, trgKey );

        }

        return names;

    }

    private List<TableIndex> getSqliteIndices( SqlSession sqlSession ) {

        String sql =
            "SELECT  name as index_key, sql as index_columns\n" +
            "FROM    SQLITE_MASTER\n" +
            "WHERE   type = 'index'\n" +
            "AND     sql IS NOT NULL"
        ;

        List<TableIndex> indices = new ArrayList<>();

        for( NMap row : sqlSession.sql(sql).list().select() ) {
            String indexName  = row.getString( "indexKey" );
            String columnInfo = row.getString( "indexColumns" ).replaceFirst( "^.*\\((.+?)\\)$","$1" );
            Set<String> indexColumns = new LinkedHashSet<>();
            for( String columnName : StringUtil.split( columnInfo,",") ) {
                indexColumns.add( StringUtil.toCamel(columnName.toLowerCase()).replaceFirst( " asc$", "" ) );
            }
            indices.add( new TableIndex( indexName, indexColumns ) );
        }

        return indices;

    }


    private class Table {

        protected String scheme = null;
        protected String name   = null;

        public Table( String tableName ) {
            if( tableName.contains(".") ) {
                int splitIndex = tableName.indexOf( "." );
                scheme = tableName.substring( 0, splitIndex ).toUpperCase();
                name   = tableName.substring( splitIndex + 1 ).toUpperCase();
            } else {
                name = tableName.toUpperCase();
            }
        }
    }

}
