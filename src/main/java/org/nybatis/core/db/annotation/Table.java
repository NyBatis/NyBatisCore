package org.nybatis.core.db.annotation;

import java.lang.annotation.*;
import org.nybatis.core.conf.Const;

/**
 * Mapping attrubutes with domain class and database table
 *
 * @author nayasis@gmail.com
 *
 */
@Inherited
@Target( ElementType.TYPE ) // Allow to class and interface
@Retention( RetentionPolicy.RUNTIME )
public @interface Table {

    /**
     * default value (table name)
     *
     * @return default value (table name)
     */
    String value() default "";

    /**
     * table name
     *
     * @return table name
     */
    String name()  default Const.db.DEFAULT_TABLE_NAME;

    /**
     * schema name
     *
     * @return schema name
     */
    String schema() default "";

    /**
     * environment id
     *
     * @return environment id
     */
    String environmentId() default Const.db.DEFAULT_ENVIRONMENT_ID;

    /**
     * index attribute
     *
     * @return additional index when create table
     */
    Index[] indices() default {};

}