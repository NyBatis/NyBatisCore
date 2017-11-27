package org.nybatis.core.reflection.inspector;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.deserializer.ColumnBooleanDeserializer;
import org.nybatis.core.reflection.deserializer.ColumnBeanDeserializer;
import org.nybatis.core.reflection.serializer.column.ColumnBooleanSerializer;
import org.nybatis.core.reflection.serializer.column.ColumnBeanSerializer;
import org.nybatis.core.util.Types;

/**
 * Column Annotation Introspector
 *
 * @author nayasis@gmail.com
 * @since 2016-03-11
 */
public class ColumnAnnotationInspector extends JacksonAnnotationIntrospector {

//    @Override
//    public boolean hasIgnoreMarker( AnnotatedMember member ) {
//        return member.getAnnotation( Column.class ) == null;
//    }



    @Override
    public Object findSerializer( Annotated annotated ) {

        if( ! annotated.hasAnnotation( Column.class ) ) {
            return super.findContentSerializer( annotated );
        }

        Column annotation = getAnnotation( annotated );
        Class classType = annotated.getRawType();

        switch( annotation.type() ) {
            case java.sql.Types.VARCHAR :
            case java.sql.Types.CHAR :
            case java.sql.Types.CLOB :
            case java.sql.Types.NCHAR :
            case java.sql.Types.NCLOB :
            case java.sql.Types.LONGVARBINARY :
            case java.sql.Types.LONGNVARCHAR :

                if( Types.isNotString(classType) ) {
                    if( Types.isBoolean(classType) ) {
                        return ColumnBooleanSerializer.class;
                    } else if( Types.isPrimitive(classType) ) {
                        return super.findContentSerializer( annotated );
                    } else {
                        return ColumnBeanSerializer.class;
                    }
                }

        }

        return super.findContentSerializer( annotated );

    }

    @Override
    public Object findDeserializer( Annotated annotated ) {

        if( ! annotated.hasAnnotation( Column.class ) ) {
            return super.findContentDeserializer( annotated );
        }
        Column annotation = getAnnotation( annotated );
        Class classType = null;

        try {
            classType = ( (AnnotatedMethod) annotated ).getRawParameterType( 0 );
        } catch( Exception e ) {
            return super.findContentDeserializer( annotated );
        }

        if( classType == null ) {
            return super.findContentDeserializer( annotated );
        }

        switch( annotation.type() ) {
            case java.sql.Types.VARCHAR :
            case java.sql.Types.CHAR :
            case java.sql.Types.CLOB :
            case java.sql.Types.NCHAR :
            case java.sql.Types.NCLOB :
            case java.sql.Types.LONGVARBINARY :
            case java.sql.Types.LONGNVARCHAR :

                if( Types.isNotString(classType) ) {
                    if( Types.isBoolean(classType) ) {
                        return ColumnBooleanDeserializer.class;
                    } else if( Types.isPrimitive(classType) ) {
                        return super.findContentDeserializer( annotated );
                    } else {
                        return ColumnBeanDeserializer.class;
                    }
                }

        }

        return super.findContentDeserializer( annotated );

    }

    @Override
    public Object findSerializationContentConverter( AnnotatedMember a ) {
        NLogger.debug( a );
        return super.findSerializationContentConverter( a );
    }

    private Column getAnnotation( Annotated annotated ) {
        return annotated.getAnnotation( Column.class );
    }

}
