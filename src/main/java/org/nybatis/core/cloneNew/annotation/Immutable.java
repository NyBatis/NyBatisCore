package org.nybatis.core.cloneNew.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * marks one immutable to avoid cloning that some specific class, field, method
 */
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention( RetentionPolicy.RUNTIME )
public @interface Immutable {}
