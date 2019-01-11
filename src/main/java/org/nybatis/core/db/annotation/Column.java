package org.nybatis.core.db.annotation;

import java.lang.annotation.*;

/**
 * column attribute to create or modify table
 *
 * @author nayasis@gmail.com
 *
 */
@Inherited
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention( RetentionPolicy.RUNTIME )
public @interface Column {

    String name() default "";

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