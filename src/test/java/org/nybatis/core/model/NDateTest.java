package org.nybatis.core.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * NDate test
 *
 * @author nayasis
 * @since 2016-06-02
 */
public class NDateTest {

    @Test
    public void testSetSecond() throws Exception {

        NDate date = new NDate( "2016-06-02 13:59:21" );

        date.setSecond( 0 );

        assertEquals( "2016-06-02 13:59:00", date.toString() );

    }

    @Test
    public void testGetBeginningOfMonthDate() throws Exception {

        NDate date = new NDate( "2016-06-02 13:59:21" );

        NDate beginningOfMonthDate = date.getBeginningOfMonthDate();
        NDate endOfMonthDate       = date.getEndOfMonthDate();

        assertEquals( beginningOfMonthDate.toString(), "2016-06-01 00:00:00" );
        assertEquals( endOfMonthDate.toString(), "2016-06-30 23:59:59" );

        assertEquals( beginningOfMonthDate.getMillisecond(), 0 );
        assertEquals( endOfMonthDate.getMillisecond(), 999 );

    }

}