package org.nybatis.core.db.sql.reader.table;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.nybatis.core.validation.Validator;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class IndexLayout {

    private String      name;
    private Set<String> columnNames = new LinkedHashSet<>();

    public IndexLayout( String name, Set<String> columnNames ) {
        setName( name );
        setColumnNames( columnNames );
    }

    public IndexLayout( String name, String[] columnNames ) {
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
            this.columnNames.add( columnName );
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
        return String.format( "{name:'%s', columns:'%s'}", name, columnNames );
    }

}
