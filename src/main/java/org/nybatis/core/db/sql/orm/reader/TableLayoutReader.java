package org.nybatis.core.db.sql.orm.reader;

import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.handler.ConnectionHandler;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.sql.orm.vo.Column;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.LinkedHashSet;
import java.util.Set;

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
        layout.setTableName( tableName );

        sqlSession.useConnection( new ConnectionHandler() {
            public void execute( Connection connection ) throws Throwable {

                DatabaseMetaData metaData = connection.getMetaData();

                Set<String> pkList = new LinkedHashSet<>();

                for( NMap pk : toList( metaData.getPrimaryKeys(null, table.scheme, table.name), false ) ) {
                    pkList.add( StringUtil.toCamel(pk.getString("columnName")) );
                }

                for( NMap column : toList( metaData.getColumns(null, table.scheme, table.name, null), false ) ) {

                    Column c = new Column();

                    c.setKey( StringUtil.toCamel( column.getString( "columnName" ) ) );
                    c.setDataType( column.getInt( "dataType" ), column.getString( "typeName" ) );
                    c.setNotNull( column.getInt( "nullable" ) <= 0 );
                    c.setPk( pkList.contains( c.getKey() ) );
                    c.setSize( column.getInt("columnSize") );

                    layout.addColumn( c );

                    if( c.isPk() ) {
                        layout.addPkColumn( c );
                    }

                }

            }
        });

        return layout;

    }

    private static class Table {

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
