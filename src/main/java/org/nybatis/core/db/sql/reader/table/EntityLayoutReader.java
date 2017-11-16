package org.nybatis.core.db.sql.reader.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.nybatis.core.db.annotation.Index;
import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.util.StringUtil;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class EntityLayoutReader {

    public TableLayout getTableLayout( Class klass ) {

        if( klass == null || klass == Object.class ) return null;

        if( ! klass.isAnnotationPresent(Table.class) ) return null;

        TableLayout tableLayout = new TableLayout();

        tableLayout.setTableName( getTableName( klass ) );



        tableLayout.setIndices( getIndex(klass, tableLayout) );

        return null;
    }

    private String getTableName( Class klass ) {
        Table tableAnnotation = getTableAnnotation( klass );
        String tableName = tableAnnotation.name();
        if( StringUtil.isEmpty(tableName) ) {
            tableName = StringUtil.toUncamel( klass.getSimpleName() );
        }
        return tableName;
    }

    private List<IndexLayout> getIndex( Class klass, TableLayout tableLayout ) {
        Table tableAnnotation = getTableAnnotation( klass );
        Map<String,IndexLayout> incices = new LinkedHashMap<>();
        List<IndexLayout> indices = new ArrayList<>();
        for( Index index : tableAnnotation.indexs() ) {
            if( StringUtil.isEmpty(index.name()) )
                throw new SqlConfigurationException( "Index(at klass:{}) must have name.", klass.getName() );
            if( index.columns().length == 0 )
                throw new SqlConfigurationException( "Index(at klass:{}) must have columns.", klass.getName() );
            if( incices.containsKey(index.name()) )
                throw new SqlConfigurationException( "there is duplicated Index name({}) on klass({}).", index.name(), klass.getName() );
            hasIndexProperColumnName( klass, index, tableLayout );
            indices.add( new IndexLayout( index.name(), index.columns() ) );
        }
        return indices;
    }

    private void hasIndexProperColumnName( Class klass, Index index, TableLayout tableLayout ) {
        for( String columnName : index.columns() ) {
            if( ! tableLayout.hasColumnName(columnName) ) {
                throw new SqlConfigurationException( "there is no column name[{}] in index[{}] at klass[{}]", columnName, index.name(), klass.getName() );
            }
        }
    }

    private Table getTableAnnotation( Class klass ) {
        return (Table) klass.getAnnotation( Table.class );
    }

}
