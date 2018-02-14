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

        NLoggerPrinter sqlLogger         = NLogger.getLogger( Const.db.LOG_SQL   );
        NLoggerPrinter batchLogger       = NLogger.getLogger( Const.db.LOG_BATCH );
        Level          prevSqlLogLevel   = sqlLogger.getLevel();
        Level          prevbatchLogLevel = batchLogger.getLevel();

        setLogLevel( sqlLogger, prevSqlLogLevel );
        setLogLevel( batchLogger, prevbatchLogLevel );

        try {
            worker.execute();
        } finally {
            sqlLogger.setLevel( prevSqlLogLevel );
            batchLogger.setLevel( prevbatchLogLevel );
        }

    }

    private void setLogLevel( NLoggerPrinter sqlLogger, Level prevSqlLogLevel ) {
        if( prevSqlLogLevel != null && prevSqlLogLevel.levelInt >= Level.DEBUG.levelInt ) {
            sqlLogger.setLevel( Level.INFO );
        }
    }

}
