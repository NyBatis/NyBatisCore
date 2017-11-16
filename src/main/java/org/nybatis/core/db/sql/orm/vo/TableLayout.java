package org.nybatis.core.db.sql.orm.vo;

import org.nybatis.core.db.sql.orm.vo.Column;
import org.nybatis.core.db.sql.orm.vo.IndexLayout;
import org.nybatis.core.model.NList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class TableLayout {

    private String            environmentId;
    private String            tableName;
    private List<Column>      pkColumns      = new ArrayList<>();
    private List<Column>      columns        = new ArrayList<>();
    private List<IndexLayout> indices        = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName( String tableName ) {
        this.tableName = tableName;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId( String environmentId ) {
        this.environmentId = environmentId;
    }

    public List<Column> getPkColumns() {
        return pkColumns;
    }

    public void addPkColumn( Column column ) {
        pkColumns.add( column );
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean hasColumnName( String name ) {
        for( Column column : columns ) {
            if( column.getName().equals(column) ) return true;
        }
        return false;
    }

    public void addColumn( Column column ) {
        columns.add( column );
    }

    public List<IndexLayout> getIndices() {
        return indices;
    }

    public void setIndices( List<IndexLayout> indices ) {
        this.indices = indices;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append( "EnvironmentId : " ).append( environmentId ).append( '\n' );
        sb.append( "Table         : " ).append( tableName ).append( '\n' );
        sb.append( "PK List       : " ).append( pkColumnsList() ).append( '\n' );
        sb.append( "Columns       :\n" ).append( new NList( columns ).toString() );
        sb.append( "Indexes       :" );
        for( IndexLayout index : indices ) {
            sb.append( "\n\t " ).append( index );
        }

        return sb.toString();

    }

    private String pkColumnsList() {

        List<String> pks = new ArrayList<>();

        for( Column column : pkColumns ) {
            pks.add( column.getKey() );
        }

        return pks.toString();

    }

    public boolean isEmpty() {
        return pkColumns.size() == 0 && columns.size() == 0;
    }

}
