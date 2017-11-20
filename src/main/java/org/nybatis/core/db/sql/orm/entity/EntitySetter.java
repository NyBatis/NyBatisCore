package org.nybatis.core.db.sql.orm.entity;

import org.nybatis.core.db.sql.orm.sqlmaker.OrmTableSqlMaker;

/**
 * ORM entity setter
 *
 * @author nayasis@gmail.com
 * @since 2017-11-20
 */
public class EntitySetter {

    private OrmTableSqlMaker tableSqlMaker;

    public EntitySetter( String environmentId ) {
        tableSqlMaker = new OrmTableSqlMaker( environmentId );
    }

    public void dropTable( Class klass ) {
        if( tableSqlMaker.notExists(klass) ) return;
        tableSqlMaker.getTableLayout( klass );

    }

}
