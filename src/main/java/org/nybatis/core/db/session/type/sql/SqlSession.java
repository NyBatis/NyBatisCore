package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.session.handler.ConnectionHandler;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.exception.unchecked.BaseRuntimeException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * The primary Java interface for SQL management in NayasisCommon library.
 * Through this interface you can execute sql or manage transaction.
 *
 * <pre>
 * SqlSession session = SessionManager.openSession();
 *
 * Map param = new HashMap();
 * param.put( <font color=red>"id"</font>, <font color=blue>"merong"</font> );
 *
 * session.executeUpdate( <font color='green'>"DELETE FROM TABLE WHERE id = <font color=red>#{id}</font>"</font>, param );
 *
 * session.rollback();
 *
 * session.executeUpdate( <font color='green'>"DELETE FROM TABLE WHERE id = <font color=red>#{id}</font>"</font>, <font color=blue>"A001"</font> );
 * session.executeUpdate( <font color='green'>"DELETE FROM TABLE WHERE id = <font color=red>#{id}</font>"</font>, <font color=blue>"B002"</font> );
 *
 * session.commit();
 *
 * </pre>
 *
 * @author nayasis@gmail.com
 *
 */
public interface SqlSession {

	SessionExecutor sqlId( String id );
	SessionExecutor sqlId( String id, Object parameter );
	SessionExecutor sql( String sql );
	SessionExecutor sql( String sql, Object parameter );

	BatchExecutor batchSqlId( String id, List<?> parameters );
	BatchExecutor batchSql( List<String> sqlList );
	BatchExecutor batchSql( String sql,  List<?> parameters );

	/**
	 * Commit and end transaction if it was activated
	 */
	SqlSession commit();

	/**
	 * Rollback and end transaction if it was activated
	 */
	SqlSession rollback();

	/**
	 * Begin transaction forcedly
	 */
	SqlSession beginTransaction();

	/**
	 * End transaction forcedly
	 */
	SqlSession endTransaction();

	/**
	 * Check transaction is activate
	 *
	 * @return is transaction activate
	 */
	boolean isTransactionBegun();

	/**
	 * Use Connection of default environment's datasource. <br/><br/>
	 *
	 * It is nesessary sometimes to handle {@link Connection} directly because {@link SqlSession} dose not support for geek functionality or Vendor Dependent usage. <br/>
	 * But {@link Connection#close} is not working in Connection Pool (ex. DataSource in WAS).
	 * So, Resources like {@link Statement}, {@link PreparedStatement}, {@link CallableStatement} and {@link ResultSet} do not close and cause memory leak. <br/>
	 * The Connection managed by SqlSession is safe for memory leak. So only you can do is just use. <br/>
	 * When {@link Connection#close} is called, this safe Connection release all resources occupied by itself automatically. <br/>
	 *
	 * @param worker  worker to use Connection
	 * @throws BaseRuntimeException When error occurs.
	 */
	SqlSession useConnection( ConnectionHandler worker ) throws BaseRuntimeException;

	/**
	 * Set environment id
	 *
	 * @param id environment id
	 */
	SqlSession setEnvironmentId( String id );

	<T> OrmSession<T> openOrmSession( String tableName, Class<T> domainClass );

	<T> OrmSession<T> openOrmSession( Class<T> domainClass );

}
