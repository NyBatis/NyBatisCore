package org.nybatis.core.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.nybatis.core.validation.Validator;

/**
 * Index attribute to create or modify table
 *
 * @author nayasis@gmail.com
 *
 */
@Inherited
@Target(ElementType.METHOD)
public @interface Index {

    /**
     * Index name
     *
     * @return index name
     */
    String   name();

    /**
     * column names that consist index
     *
     * @return index column names
     */
    String[] columns();

}