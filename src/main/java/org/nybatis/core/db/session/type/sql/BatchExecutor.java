package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.annotation.NotSupportCache;

/**
 * The primary Java interface for SQL management in Nybatis.
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
public interface BatchExecutor {

	/**
	 * Execute statement in batch mode without commit process.
	 *
	 * <br/><br/>
	 * if it called, Transaction is activate automatically
	 *
	 * @param transactionSize  size to commit execution
	 * @return affected count by execute (insert, update, delete ... )
	 */
	@NotSupportCache
	int execute( Integer transactionSize );

	/**
	 * Execute statement in batch mode without commit process.
	 *
	 * <br/><br/>
	 * if it called, Transaction is activate automatically
	 *
	 * @return affected count by execute (insert, update, delete ... )
	 */
	@NotSupportCache
	int execute();

}
