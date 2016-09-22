package org.nybatis.core.db.session;

import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.reflection.Reflector;

/**
 * OrmEntity
 *
 * @author nayasis
 * @since 2016-09-22
 */
@Table( SqlSessionSqliteTest.TABLE_NAME )
public class OrmEntity {

    public String listId;
    public String prodId;
    public String prodName;

    public String toString() {
        return Reflector.toString( this );
    }

}
