package org.nybatis.core.db.sql.orm.vo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import static org.nybatis.core.util.StringUtil.nvl;

/**
 * Table Index
 *
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class TableIndex {

    private String      name;
    private Set<String> columnNames = new LinkedHashSet<>();

    public TableIndex( String name, Set<String> columnNames ) {
        setName( name );
        setColumnNames( columnNames );
    }

    public TableIndex( String name, String[] columnNames ) {
        setName( name );
        setColumnNames( columnNames );
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Set<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames( Set<String> columnNames ) {
        this.columnNames = columnNames;
    }

    public void setColumnNames( String[] columnNames ) {
        this.columnNames.clear();
        if( Validator.isEmpty(columnNames) ) return;
        for( String columnName : columnNames ) {
            this.columnNames.add( StringUtil.toUncamel(columnName).toLowerCase() );
        }
    }

    public void setColumnNames( List<String> columnNames ) {
        this.columnNames.clear();
        if( Validator.isEmpty(columnNames) ) return;
        for( String columnName : columnNames ) {
            this.columnNames.add( columnName );
        }
    }

    public String toString() {
        return String.format( "{name:%s, columns:%s}", name, columnNames );
    }

    public boolean isEqual( TableIndex another ) {
        if( another == null ) return false;
        if( StringUtil.isNotEqual( name, another.name ) ) return false;
        if( columnNames.size() != another.columnNames.size() ) return false;
        return columnNames.toString().equals( another.columnNames.toString() );
    }

}
