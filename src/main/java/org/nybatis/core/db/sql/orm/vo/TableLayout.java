package org.nybatis.core.db.sql.orm.vo;

import java.util.*;
import org.nybatis.core.model.NList;
import org.nybatis.core.util.StringUtil;

/**
 * Table Layout
 *
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class TableLayout {

    private String                  environmentId;
    private String                  name;
    private String                  pkName;
    private Map<String,TableColumn> pkColumns      = new LinkedHashMap<>();
    private Map<String,TableColumn> columns        = new LinkedHashMap<>();
    private Map<String,TableIndex>  indices        = new LinkedHashMap<>();

    private boolean supportToReadIndex = true;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getPkName() {
        return pkName;
    }

    public boolean hasPk() {
        return pkColumns.size() > 0;
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

    public List<TableColumn> getPkColumns() {
        return new ArrayList<>( pkColumns.values() );
    }

    public Set<String> getPkColumnNames() {
        return pkColumns.keySet();
    }

    public List<TableColumn> getColumns() {
        return new ArrayList<>( columns.values() );
    }

    public Map<String,TableColumn> getColumnMap() {
        return columns;
    }

    public TableColumn getColumn( String name ) {
        if( StringUtil.isNotEmpty(name) ) {
            name = name.replaceFirst( " .*$", "" );
            return columns.get( name );
        }
        return null;
    }

    public boolean hasColumnName( String name ) {
        if( StringUtil.isEmpty(name) ) return false;
        name = name.replaceFirst( " .*$", "" );
        return columns.containsKey( name );
    }

    public void addColumn( TableColumn column ) {
        if( column == null ) return;
        columns.put( column.getKey(), column );
        if( column.isPk() ) {
            pkColumns.put( column.getKey(), column );
        }
    }

    public List<TableIndex> getIndices() {
        return new ArrayList<>( indices.values() );
    }

    public void addIndex( TableIndex index ) {
        if( index == null ) return;
        indices.put( index.getName(), index );
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "EnvironmentId : " ).append( environmentId ).append( '\n' );
        sb.append( "Table         : " ).append( name ).append( '\n' );
        sb.append( "PK index      : " ).append( getPkName() ).append( " / " ).append( pkColumnsList() ).append( '\n' );
        sb.append( "Columns       :\n" ).append( new NList( columns.values() ).toString() );
        sb.append( "Indexes       :" );
        for( TableIndex index : indices.values() ) {
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
        if( ! StringUtil.nvl( name ).equals( StringUtil.nvl(another.name ) ) ) return false;
        if( ! isEqualColumns( columns, another.columns ) ) return false;
        if( ! supportToReadIndex || ! another.supportToReadIndex ) return true;
        return isEqualIncies( indices, another.indices );
    }

    private boolean isEqualColumns( Map<String,TableColumn> map1, Map<String,TableColumn> map2 ) {
        if( map1.size() != map2.size() ) return false;
        for( String key : map1.keySet() ) {
            if( ! map2.containsKey( key ) ) return false;
            TableColumn c1 = map1.get( key );
            TableColumn c2 = map2.get( key );
            if( ! c1.isEqual(c2) ) return false;
        }
        return true;
    }

    private boolean isEqualIncies( Map<String,TableIndex> map1, Map<String,TableIndex> map2 ) {
        if( map1.size() != map2.size() ) return false;
        for( String key : map1.keySet() ) {
            if( ! map2.containsKey( key ) ) return false;
            TableIndex idx1 = map1.get( key );
            TableIndex idx2 = map2.get( key );
            if( ! idx1.isEqual(idx2) ) return false;
        }
        return true;
    }

    public List<TableColumn> getColumnsToAdd( TableLayout another ) {
        if( another == null )
            return new ArrayList<>( this.columns.values() );
        List<TableColumn> columns = new ArrayList<>();
        for( String key : this.columns.keySet() ) {
            if( ! another.columns.containsKey(key) ) {
                columns.add( this.columns.get(key) );
            }
        }
        return columns;
    }

    public List<TableColumn> getColumnsToDrop( TableLayout another ) {
        if( another == null )
            return new ArrayList<>( this.columns.values() );
        List<TableColumn> columns = new ArrayList<>();
        for( String key : another.columns.keySet() ) {
            if( ! this.columns.containsKey(key) ) {
                columns.add( another.columns.get(key) );
            }
        }
        return columns;
    }

    public List<TableColumn> getColumnsToModify( TableLayout another ) {
        if( another == null )
            return new ArrayList<>( this.columns.values() );
        List<TableColumn> columns = new ArrayList<>();
        for( String key : this.columns.keySet() ) {
            if( another.columns.containsKey(key) ) {
                // do not check PK dirrefence
                if( ! this.columns.get(key).isEqual( another.columns.get(key), false, false ) ) {
                    columns.add( this.columns.get(key) );
                }
            }
        }
        return columns;
    }

    public List<TableIndex> getIndicesToAdd( TableLayout another ) {
        if( ! supportToReadIndex || ! another.supportToReadIndex ) return new ArrayList<>();
        if( another == null ) return new ArrayList<>( this.indices.values() );
        List<TableIndex> indices = new ArrayList<>();
        for( String key : this.indices.keySet() ) {
            if( ! another.indices.containsKey(key) ) {
                indices.add( this.indices.get(key) );
            }
        }
        return indices;
    }

    public List<TableIndex> getIndicesToDrop( TableLayout another ) {
        if( ! supportToReadIndex || ! another.supportToReadIndex ) return new ArrayList<>();
        if( another == null )
            return new ArrayList<>( this.indices.values() );
        List<TableIndex> indices = new ArrayList<>();
        for( String key : another.indices.keySet() ) {
            if( ! this.indices.containsKey(key) ) {
                indices.add( another.indices.get(key) );
            }
        }
        return indices;
    }

    public List<TableIndex> getIndicesToModify( TableLayout another ) {
        if( ! supportToReadIndex || ! another.supportToReadIndex ) return new ArrayList<>();
        if( another == null )
            return new ArrayList<>( this.indices.values() );
        List<TableIndex> indices = new ArrayList<>();
        for( String key : this.indices.keySet() ) {
            if( another.indices.containsKey(key) ) {
                // do not check PK dirrefence
                if( ! this.indices.get(key).isEqual( another.indices.get(key) ) ) {
                    indices.add( this.indices.get(key) );
                }
            }
        }
        return indices;
    }

    public boolean isPkEqual( TableLayout another ) {
        if( another == null || pkColumns.size() != another.pkColumns.size() ) return false;
        return pkColumns.keySet().toString().equals( another.pkColumns.keySet().toString() );
    }

    public boolean isSupportToReadIndex() {
        return supportToReadIndex;
    }

    public void setSupportToReadIndex( boolean supportToReadIndex ) {
        this.supportToReadIndex = supportToReadIndex;
    }
}
