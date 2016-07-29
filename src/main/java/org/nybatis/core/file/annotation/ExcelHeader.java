package org.nybatis.core.file.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mapping attrubutes with domain class and database table
 *
 * @author nayasis@gmail.com
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD }) // Allow to class and interface
@Retention( RetentionPolicy.RUNTIME )
public @interface ExcelHeader {

    /**
     * Special value that indicates that handlers should use the default
     * name (derived from method or field name) for property.
     *
     * @return header title value
     * @since 2.1
     */
    String value() default "";

}