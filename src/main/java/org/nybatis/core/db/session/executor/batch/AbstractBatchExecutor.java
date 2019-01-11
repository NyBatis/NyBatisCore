package org.nybatis.core.db.session.executor.batch;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.session.executor.SelectKeyExecutor;
import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.batch.module.Logs;
import org.nybatis.core.db.session.executor.batch.module.Statements;
import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StopWatch;
import org.nybatis.core.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBatchExecutor {

	private static final Logger logger = LoggerFactory.getLogger( Const.db.LOG_BATCH );

	protected String         token;
	protected SqlProperties  properties;

	public AbstractBatchExecutor( String token, SqlProperties properties ) {
		this.token      = token;
		this.properties = properties;
	}

	public int executeSql( List<?> parameters, Integer commitCount ) throws SqlException {
		return executeSql( null, parameters, commitCount );
	}

	public int executeSql( SqlNode sqlNode, List<?> parameters, Integer commitCount ) throws SqlException {

		if( parameters == null || parameters.size() == 0 ) return 0;

		if( sqlNode != null ) {
			properties = sqlNode.getProperties().merge( properties );
			if( Validator.isNotEmpty( sqlNode.getProperties().getEnvironmentId() ) ) {
				properties.setEnvironmentId( sqlNode.getProperties().getEnvironmentId() );
			}
		}

		String environmentId = properties.getRepresentativeEnvironmentId();

		Statements  statements  = getStatements().init( token, environmentId );
		SqlBean     sqlBean     = null;

		int executeCount = 0;

		TransactionManager.begin( token );

		try {

			for( Object param : parameters ) {

				sqlBean = statements.generateSqlBean( sqlNode, param );

				sqlBean.init( properties );

				selectKeys( sqlBean );

				sqlBean.build();

				Object key = statements.getKey( sqlBean );

				statements.addBatch( key, sqlBean );

				if( commitCount != null ) {
					executeCount++;
					if( executeCount % commitCount == 0 ) {
						executeBatch( statements );
						statements.commit();
					}
				}

			}

			executeBatch( statements );

			if( commitCount != null ) {
				statements.commit();
			}

			statements.close();

			return parameters.size();

		} catch( SQLException e ) {

			SqlException exception = new SqlException( e, "{} Error (code:{}) {}\n{}", sqlBean, e.getErrorCode(), e.getMessage(), sqlBean.getDebugSql() );
			exception.setErrorCode( e.getErrorCode() );
			exception.setDatabaseName( sqlBean.getDatasourceAttribute().getDatabase() );

			throw exception;

		} catch( SqlException e ) {

			e.setDatabaseName( sqlBean.getDatasourceAttribute().getDatabase() );
			throw e;

		} catch( ClassCastException e ) {
			SqlException exception = new SqlException( e, "{} parameter binding error. ({})\n{}", sqlBean, e.getMessage(), sqlBean.getDebugSql() );
			exception.setDatabaseName( sqlBean.getDatasourceAttribute().getDatabase() );
			throw exception;

		} finally {
			statements.clear();
		}

	}

	private void executeBatch( Statements statements ) throws SQLException {

		Map<Object, Long> elapsedTimes = new HashMap<>();

		StopWatch watcher = new StopWatch();

		for( Object key : statements.keySet() ) {

			watcher.reset();

			try {

				statements.get(key).executeBatch();

				elapsedTimes.put( key, watcher.elapsedMiliSeconds() );

			} catch( BatchUpdateException e ) {

				int successCount = 0;
				int failCount = 0;
				int notAavailable = 0;

				int[] updateCounts = e.getUpdateCounts();

				// {1,1,1,0,0,0,0,0,0,0} : sqlite

				for( int i = 0, iCnt = updateCounts.length; i < iCnt; i++ ) {
					if( updateCounts[i] >= 0 ) {
						successCount++;
					} else if( updateCounts[i] == Statement.SUCCESS_NO_INFO ) {
						notAavailable++;
					} else if( updateCounts[i] == Statement.EXECUTE_FAILED ) {
						failCount++;
					}
				}

				NLogger.debug( ">> successCount : {}, notAavailable : {}, failCount : {}", successCount, notAavailable, failCount );

				SqlException exception;

				try {
					exception = new SqlException( e, "{} Error (code:{}) {}", statements.getKeyInfo( key ), e.getErrorCode(), e.getMessage() );
				} catch( Exception error ) {
					exception = new SqlException( e );
				}

				exception.setErrorCode( e.getErrorCode() );

				throw exception;

			}

		}


		if( logger.isDebugEnabled() ) {
			DbUtils.logCaller();
			for( Object key : statements.keySet() ) {
				logger.debug( ">> {} executed, elapsed:[{}]ms", statements.getKeyInfo( key ), elapsedTimes.get(key) );
			}
        }

		statements.clear();

	}

	private void selectKeys( SqlBean sqlBean ) {
		new SelectKeyExecutor( token ).selectKeys( sqlBean );
	}

	protected abstract Statements getStatements();
	protected abstract Logs       getLogs();

}
