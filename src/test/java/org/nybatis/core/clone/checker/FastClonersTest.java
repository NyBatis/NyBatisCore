package org.nybatis.core.clone.checker;

import org.nybatis.core.clone.fastcloner.FastCloners;
import org.nybatis.core.clone.fastcloner.interfaces.DeepCloner;
import org.nybatis.core.clone.fastcloner.implement.ArrayCloner;
import org.nybatis.core.clone.fastcloner.implement.ListCloner;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class FastClonersTest {

    @Test
    public void test() {

        FastCloners fastCloners = new FastCloners();

        List<String> list = new ArrayList<>();

        DeepCloner cloner1 = fastCloners.getCloner( list );
        DeepCloner cloner2 = fastCloners.getCloner( new String[] { "A", "B", "C", "D" } );

        assertEquals( ListCloner.class, cloner1.getClass() );
        assertEquals( ArrayCloner.class, cloner2.getClass() );

    }

}