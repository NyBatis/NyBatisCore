package org.nybatis.core.db.session.executor.batch;

import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.batch.module.Logs;
import org.nybatis.core.db.session.executor.batch.module.Statements;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.log.NLogger;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchStatementExecutor extends AbstractBatchExecutor {

	public BatchStatementExecutor( String token, SqlProperties properties ) {
		super( token, properties );
	}

	protected Statements getStatements() { return new Statements() {

		@Override
		public Object generateKey( SqlBean sqlBean ) {
			return sqlBean.getProperties().getEnvironmentId();
		}

		@Override
		public SqlBean generateSqlBean( SqlNode sqlNode, Object param ) {
			return new SqlBean( (SqlNode) param );
		}

		@Override
		public Statement getStatement( Object key, SqlBean sqlBean ) throws SQLException {
			return getConnection().createStatement();
		}

		@Override
		public void addBatch( Statement statement, SqlBean sqlBean ) throws SQLException {
			statement.addBatch( sqlBean.getSql() );
		}

	};}

	protected Logs getLogs() { return new Logs() {

		private Map<Object, List<String>> sqlPool = new HashMap<>();

		@Override
		public void set( Object key, SqlBean sqlBean ) {
			if( ! sqlPool.containsKey(key) ) {
				sqlPool.put( key, new ArrayList<>() );
			}
			sqlPool.get( key ).add( sqlBean.getOrignalSql() );
		}

		@Override
		public int getParamSize( Object key ) {
			return sqlPool.get( key ).size();
		}

		public void clear() {
			sqlPool.clear();
		}

		@Override
		public String getLog( Object key ) {

			if( ! NLogger.isDebugEnabled() ) return "";

			StringBuilder log = new StringBuilder();

			log.append( "\t- [SQLS] :" );

			for( String sql : sqlPool.get(key) ) {
				log.append( "\n\t" ).append( sql );
			}

			return log.toString();

		}

	};}

}
