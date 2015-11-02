package org.nybatis.core.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
public class TypeUtilTest {

    @Test
    public void isArray() {

        List<String> list = new ArrayList<>();

        list.add( "A" );
        list.add( "B" );
        list.add( "C" );
        list.add( "D" );

        Assert.assertEquals( true, TypeUtil.isArray( list ) );

    }

}
