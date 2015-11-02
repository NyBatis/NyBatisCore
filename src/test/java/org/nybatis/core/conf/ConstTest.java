package org.nybatis.core.conf;

import org.nybatis.core.log.NLogger;
import org.testng.annotations.Test;

/**
 * Created by nayasis@gmail.com on 2015-07-02.
 */
public class ConstTest {

    @Test
    public void pathTest() {
        NLogger.debug( "root : {}", Const.path.getRoot() );
        NLogger.debug( "base : {}", Const.path.getBase() );
    }
}
