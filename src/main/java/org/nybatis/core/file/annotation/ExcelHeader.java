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

    /**
     * Marker value used to indicate that no index has been specified.
     * Used as the default value as annotations do not allow "missing"
     * values.
     *
     * @since 2.4
     */
    int INDEX_UNKNOWN = -1;

    /**
     * Property that indicates numerical index of this property (relative
     * to other properties specified for the Object). This index
     * is typically used by binary formats, but may also be useful
     * for schema languages and other tools.
     *
     * @return header sort index
     * @since 2.4
     */
    int index() default INDEX_UNKNOWN;

}