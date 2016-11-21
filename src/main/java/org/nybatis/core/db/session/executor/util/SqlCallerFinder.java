package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.db.session.executor.SqlExecutor;
import org.nybatis.core.db.session.executor.batch.AbstractBatchExecutor;
import org.nybatis.core.db.session.type.orm.OrmBatchExecutorImpl;
import org.nybatis.core.db.session.type.orm.OrmListExecutorImpl;
import org.nybatis.core.db.session.type.orm.OrmSessionImpl;
import org.nybatis.core.db.session.type.sql.BatchExecutorImpl;
import org.nybatis.core.db.session.type.sql.ListExecutorImpl;
import org.nybatis.core.db.session.type.sql.SessionExecutorImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Sql caller finder
 *
 * @author nayasis
 * @since 2016-10-21
 */
public class SqlCallerFinder {

    Set<String> excludeClass = new HashSet<>();

    public SqlCallerFinder() {
        exclude( Thread.class                );
        exclude( SqlCallerFinder.class       );
        exclude( DbUtils.class               );
        exclude( SqlExecutor.class           );
        exclude( SessionExecutorImpl.class   );
        exclude( AbstractBatchExecutor.class );
        exclude( BatchExecutorImpl.class     );
        exclude( ListExecutorImpl.class      );
        exclude( OrmListExecutorImpl.class   );
        exclude( OrmSessionImpl.class        );
        exclude( OrmBatchExecutorImpl.class  );
    }

    private void exclude( Class klass ) {
        excludeClass.add( klass.getName() );
    }

    /**
     * get caller information
     * <pre>
     *  for example :
     *    - org.nybatis.core.db.session.SqlSessionSqliteTest.case10_initDummyDataByStatementBatch (SqlSessionSqliteTest.java:122)
     * </pre>
     * @return sql caller information
     */
    public String get() {
        for( StackTraceElement e : Thread.currentThread().getStackTrace() ) {
            String className = e.getClassName();
            if( excludeClass.contains( className ) ) continue;
            return String.format( "%s.%s (%s:%s)", e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber() );
        }
        return "";
    }

}
