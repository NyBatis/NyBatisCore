package org.nybatis.core.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Primary key on database
 *
 * @author nayasis@gmail.com
 *
 */
@Target( ElementType.FIELD ) // Allow to class and interface
@Retention( RetentionPolicy.RUNTIME )
public @interface Pk {
}