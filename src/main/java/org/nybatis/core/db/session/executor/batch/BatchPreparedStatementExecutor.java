package org.nybatis.core.db.session.executor.batch;

import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.batch.module.Logs;
import org.nybatis.core.db.session.executor.batch.module.Statements;
import org.nybatis.core.db.session.executor.util.StatementController;
import org.nybatis.core.db.sql.sqlMaker.BindParam;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BatchPreparedStatementExecutor extends AbstractBatchExecutor {

	public BatchPreparedStatementExecutor( String token, SqlProperties properties ) {
		super( token, properties );
	}

	protected Statements getStatements() { return new Statements() {

		@Override
		public Object generateKey( SqlBean sqlBean ) {
			return sqlBean.getUniqueKeyQuery();
		}

		@Override
		public SqlBean generateSqlBean( SqlNode sqlNode, Object param ) {
			return new SqlBean( sqlNode, param );
		}

		@Override
		public Statement getStatement( Object key, SqlBean sqlBean ) throws SQLException {
			return getConnection().prepareStatement( sqlBean.getSql() );
		}

		@Override
		public void addBatch( Statement statement, SqlBean sqlBean ) throws SQLException {
			PreparedStatement psmt = (PreparedStatement) statement;
			new StatementController( sqlBean ).setParameter( psmt );
			psmt.addBatch();
		}

	};}

	protected Logs getLogs() { return new Logs() {

		private Map<Object, List<List<BindParam>>> paramListPool = new HashMap<>();
		private Map<Object, String>                sqlPool       = new HashMap<>();

		@Override
		public void set( Object key, SqlBean sqlBean ) {

			sqlPool.putIfAbsent( key, sqlBean.getOrignalSql() );

			if( ! paramListPool.containsKey( key) ) {
				paramListPool.put( key, new ArrayList<>() );
			}
			paramListPool.get( key ).add( sqlBean.getBindParams() );

		}

		@Override
		public int getParamSize( Object key ) {
			return paramListPool.get( key ).size();
		}

		public void clear() {
			paramListPool.clear();
			sqlPool.clear();
		}

		@Override
		public String getLog( Object key ) {

			StringBuilder log = new StringBuilder();

			log.append( "\t- [SQL] :\n" ).append( sqlPool.get( key ) );
			log.append( "\t- [PARAMETERS] :\n" );

			NList paramLog = new NList();

			for( List<BindParam> params : paramListPool.get(key) ) {
				for( BindParam param : params ) {
					paramLog.addRow( param.getKey(), param.getValue() );
				}

			}

			log.append( paramLog.toDebugString() );

			return log.toString();

		}

		@Override
		public String getDuplicatedParameters( Object key ) {

			NList log = new NList();

			Set<Integer> hashSet  = new HashSet<>();
			Set<Integer> inserted = new HashSet<>();

			for( List<BindParam> params : paramListPool.get(key) ) {

				NMap param = new NMap();

				for( BindParam bindParam : params ) {
					param.put( bindParam.getKey(), bindParam.getValue() );
				}

				int hash = param.hashCode();

				if( hashSet.contains( hash ) ) {
					if( ! inserted.contains( hash ) ) {
						log.addRow( param );
						inserted.add( hash );
					}
				} else {
					hashSet.add( hash );
				}

			}

			return log.toDebugString( true, true );

		}

	};}

}
