package org.nybatis.core.db.sql.orm.indicator.sqltype;

import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.sql.mapper.SqlType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-22
 */
public class SqltypeDialect {

    private SqlType  sqlType;

    private Map<DatabaseName,SqlType> types   = new HashMap<>();

    public SqltypeDialect( SqlType sqlType ) {
        this( sqlType, null );
    }

    public SqltypeDialect( SqlType sqlType, Integer length ) {
        this.sqlType = sqlType;
    }

    public SqltypeDialect add( DatabaseName dbName, SqlType sqlType ) {
        if( sqlType != null ) types.put( dbName, sqlType );
        return this;
    }

    public SqltypeDialect add( DatabaseName[] dbNames, SqlType sqlType ) {
        for( DatabaseName name : dbNames ) {
            add( name, sqlType );
        }
        return this;
    }

    public SqlType getSqlType( DatabaseName dbName ) {
        if( types.containsKey(dbName) ) {
            return types.get( dbName );
        } else {
            return sqlType;
        }
    }

}
