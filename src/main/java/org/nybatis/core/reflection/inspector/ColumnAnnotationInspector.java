package org.nybatis.core.reflection.inspector;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.reflection.deserializer.ColumnBeanDeserializer;
import org.nybatis.core.reflection.deserializer.ColumnBooleanDeserializer;
import org.nybatis.core.reflection.serializer.column.ColumnBeanSerializer;
import org.nybatis.core.reflection.serializer.column.ColumnBooleanSerializer;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.Types;

/**
 * Column Annotation Introspector
 *
 * @author nayasis@gmail.com
 * @since 2016-03-11
 */
public class ColumnAnnotationInspector extends JacksonAnnotationIntrospector {

    @Override
    public Object findSerializer( Annotated annotated ) {

        if( ! annotated.hasAnnotation(Column.class) || ! isStringType(annotated) ) {
            return super.findSerializer( annotated );
        }

        Class classType = annotated.getRawType();

        if( Types.isNotString(classType) ) {
            if( Types.isBoolean(classType) ) {
                return ColumnBooleanSerializer.class;
            } else if( ! Types.isPrimitive(classType) ) {
                return ColumnBeanSerializer.class;
            }
        }

        return super.findSerializer( annotated );

    }

    @Override
    public Object findDeserializer( Annotated annotated ) {

        if( ! annotated.hasAnnotation(Column.class) || ! isStringType(annotated) ) {
            return super.findDeserializer( annotated );
        }

        Class classType;
        try {
            classType = ( (AnnotatedMethod) annotated ).getRawParameterType( 0 );
        } catch( Exception e ) {
            classType = annotated.getRawType();
        }

        if( Types.isNotString(classType) ) {
            if( Types.isBoolean(classType) ) {
                return ColumnBooleanDeserializer.class;
            } else if( ! Types.isPrimitive(classType) ) {
                return ColumnBeanDeserializer.class;
            }
        }

        return super.findDeserializer( annotated );

    }

    private boolean isStringType( Annotated annotated ) {
        Column annotation = annotated.getAnnotation( Column.class );
        switch( annotation.type() ) {
            case java.sql.Types.VARCHAR :
            case java.sql.Types.CHAR :
            case java.sql.Types.CLOB :
            case java.sql.Types.NCHAR :
            case java.sql.Types.NCLOB :
            case java.sql.Types.LONGVARBINARY :
            case java.sql.Types.LONGNVARCHAR :
                return true;
        }
        return false;
    }

    @Override
    public PropertyName findNameForSerialization( Annotated annotated ) {
        return getPropertyName( annotated );
    }

    @Override
    public PropertyName findNameForDeserialization( Annotated annotated ) {
        return getPropertyName( annotated );
    }

    private PropertyName getPropertyName( Annotated annotated ) {

        Column column = annotated.getAnnotation( Column.class );
        if ( column != null && StringUtil.isNotEmpty(annotated.getName()) ) {
            String name = column.name();
            name = StringUtil.toUncamel( name );
            name = StringUtil.toCamel( name );
            return new PropertyName( name );
        }

        JsonProperty jsonProperty = annotated.getAnnotation( JsonProperty.class );
        if( jsonProperty != null ) {
            if( StringUtil.isNotEmpty(jsonProperty.value()) ) {
                return new PropertyName( jsonProperty.value() );
            } else {
                return PropertyName.USE_DEFAULT;
            }
        }

        return null;

    }

}
