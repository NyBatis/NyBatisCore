package org.nybatis.core.db.etc;

import ch.qos.logback.classic.Level;
import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.log.NLoggerPrinter;
import org.nybatis.core.worker.WorkerExecute;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-22
 */
public class SqlLogHider {
    public static SqlLogHider $ = new SqlLogHider();
    public void hideDebugLog( WorkerExecute worker ) {

        NLoggerPrinter logger = NLogger.getLogger( Const.db.LOG_SQL );
        Level prevLogLevel = logger.getLevel();

        if( prevLogLevel != null && prevLogLevel.levelInt >= Level.DEBUG.levelInt ) {
            logger.setLevel( Level.INFO );
        }

        try {
            worker.execute();
        } finally {
            logger.setLevel( prevLogLevel );
        }

    }

}
