package org.nybatis.core.model;

import org.nybatis.core.conf.Const;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.reflection.Reflector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Serializable;

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

    @Test
    public void serializeViaFile() {

        String before, after;

        SampleVo sampleVo = new SampleVo( "nayasis", new NDate( "1977-01-22" ) );

        before = sampleVo.toString();

        String testFile = Const.path.getBase() + "/test.serialized.obj";

        FileUtil.writeObject( testFile, sampleVo );

        sampleVo = FileUtil.readObject( testFile );

        after = sampleVo.toString();

        Assert.assertEquals( after, before );

        FileUtil.delete( testFile );

    }

    private static class SampleVo implements Serializable {

        private String name;
        private NDate  birth;

        public SampleVo( String name, NDate birth ) {
            this.name = name;
            this.birth = birth;
        }

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public NDate getBirth() {
            return birth;
        }

        public void setBirth( NDate birth ) {
            this.birth = birth;
        }

        public String toString() {
            return Reflector.toJson( this );
        }

    }

}