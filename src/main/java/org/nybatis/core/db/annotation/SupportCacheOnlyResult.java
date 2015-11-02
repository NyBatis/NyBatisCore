package org.nybatis.core.db.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method supports to cache Database execute result only.
 *
 * @author nayasis@gmail.com
 *
 */
@Target( {TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE} )
@Retention( RetentionPolicy.SOURCE )
public @interface SupportCacheOnlyResult {

}
