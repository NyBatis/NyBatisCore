package org.nybatis.core.clone.annotation;

import java.lang.annotation.*;

/**
 * marks one immutable to avoid cloning that some specific class, field, method
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention( RetentionPolicy.RUNTIME )
public @interface Immutable {}
