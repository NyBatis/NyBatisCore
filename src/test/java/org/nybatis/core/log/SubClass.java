package org.nybatis.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nayasis@gmail.com
 * @since 2018-06-29
 */
public class SubClass {

    private static final Logger logger = LoggerFactory.getLogger( SubClass.class );

    public void test() {
        logger.debug( "This is subclass by logback !" );
        NLogger.debug( "This is subclass by NLogger !" );
    }
}
