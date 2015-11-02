package org.nybatis.core.db.session.executor.batch;

import org.nybatis.core.db.session.executor.SelectKeyExecutor;
import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.batch.module.Logs;
import org.nybatis.core.db.session.executor.batch.module.Statements;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StopWatcher;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBatchExecutor {

	protected String        token;
	protected SqlProperties properties;

	public AbstractBatchExecutor( String token, SqlProperties properties ) {
		this.token          = token;
		this.properties = properties;
	}

	public int executeSql( List<?> paramList, Integer commitCount ) {
		return executeSql( null, paramList, commitCount );
	}

	public int executeSql( SqlNode sqlNode, List<?> paramList, Integer commitCount ) {

		if( paramList == null || paramList.size() == 0 ) return 0;

		if( sqlNode != null ) {
			properties = sqlNode.getProperties().merge( properties );
		}

		if( commitCount != null && commitCount > 0 ) {
			properties.isAutocommit( true );
		} else {
			properties.isAutocommit( false );
			commitCount = null;
		}

		String environmentId = properties.getEnvironmentId();

		Statements  statements  = getStatements().init( token, environmentId );
		Logs logs        = getLogs();
		SqlBean sqlBean     = null;



		int executeCount = 0;

		TransactionManager.begin( token );

		try {

			for( Object param : paramList ) {

				sqlBean = statements.generateSqlBean( sqlNode, param );

				sqlBean.init( properties );

				selectKeys( sqlBean );

				sqlBean.build();

				Object key = statements.getKey( sqlBean );

				logs.set( key, sqlBean );

				statements.addBatch( key, sqlBean );

				if( commitCount != null ) {
					executeCount++;
					if( executeCount % commitCount == 0 ) {
						executeBatch( statements, logs );
						statements.commit();
					}
				}

			}

			executeBatch( statements, logs );

			if( properties.isAutocommit() ) {
				TransactionManager.commit( token );
			}

			statements.close();

			return paramList.size();

		} catch( SQLException e ) {

			SqlException exception = new SqlException( e, "{} Error (code:{}) {}\n{}", sqlBean, e.getErrorCode(), e.getMessage(), sqlBean.getDebugSql() );
			exception.setErrorCode( e.getErrorCode() );

			throw exception;

		} catch( ClassCastException e ) {
			NLogger.error( e );
			throw new SqlException( e, "{} parameter binding error. ({})\n{}", sqlBean, e.getMessage(), sqlBean.getDebugSql() );

		}

	}

	private void executeBatch( Statements statements, Logs logs  ) {

		Map<Object, Long> elapsedTimes = new HashMap<>();

		StopWatcher watcher = new StopWatcher();

		for( Object key : statements.keySet() ) {

			watcher.reset();

			try {

				statements.get(key).executeBatch();

				elapsedTimes.put( key, watcher.elapsedMiliSeconds() );

			} catch( SQLException e ) {

				SqlException exception = new SqlException( e, "{} Error (code:{}) {}\n{}",
						statements.getKeyInfo( key ), e.getErrorCode(), e.getMessage(), logs.getLog(key) );

				exception.setErrorCode( e.getErrorCode() );

				throw exception;
			}

		}

		if( NLogger.isDebugEnabled() ) {
			for( Object key : statements.keySet() ) {
				NLogger.debug( ">> {} executed:[{}]count(s), elapsed:[{}]ms\n{}", statements.getKeyInfo( key ), logs.getParamSize( key ), elapsedTimes.get(key), logs.getLog(key) );
			}
		}

		statements.clear();
		logs.clear();

	}

	private void selectKeys( SqlBean sqlBean ) {
		new SelectKeyExecutor( token ).selectKeys( sqlBean );
	}

	protected abstract Statements getStatements();
	protected abstract Logs       getLogs();

}
