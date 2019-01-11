package org.nybatis.core.db.sql.orm.indicator.klass;

import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.sql.mapper.SqlType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-22
 */
public class ClassSqltypeDialect {

    private SqlType  sqlType;
    private Integer  length;

    private Map<DatabaseName,SqlType> types   = new HashMap<>();
    private Map<DatabaseName,Integer> lengths = new HashMap<>();

    public ClassSqltypeDialect( SqlType sqlType ) {
        this( sqlType, null );
    }

    public ClassSqltypeDialect( SqlType sqlType, Integer length ) {
        this.sqlType = sqlType;
        this.length = length;
    }

    public ClassSqltypeDialect add( DatabaseName dbName, SqlType sqlType ) {
        return add( dbName, sqlType, null );
    }

    public ClassSqltypeDialect add( DatabaseName dbName, SqlType sqlType, Integer length ) {
        if( sqlType != null ) types.put( dbName, sqlType );
        if( length  != null ) lengths.put( dbName, length );
        return this;
    }

    public ClassSqltypeDialect add( DatabaseName[] dbNames, SqlType sqlType ) {
        return add( dbNames, sqlType, null );
    }

    public ClassSqltypeDialect add( DatabaseName[] dbNames, SqlType sqlType, Integer length ) {
        for( DatabaseName name : dbNames ) {
            add( name, sqlType, length );
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

    public Integer getLength( DatabaseName dbName ) {
        if( lengths.containsKey(dbName) ) {
            return lengths.get( dbName );
        } else {
            return length;
        }
    }

}
