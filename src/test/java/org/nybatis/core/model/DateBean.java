package org.nybatis.core.model;

import org.nybatis.core.reflection.Reflector;

import java.util.Date;

/**
 * @author 1002159
 * @since 2016-02-22
 */
public class DateBean {

    public NDate ndate;
    public Date  date;

    public void init() {
        ndate = new NDate();
        date  = new Date();
    }

    public String toString() {
        return Reflector.toString( this );
    }

}
