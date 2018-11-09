package org.nybatis.core.db.sql.orm.reader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nybatis.core.db.annotation.*;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.orm.indicator.sqltype.ClassDialector;
import org.nybatis.core.db.sql.orm.indicator.sqltype.SqltypeDialector;
import org.nybatis.core.db.sql.orm.vo.TableColumn;
import org.nybatis.core.db.sql.orm.vo.TableIndex;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.reflection.core.CoreReflector;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.nybatis.core.util.StringUtil.toUncamel;

/**
 * Table creation layout reader from Entity
 *
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class EntityLayoutReader {

    private String environmentId;

    public EntityLayoutReader( String environmentId ) {
        this.environmentId = environmentId;
    }

    public TableLayout getTableLayout( Class klass ) {

        if( klass == null || klass == Object.class ) return null;

        Table tableAnnotation = getTableAnnotation( klass );
        if( tableAnnotation == null ) return null;

        TableLayout table = new TableLayout();
        table.setEnvironmentId( environmentId );
        table.setName( getTableName(klass) );
        setColumnMeta( klass, table );
        setIndices( tableAnnotation.indices(),table, klass );

        return table;
    }

    public static String getTableName( Class klass ) {
        Table annotation = getTableAnnotation( klass );
        if( annotation == null ) {
            return toUncamel( klass.getSimpleName() ).toUpperCase();
        } else {
            String tableName = Validator.nvl( annotation.value(), annotation.name() );
            if( StringUtil.isEmpty(tableName) ) {
                tableName = toUncamel( klass.getSimpleName() );
            }
            return tableName.toUpperCase();
        }
    }

    private void setIndices( Index[] annotations, TableLayout table, Class klass ) {
        Map<String,TableIndex> indices = new LinkedHashMap<>();
        for( Index index : annotations ) {
            if( StringUtil.isEmpty(index.name()) )
                throw new SqlConfigurationException( "Index(at class:{}) must have name.", klass.getName() );
            if( index.columns().length == 0 )
                throw new SqlConfigurationException( "Index(at class:{}) must have columns.", klass.getName() );
            if( indices.containsKey(index.name()) )
                throw new SqlConfigurationException( "there is duplicated Index name({}) on '{}'.", index.name(), klass.getName() );
            hasIndexProperColumnName( klass, index, table );
            indices.put( index.name(), new TableIndex( index.name(), index.columns() ) );
        }
        for( TableIndex index : indices.values() ) {
            String prefix = "IDX_" + table.getName() + "_";
            String indexName = StringUtil.toUncamel( index.getName() ).toUpperCase();
            indexName = prefix + indexName.replace( prefix, "" );
            index.setName( indexName );
            table.addIndex( index );
        }
    }

    private void hasIndexProperColumnName( Class klass, Index index, TableLayout table ) {
        for( String columnName : index.columns() ) {
            if( ! table.hasColumnName(columnName) ) {
                throw new SqlConfigurationException( "there is no column[{}] in index[{}] at \"{}\"", columnName, index.name(), klass.getName() );
            }
        }
    }

    private void setColumnMeta( Class klass, TableLayout table ) {

        CoreReflector reflector = new CoreReflector();

        Map<String,TableColumn> columns = new LinkedHashMap<>();

        for( Field field : reflector.getFields(klass) ) {
            columns.put( field.getName(), toColumnModel( field, table ) );
        }

        for( Method method : reflector.getMethods(klass) ) {
            TableColumn column = toColumnModel( method, table );
            if( column != null && columns.containsKey( column.getKey() ) ) {
                columns.put( column.getKey(), column );
            }
        }

        for( TableColumn column : columns.values() ) {
            if( column == null ) continue;
            table.addColumn( column );
        }

    }

    private TableColumn toColumnModel( Field field, TableLayout table ) {

        if( field.isAnnotationPresent(JsonIgnore.class)   ) return null;
        if( field.isAnnotationPresent(ColumnIgnore.class) ) return null;

        TableColumn column = new TableColumn( table );
        SqlType sqlType = ClassDialector.$.getSqlType( field.getType(), environmentId );
        column.setDataType( sqlType.code, sqlType.name );
        column.setSize( column.findColumnLength( field.getType() ) );
        column.setKey( field.getName() );

        if( field.isAnnotationPresent(Column.class) )
            setColumn( column, field.getAnnotation(Column.class) );
        if( field.isAnnotationPresent(Pk.class) )
            column.setPk( true );

        return column;

    }

    private TableColumn toColumnModel( Method method, TableLayout table ) {

        if( method.isAnnotationPresent(JsonIgnore.class) ) return null;
        if( ! method.isAnnotationPresent(Column.class) && ! method.isAnnotationPresent(Pk.class) ) return null;

        String key = method.getName().replaceFirst( "^(get|set)", "" );
        key = toUncamel( key );
        key = StringUtil.toCamel( key );

        TableColumn column = new TableColumn( table );
        column.setDataType( method.getReturnType() );
        column.setKey( key );

        if( method.isAnnotationPresent(Column.class) )
            setColumn( column, method.getAnnotation(Column.class) );
        if( method.isAnnotationPresent(Pk.class) )
            column.setPk( true );

        return column;

    }

    private void setColumn( TableColumn column, Column annotation ) {
        if( annotation.type() != Integer.MIN_VALUE ) {
            SqlType sqlType = SqltypeDialector.$.getSqlType( annotation.type(), environmentId );
            column.setDataType( sqlType.code );
        }
        if( StringUtil.isNotEmpty(annotation.name()) ) {
            column.setKey( annotation.name() );
        }
        if( StringUtil.isNotEmpty(annotation.defaultValue()) ) column.setDefaultValue( annotation.defaultValue() );
        if( annotation.precision() > 0 ) column.setPrecison( annotation.precision() );
        if( annotation.length()    > 0 ) column.setSize( annotation.length() );
        column.setNotNull( annotation.notNull() );
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

}
