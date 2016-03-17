package org.nybatis.core.db.session.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
public class SampleVo {

    public String name;
    public int    age;

    public SampleVo firstChild;

    public List<SampleVo> children = new ArrayList<>();

//    public String toString() {
//        return Reflector.toString( this );
//    }

}
