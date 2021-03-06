package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.model.NMap;

/**
 * The primary Java interface for SQL management in NayasisCommon library.
 * Through this interface you can execute sql or manage transaction.
 *
 * <pre>
 * SqlSession session = SessionManager.openSession();
 *
 * Map param = new HashMap();
 * param.put( <font style="color:red">"id"</font>, <font style="color:blue">"merong"</font> );
 *
 * session.executeUpdate( <font style="color:green">"DELETE FROM TABLE WHERE id = <font style="color:red">#{id}</font>"</font>, param );
 *
 * session.rollback();
 *
 * session.executeUpdate( <font style="color:green">"DELETE FROM TABLE WHERE id = <font style="color:red">#{id}</font>"</font>, <font style="color:blue">"A001"</font> );
 * session.executeUpdate( <font style="color:green">"DELETE FROM TABLE WHERE id = <font style="color:red">#{id}</font>"</font>, <font style="color:blue">"B002"</font> );
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
	 * @return single row data
	 */
	NMap select();

	/**
	 * Retrieve a single row or single value
	 *
	 * @param returnType	Map or Bean (if you want to return row), Primitive (if you want to single value)
	 * @param <T> 			expected class of return
	 * @return single row or single value
	 */
	<T> T select( Class<T> returnType );

	/**
	 * get list executor
	 *
	 * @return list executor
	 */
	ListExecutor list();

	/**
	 * Execute statement
	 *
	 * <pre>
	 * if it called, Transaction is activate automatically
	 * </pre>
	 *
	 * @return affected count by execute (insert, update, delete ... )
	 */
	int execute();

	/**
	 * Execute function or procedure
	 *
	 * <pre>
	 * String sqlId = '{ #{result:rs:out} = call function( #{name} ) }'
	 *
	 * Map&lt;String, Object&gt; param = new HashMap&lt;&gt;();
	 * param.put( "name", "nayasis" );
	 *
	 * SqlSession session = SessionManager.openSession();
	 * NMap result = session.sqlId( sqlId, param ).call();
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
	 * @return result of out parameters
	 */
	NMap call();

	/**
	 * Execute function or procedure
	 *
	 * <pre>
	 * String sqlId = '{ #{result:rs:out} = call function( #{name} ) }'
	 *
	 * Map&lt;String, Object&gt; param = new HashMap&lt;&gt;();
	 * param.put( "name", "nayasis" );
	 *
	 * SqlSession session = SessionManager.openSession();
	 * NMap result = session.sqlId( sqlId, param ).call();
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
	 * @param listReturnTypes return type of ResultSet(s). basic return type is NMap.
	 * @return result of out parameters
	 */
	NMap call( Class<?>... listReturnTypes );

	/**
	 * Execute function or procedure
	 *
	 * <pre>
	 * String sqlId = '{ #{result:rs:out} = call function( #{name} ) }'
	 *
	 * Map&lt;String, Object&gt; param = new HashMap&lt;&gt;();
	 * param.put( "name", "nayasis" );
	 *
	 * SqlSession session = SessionManager.openSession();
	 * String result = session.sqlId( sqlId, param ).call( String.class );
	 *
	 * System.out.println( result );
	 *
	 * <b>** value of result **</b>
	 *
	 * SUCCESS
	 *
	 * </pre>
	 *
	 * @param returnType        Map or Bean (if you want to return row), Primitive (if you want to single value)
	 * @param listReturnTypes   (optional) return type of ResultSet(s). basic return type is NMap.
	 * @param <T>				expected class of return
	 * @return result of function or precedure
	 */
	<T> T call( Class<T> returnType, Class<?>... listReturnTypes );

	/**
	 * Set parameter
	 *
	 * @param parameter parameter to set. it consists with Map, Bean or Primitive (int, Integer, Date... )
	 * @return self instance
	 */
	SessionExecutor setParameter( Object parameter );

	/**
	 * Add parameter
	 *
	 * @param parameter parameter to add. it consists with Map, Bean or Primitive (int, Integer, Date... )
	 * @return self instance
	 */
	SessionExecutor addParameter( Object parameter );

	/**
	 * Add parameter
	 *
	 * @param key	key
	 * @param value parameter to add. it consists with Map, Bean or Primitive (int, Integer, Date... )	 * @param key
	 * @return self instance
	 */
	SessionExecutor addParameter( String key, Object value );

	/**
	 * get all parameters to be input. <br>
	 *
	 * it also have <b>select key</b> parameters called in sql.
	 *
	 * @return input parameters
	 */
	NMap getParameters();

	/**
	 * Get name of database connected with session.
	 *
	 * @return database name
	 */
	String getDatabaseName();

}
