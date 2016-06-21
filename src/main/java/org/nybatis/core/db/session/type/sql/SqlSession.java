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
 * The primary Java interface for SQL management in NyBatis.
 * Through this interface you can execute sql or manage transaction.
 *
 * <pre>
 * SqlSession session = SessionManager.openSession();
 *
 * Map param = new HashMap();
 * param.put( <font color=red>"id"</font>, <font color=blue>"merong"</font> );
 *
 * session.sql( <font color='green'>"DELETE FROM TABLE WHERE id = <font color=red>#{id}</font>"</font>, param ).update();
 *
 * session.rollback();
 *
 * session.sql( <font color='green'>"DELETE FROM TABLE WHERE id = <font color=red>#{id}</font>"</font>, <font color=blue>"A001"</font> ).update();
 * session.sql( <font color='green'>"DELETE FROM TABLE WHERE id = <font color=red>#{id}</font>"</font>, <font color=blue>"B002"</font> ).update();
 *
 * session.commit();
 *
 * </pre>
 *
 * @author nayasis@gmail.com
 *
 */
public interface SqlSession {

	/**
	 * Set SQL ID in mapper
	 * @param  id SQL ID consisted with <font color="blue">MAPPER_FILE_NAME</font><font color="red"><b>.</b></font><font color="green">sqlId</font>.
	 * @return executor to run sql
	 */
	SessionExecutor sqlId( String id );

	/**
	 * Set SQL ID in mapper
	 * @param  id SQL ID consisted with <font color="blue">MAPPER_FILE_NAME</font><font color="red"><b>.</b></font><font color="green">sqlId</font>.
	 * @param  parameter Parameter to bind with sql. it consists with Map, Bean or Primitive (int, Integer, Date... )
	 * @return executor to run sql
	 */
	SessionExecutor sqlId( String id, Object parameter );

	/**
	 * Set plain SQL.
	 * @param  sql plain sql
	 * @return executor to run sql
	 */
	SessionExecutor sql( String sql );

	/**
	 * Set plain SQL.
	 * @param  sql plain sql
	 * @param  parameter Parameter to bind with sql. it consists with Map, Bean or Primitive (int, Integer, Date... )
	 * @return executor to run sql
	 */
	SessionExecutor sql( String sql, Object parameter );

	/**
	 * Set SQL ID in mapper for batch execution.
	 * @param  id SQL ID consisted with <font color="blue">MAPPER_FILE_NAME</font><font color="red"><b>.</b></font><font color="green">sqlId</font>.
	 * @param  parameters Parameters to bind with sql. Parameter consists with Map, Bean or Primitive (int, Integer, Date... )
	 * @return batch mode executor to run sql
	 */
	BatchExecutor batchSqlId( String id, List<?> parameters );

	/**
	 * Set plain SQLs for batch execution.
	 * @param  sqlList plain sqls
	 * @return batch mode executor to run sql
	 */
	BatchExecutor batchSql( List<String> sqlList );

	/**
	 * Set SQL ID in mapper for batch execution.
	 * @param  sql plain sql
	 * @param  parameters Parameters to bind with sql. Parameter consists with Map, Bean or Primitive (int, Integer, Date... )
	 * @return batch mode executor to run sql
	 */
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
	 * Begin transaction forcibly
	 */
	SqlSession beginTransaction();

	/**
	 * End transaction forcibly
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

	/**
	 * Open ORM Session.
	 * environment id is determined by <b>environmentId</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class.
	 *
	 * @param tableName     database table name
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	<T> OrmSession<T> openOrmSession( String tableName, Class<T> domainClass );

	/**
	 * Open ORM Session.
	 * table name is determined by <b>name</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class or domain class's uncamel name.
	 * and environment id is determined by <b>environmentId</b>'s value of {@link org.nybatis.core.db.annotation.Table} annotation in domain class.
	 *
	 * @param domainClass   domain class represent to database table
	 * @return OrmSession
	 */
	<T> OrmSession<T> openOrmSession( Class<T> domainClass );

	/**
	 * Open seperated Sql Session for nested transaction handling.
	 * Environment id is along to current SqlSession.
	 *
	 * @return seperated SqlSession (Connection is different from another SqlSession)
	 */
	SqlSession openSeperateSession();

	/**
	 * Get name of database connected with session.
	 *
	 * @return database name
	 */
	String getDatabaseName();

}
