package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.annotation.NotSupportCache;
import org.nybatis.core.db.annotation.SupportCache;
import org.nybatis.core.db.annotation.SupportCacheOnlyResult;
import org.nybatis.core.model.NMap;

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
public interface SessionExecutor {

	/**
	 * Retrieve a single row
	 *
	 */
	@SupportCache
	NMap select();

	/**
	 * Retrieve a single row or single value
	 *
	 * @param returnType  Map or Bean (if you want to return row), Primitive (if you want to single value)
	 * @return single row or single value
	 */
	@SupportCache
	<T> T select( Class<T> returnType );

	/**
	 * get list executor
	 *
	 * @return list executor
	 */
	@SupportCache
	ListExecutor list();

	/**
	 * Execute statement
	 *
	 * <br/><br/>
	 * if it called, Transaction is activate automatically
	 *
	 * @return affected count by execute (insert, update, delete ... )
	 */
	@NotSupportCache
	int execute();

	/**
	 * Execute function or procedure
	 *
	 * <pre>
	 * String sqlId = '{ #{result:rs:out} = call function( #{name} ) }'
	 *
	 * Map<String, Object> param = new HashMap<>();
	 * param.put( "name", "nayasis" );
	 *
	 * SqlSession session = SessionManager.openSession();
	 * NMap result = session.call( sqlId, param );
	 *
	 * System.out.println( result.toStringForDebug() );
	 *
	 * <b>** value of result **</b>
	 *
	 * ------------------------------------
	 * | key   | type            | val    |
	 * ------------------------------------
	 * | result| java.lang.String| SUCCESS|
	 * ------------------------------------
	 *
	 * type and value of result are case by case.
	 *
	 * </pre>
	 *
	 * @param sqlId             Id mapped to xml or Statement
	 * @param parameter         parameter consist of Map, Bean or Primitive (int, Integer, Date... )
	 * @param listReturnTypes   (optional) return type of ResultSet(s). basic return type is NMap.
	 * @return
	 */
	@SupportCacheOnlyResult
	NMap call();

	/**
	 * Execute function or procedure
	 *
	 * <pre>
	 * String sqlId = '{ #{result:rs:out} = call function( #{name} ) }'
	 *
	 * Map<String, Object> param = new HashMap<>();
	 * param.put( "name", "nayasis" );
	 *
	 * SqlSession session = SessionManager.openSession();
	 * NMap result = session.call( sqlId, param );
	 *
	 * System.out.println( result.toStringForDebug() );
	 *
	 * <b>** value of result **</b>
	 *
	 * ------------------------------------
	 * | key   | type            | val    |
	 * ------------------------------------
	 * | result| java.lang.String| SUCCESS|
	 * ------------------------------------
	 *
	 * type and value of result are case by case.
	 *
	 * </pre>
	 *
	 * @param sqlId             Id mapped to xml or Statement
	 * @param parameter         parameter consist of Map, Bean or Primitive (int, Integer, Date... )
	 * @param listReturnTypes   (optional) return type of ResultSet(s). basic return type is NMap.
	 * @return
	 */
	@SupportCacheOnlyResult
	NMap call( Class<?>... listReturnTypes );

	/**
	 * Execute function or procedure
	 *
	 * <pre>
	 * String sqlId = '{ #{result:rs:out} = call function( #{name} ) }'
	 *
	 * Map<String, Object> param = new HashMap<>();
	 * param.put( "name", "nayasis" );
	 *
	 * SqlSession session = SessionManager.openSession();
	 * String result = session.call( sqlId, param, String.class );
	 *
	 * System.out.println( result );
	 *
	 * <b>** value of result **</b>
	 *
	 * SUCCESS
	 *
	 * </pre>
	 *
	 * @param sqlId             Id mapped to xml or Statement
	 * @param parameter         parameter consist of Map, Bean or Primitive (int, Integer, Date... )
	 * @param returnType        Map or Bean (if you want to return row), Primitive (if you want to single value)
	 * @param listReturnTypes   (optional) return type of ResultSet(s). basic return type is NMap.
	 * @return
	 */
	@SupportCacheOnlyResult
	<T> T call( Class<T> returnType, Class<?>... listReturnTypes );

	/**
	 * Set parameter
	 *
	 * @param parameter parameter to set
	 * @return self instance
	 */
	SessionExecutor setParameter( Object parameter );

	/**
	 * Set true if statements should be auto commited forcedly when has been executed.
	 *
	 * @param yn whether or not of statement to be commited.
	 * @return self instance
	 */
	SessionExecutor setAutoCommitAtOnce( boolean yn );

	/**
	 * Cache statements should not be cached at once when has been executed.
	 *
	 * @return self instance
	 */
	SessionExecutor disableCache();

	/**
	 * Clear cache
	 *
	 * @return self instance
	 */
	SessionExecutor clearCache();

}
