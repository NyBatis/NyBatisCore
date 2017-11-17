package org.nybatis.core.db.sql.orm.reader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import org.nybatis.core.db.annotation.ColumnIgnore;
import org.nybatis.core.db.annotation.Index;
import org.nybatis.core.db.annotation.Pk;
import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.orm.vo.Column;
import org.nybatis.core.db.sql.orm.vo.IndexLayout;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.reflection.core.CoreReflector;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

/**
 * Table creation layout reader from Entity
 *
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class EntityLayoutReader {

    public TableLayout getTableLayout( Class klass ) {

        if( klass == null || klass == Object.class ) return null;

        Table tableAnnotation = getTableAnnotation( klass );
        if( tableAnnotation == null ) return null;

        TableLayout tableLayout = new TableLayout();
        tableLayout.setTableName( getTableName(klass) );
        setColumnMeta( klass, tableLayout );
        setIndices( klass,tableAnnotation,tableLayout );

        return tableLayout;
    }

    public static String getTableName( Class klass ) {
        Table annotation = getTableAnnotation( klass );
        String tableName = Validator.nvl( annotation.value(), annotation.name() );
        if( StringUtil.isEmpty(tableName) ) {
            tableName = StringUtil.toUncamel( klass.getSimpleName() );
        }
        return tableName;
    }

    private void setIndices( Class klass, Table tableAnnotation, TableLayout tableLayout ) {
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
            tableLayout.addIndex( new IndexLayout( index.name(), index.columns() ) );
            indices.add( new IndexLayout( index.name(), index.columns() ) );
        }
        for( IndexLayout indexLayout : indices ) {
            tableLayout.addIndex( indexLayout );
        }
    }

    private void hasIndexProperColumnName( Class klass, Index index, TableLayout tableLayout ) {
        for( String columnName : index.columns() ) {
            if( ! tableLayout.hasColumnName(columnName) ) {
                throw new SqlConfigurationException( "there is no column name[{}] in index[{}] at klass[{}]", columnName, index.name(), klass.getName() );
            }
        }
    }

    private void setColumnMeta( Class klass, TableLayout tableLayout ) {

        CoreReflector reflector = new CoreReflector();

        Map<String,Column> columns = new LinkedHashMap<>();

        for( Field field : reflector.getFields(klass) ) {
            columns.put( field.getName(), toColumnModel( field ) );
        }

        for( Method method : reflector.getMethods(klass) ) {
            Column column = toColumnModel( method );
            if( column != null && columns.containsKey( column.getKey() ) ) {
                columns.put( column.getKey(), column );
            }
        }

        for( Column column : columns.values() ) {
            if( column == null ) continue;
            tableLayout.addColumn( column );
        }

    }

    private Column toColumnModel( Field field ) {

        if( field.isAnnotationPresent(JsonIgnore.class)   ) return null;
        if( field.isAnnotationPresent(ColumnIgnore.class) ) return null;

        SqlType sqlType = SqlType.find( field.getType() );

        Column column = new Column();
        column.setKey( field.getName() );
        column.setDataType( sqlType.code );
        column.setSize( sqlType.length );

        if( field.isAnnotationPresent(Pk.class) ) {
            column.setPk( true );
        }

        if( field.isAnnotationPresent( org.nybatis.core.db.annotation.Column.class ) ) {
            setColumn( column, field.getAnnotation( org.nybatis.core.db.annotation.Column.class ) );
        }

        return column;

    }

    private Column toColumnModel( Method method ) {

        if( method.isAnnotationPresent(JsonIgnore.class) ) return null;
        if( ! method.isAnnotationPresent( org.nybatis.core.db.annotation.Column.class ) && ! method.isAnnotationPresent(Pk.class) ) return null;

        String key = method.getName().replaceFirst( "^(get|set)", "" );
        key = StringUtil.toUncamel( key );
        key = StringUtil.toCamel( key );

        SqlType sqlType = SqlType.find( method.getReturnType() );

        Column column = new Column();
        column.setKey( key );
        column.setDataType( sqlType.code );
        column.setSize( sqlType.length );

        if( method.isAnnotationPresent(Pk.class) ) {
            column.setPk( true );
        }

        if( method.isAnnotationPresent( org.nybatis.core.db.annotation.Column.class ) ) {
            setColumn( column, method.getAnnotation( org.nybatis.core.db.annotation.Column.class ) );
        }

        return column;

    }

    private void setColumn( Column column, org.nybatis.core.db.annotation.Column columnAnnotation ) {
        if( columnAnnotation.type() != Integer.MIN_VALUE ) column.setDataType( columnAnnotation.type() );
        if( columnAnnotation.precision() > 0 ) column.setPrecison( columnAnnotation.precision() );
        if( columnAnnotation.length()    > 0 ) column.setSize( columnAnnotation.length() );
        column.setNotNull( columnAnnotation.notNull() );
        column.setDefinedByAnnotation( true );
    }

    public static Table getTableAnnotation( Class klass ) {
        if( klass == null ) return null;
        Class cursor = klass;
        while( true ) {
            if( cursor.isAnnotationPresent( Table.class ) )
                return (Table) cursor.getAnnotation( Table.class );
            cursor = cursor.getSuperclass();
            if( cursor == Object.class ) return null;
        }
    }

    public org.nybatis.core.db.annotation.Column getColumnAnnotation( Field field ) {
        field.setAccessible( true );
        return field.getAnnotation( org.nybatis.core.db.annotation.Column.class );
    }

}
