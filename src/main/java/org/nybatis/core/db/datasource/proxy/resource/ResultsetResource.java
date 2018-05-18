package org.nybatis.core.db.datasource.proxy.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Connection resource manager like Statement or ResultSet
 *
 * @author nayasis@gmail.com
 * @since 2018-05-17
 */
public class ResultsetResource {

    public static final Savepoint RELEASE_RESOURCE = new Savepoint() {
        public String getSavepointName() throws SQLException {
            return ResultsetResource.class.getName() + ".CLEAR_RESOURCE";
        }
        public int getSavepointId() throws SQLException {
            return Integer.MIN_VALUE;
        }
    };

    private Set<Statement> poolStatement = new HashSet<>();
    private Set<ResultSet> poolResultset = new HashSet<>();
    private long           lastUsedTime  = System.nanoTime();
    private Runnable       runnable      = null;

    public void add( Object object ) {
        if( object == null ) return;
        if( object instanceof ResultSet ) {
            poolResultset.add( (ResultSet) object );
        } else if( object instanceof Statement ) {
            poolStatement.add( (Statement) object );
        }
    }

    public void resetLastUsedTime() {
        lastUsedTime = System.nanoTime();
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void release() {
        for( ResultSet resultset : poolResultset ) {
            if( resultset == null ) continue;
            try { resultset.close(); } catch( SQLException e ) {}
        }
        poolResultset.clear();
        for( Statement statement : poolStatement ) {
            if( statement == null ) continue;
            try { statement.close(); } catch( SQLException e ) {}
        }
        poolStatement.clear();
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable( Runnable runnable ) {
        this.runnable = runnable;
    }

    public void executeRunnable() {
        if( runnable != null ) runnable.run();
    }

}
