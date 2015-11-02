package org.nybatis.core.db.session.executor.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Administrator
 * @since 2015-09-22
 */
public class HeaderTest {

    @Test
    public void testKeySet() throws Exception {

        Header header = new Header();

        header.add( "1", null );
        header.add( "2", null );
        header.add( "3", null );
        header.add( "4", null );

        assertEquals( header.keySet().toString(), "[1, 2, 3, 4]" );

    }

}