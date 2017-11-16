package org.nybatis.core.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * column attribute to create or modify table
 *
 * @author nayasis@gmail.com
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention( RetentionPolicy.RUNTIME )
public @interface Column {

    /**
     * type (VARCHAR, CLOB, NUMBER, etc... )
     *
     * default is determined by field's type
     *
     * @see java.sql.Types
     * @return column's sql type
     */
    int type() default Integer.MIN_VALUE;

    /**
     * length
     *
     * @return column's length
     */
    int length() default Integer.MIN_VALUE;

    /**
     * precision on floating point
     *
     * used in Number type column definition
     *
     * @return column's precision
     */
    int precision() default Integer.MIN_VALUE;

    /**
     * comment
     *
     * @return column's comment
     */
    String comment() default "";

    /**
     * default value
     *
     * @return column's default value
     */
    String defaultValue() default "";

    /**
     * not null attribute
     *
     * @return not null
     */
    boolean notNull() default false;

}