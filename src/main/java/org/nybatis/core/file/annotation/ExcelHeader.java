package org.nybatis.core.file.annotation;

import java.lang.annotation.*;

/**
 * Mapping attrubutes with domain class and database table
 *
 * @author nayasis@gmail.com
 *
 */
@Inherited
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