package org.nybatis.core.db.annotation;

import java.lang.annotation.*;

/**
 * ignorance indicator on table creation
 *
 * @author nayasis@gmail.com
 *
 */
@Inherited
@Target({ ElementType.FIELD })
@Retention( RetentionPolicy.RUNTIME )
public @interface ColumnIgnore {}