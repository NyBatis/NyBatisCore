package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.sql.orm.vo.TableLayout;

/**
 * ORM entity table handler
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public interface OrmTableHandler<T> extends Cloneable {

    /**
     * get table layout on database
     * @return table layout
     */
    TableLayout getLayout();

    /**
     * is table exists on database
     * @return true if table exists
     */
    boolean exists();

    /**
     * is not table exists on database
     *
     * @return true if table does not exist
     */
    boolean notExists();

    /**
     * drop table layout on database
     *
     * @return true if entity changed success
     */
    boolean drop();

    /**
     * set table layout on database
     *
     * @return true if entity changed success
     */
    boolean set();

}
