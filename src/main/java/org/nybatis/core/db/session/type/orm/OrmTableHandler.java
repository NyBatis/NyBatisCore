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
     * @return
     */
    TableLayout getLayout();

    /**
     * is table exists on database
     * @return
     */
    boolean exists();

    /**
     * is not table exists on database
     * @return
     */
    boolean notExists();

    /**
     * drop table layout on database
     */
    OrmTableHandler<T> drop();

    /**
     * set table layout on database
     */
    OrmTableHandler<T> set();

}
