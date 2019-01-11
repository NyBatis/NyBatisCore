package org.nybatis.core.db.annotation;

import java.lang.annotation.*;

/**
 * Primary key on database
 *
 * @author nayasis@gmail.com
 *
 */
@Inherited
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention( RetentionPolicy.RUNTIME )
public @interface Pk {
}