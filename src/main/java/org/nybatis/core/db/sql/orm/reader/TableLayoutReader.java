package org.nybatis.core.db.sql.orm.reader;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.handler.ConnectionHandler;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.sql.orm.vo.Column;
import org.nybatis.core.db.sql.orm.vo.TableIndex;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

/**
 * Table Layout Reader
 *
 * @author nayasis@gmail.com
 * @since 2015-09-08
 */
public class TableLayoutReader {

    public TableLayout getTableLayout( String environmentId, String tableName ) {

        SqlSession sqlSession = SessionManager.openSession( environmentId );

        Table table = new Table( tableName );

        TableLayout layout = new TableLayout();
        layout.setEnvironmentId( environmentId );
        layout.setName( tableName );

        sqlSession.useConnection( new ConnectionHandler() {
            public void execute( Connection connection ) throws Throwable {
                DatabaseMetaData metaData = connection.getMetaData();
                readColumns( metaData );
                // read non-unique index
                readIndex( toList( metaData.getIndexInfo(null, table.scheme, table.name, false, false), false ) );
                // read unique index
                readIndex( toList( metaData.getIndexInfo(null, table.scheme, table.name, true,  false), false ) );
            }

            private void readColumns( DatabaseMetaData metaData ) throws SQLException {

                Set<String> pkColumnNames = new HashSet<>();

                for( NMap pk : toList( metaData.getPrimaryKeys(null, table.scheme, table.name), false ) ) {
                    layout.setPkName( pk.getString( "pkName" ) );;
                    pkColumnNames.add( StringUtil.toCamel(pk.getString("columnName")) );
                }

                for( NMap column : toList( metaData.getColumns(null, table.scheme, table.name, null), false ) ) {

                    Column c = new Column();
                    c.setKey( StringUtil.toCamel(column.getString( "columnName") ) );
                    c.setDataType( column.getInt( "dataType" ), column.getString( "typeName" ) );
                    c.setNotNull( column.getInt( "nullable" ) <= 0 );
                    c.setPk( pkColumnNames.contains( c.getKey() ) );
                    c.setSize( column.getInt("columnSize") );
                    int precision = column.getInt( "decimalDigits" );
                    if( precision > 0  ) {
                        c.setPrecison( precision );
                    }

                    layout.addColumn( c );

                }
            }

            private void readIndex( NList indexList ) throws SQLException {

                Map<String,Map<Integer,String>> indices = new LinkedHashMap<>();

                for( NMap index : indexList ) {

                    String indexName  = index.getString( "indexName" );
                    int    nonUnique  = index.getInt( "nonUnique" );
                    String columnName = StringUtil.toCamel( index.getString( "columnName" ) );
                    String ascOrDesc  = index.getString("ascOrDesc");
                    int    position   = index.getInt( "ordinalPosition" );

                    if( StringUtil.isEmpty(indexName) || indexName.equals(layout.getPkName()) ) continue;

                    if( ! indices.containsKey(indexName) ) {
                        indices.put( indexName, new TreeMap<>() );
                    }

                    Map<Integer, String> indexMap = indices.get( indexName );
                    if( StringUtil.isNotEmpty(ascOrDesc) ) {
                        columnName += " " + ascOrDesc;
                    }
                    indexMap.put( position, columnName );

                }

                for( String indexName : indices.keySet() ) {
                    Map<Integer, String> columnInfo = indices.get( indexName );
                    TableIndex index = new TableIndex( indexName, new LinkedHashSet<>( columnInfo.values() ) );
                    layout.addIndex( index );
                }
            }

        });

        return layout;

    }

    private class Table {

        protected String scheme = null;
        protected String name   = null;

        public Table( String tableName ) {
            if( tableName.contains(".") ) {
                int splitIndex = tableName.indexOf( "." );
                scheme = tableName.substring( 0, splitIndex );
                name   = tableName.substring( splitIndex + 1 );
            } else {
                name = tableName;
            }
        }
    }

}
