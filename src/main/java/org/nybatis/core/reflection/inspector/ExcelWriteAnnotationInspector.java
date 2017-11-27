package org.nybatis.core.reflection.inspector;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.nybatis.core.file.annotation.ExcelHeader;
import org.nybatis.core.validation.Validator;

/**
 * Excel Annotation Introspector
 *
 * @author nayasis@gmail.com
 * @since 2016-03-11
 */
public class ExcelWriteAnnotationInspector extends JacksonAnnotationIntrospector {

    @Override
    public PropertyName findNameForSerialization( Annotated annotated ) {

        ExcelHeader annotation = annotated.getAnnotation( ExcelHeader.class );

        if ( annotation != null && Validator.isNotEmpty(annotation.value()) ) {
            return new PropertyName( annotation.value() );
        }

        return PropertyName.USE_DEFAULT;

    }

    @Override
    public PropertyName findNameForDeserialization( Annotated annotated ) {

        ExcelHeader annotation = annotated.getAnnotation( ExcelHeader.class );

        if ( annotation != null && Validator.isNotEmpty(annotation.value()) ) {
            return new PropertyName( annotation.value() );
        }

        return PropertyName.USE_DEFAULT;

    }

}
