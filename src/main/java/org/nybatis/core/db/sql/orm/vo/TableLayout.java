package org.nybatis.core.db.sql.orm.vo;

import java.util.*;
import org.nybatis.core.model.NList;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

/**
 * Table Layout
 *
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class TableLayout {

    private String                  environmentId;
    private String                  tableName;
    private String                  pkName;
    private Map<String,Column>      pkColumns      = new LinkedHashMap<>();
    private Map<String,Column>      columns        = new LinkedHashMap<>();
    private Map<String,IndexLayout> indices        = new LinkedHashMap<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName( String tableName ) {
        this.tableName = tableName;
    }

    public String getPkName() {
        if( Validator.isEmpty(pkName) ) {
            return String.format( "PK_%s", tableName );
        } else {
            return pkName;
        }
    }

    public void setPkName( String pkName ) {
        this.pkName = pkName;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId( String environmentId ) {
        this.environmentId = environmentId;
    }

    public List<Column> getPkColumns() {
        return new ArrayList<>( pkColumns.values() );
    }

    public List<Column> getColumns() {
        return new ArrayList<>( columns.values() );
    }

    public boolean hasColumnName( String name ) {
        return columns.containsKey( name );
    }

    public void addColumn( Column column ) {
        if( column == null ) return;
        columns.put( column.getKey(), column );
        if( column.isPk() ) {
            pkColumns.put( column.getKey(), column );
        }
    }

    public List<IndexLayout> getIndices() {
        return new ArrayList<>( indices.values() );
    }

    public void addIndex( IndexLayout index ) {
        if( index == null ) return;
        indices.put( index.getName(), index );
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "EnvironmentId : " ).append( environmentId ).append( '\n' );
        sb.append( "Table         : " ).append( tableName ).append( '\n' );
        sb.append( "PK index      : " ).append( getPkName() ).append( " / " ).append( pkColumnsList() ).append( '\n' );
        sb.append( "Columns       :\n" ).append( new NList( columns.values() ).toString() );
        sb.append( "Indexes       :" );
        for( IndexLayout index : indices.values() ) {
            sb.append( "\n\t " ).append( index );
        }
        return sb.toString();
    }

    private String pkColumnsList() {
        return pkColumns.keySet().toString();
    }

    public boolean isEmpty() {
        return pkColumns.size() == 0 && columns.size() == 0;
    }

    public boolean isEqual( TableLayout another ) {
        if( another == null ) return false;
        if( ! StringUtil.nvl(tableName).equals( StringUtil.nvl(another.tableName) ) ) return false;
        if( ! isEqualColumns( columns, another.columns ) ) return false;
        if( ! isEqualIncies( indices, another.indices ) ) return false;
        return true;
    }

    private boolean isEqualColumns( Map<String,Column> map1, Map<String,Column> map2 ) {
        if( map1.size() != map2.size() ) return false;
        for( String key : map1.keySet() ) {
            if( ! map2.containsKey( key ) ) return false;
            Column c1 = map1.get( key );
            Column c2 = map2.get( key );
            if( ! c1.isEqual(c2) ) return false;
        }
        return true;
    }

    private boolean isEqualIncies( Map<String,IndexLayout> map1, Map<String,IndexLayout> map2 ) {
        if( map1.size() != map2.size() ) return false;
        for( String key : map1.keySet() ) {
            if( ! map2.containsKey( key ) ) return false;
            IndexLayout idx1 = map1.get( key );
            IndexLayout idx2 = map2.get( key );
            if( ! idx1.isEqual(idx2) ) return false;
        }
        return true;
    }

    public List<Column> getColumnsToAdd( TableLayout another ) {
        if( another == null )
            return new ArrayList<>( this.columns.values() );
        List<Column> columns = new ArrayList<>();
        for( String key : this.columns.keySet() ) {
            if( ! another.columns.containsKey(key) ) {
                columns.add( this.columns.get(key) );
            }
        }
        return columns;
    }

    public List<Column> getColumnsToDelete( TableLayout another ) {
        if( another == null )
            return new ArrayList<>( this.columns.values() );
        List<Column> columns = new ArrayList<>();
        for( String key : another.columns.keySet() ) {
            if( ! this.columns.containsKey(key) ) {
                columns.add( this.columns.get(key) );
            }
        }
        return columns;
    }

    public List<Column> getColumnsToUpdate( TableLayout another ) {
        if( another == null )
            return new ArrayList<>( this.columns.values() );
        List<Column> columns = new ArrayList<>();
        for( String key : this.columns.keySet() ) {
            if( another.columns.containsKey(key) ) {
                // do not check PK dirrefence
                if( ! this.columns.get(key).isEqual( another.columns.get(key), false ) ) {
                    columns.add( this.columns.get(key) );
                }
            }
        }
        return columns;
    }

    public boolean isPkEqual( TableLayout another ) {
        if( another == null || pkColumns.size() != another.pkColumns.size() ) return false;
        return pkColumns.keySet().toString().equals( another.pkColumns.keySet().toString() );
    }

}
