package org.nybatis.core.db.sql.orm.indicator.sqltype;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.sql.mapper.SqlType;

import java.util.HashMap;
import java.util.Map;

import static org.nybatis.core.db.datasource.driver.DatabaseName.*;
import static org.nybatis.core.db.sql.mapper.SqlType.*;

/**
 * java.sql.Types to SqlType Indicator
 *
 * Indicate java.sql.Types to SqlType considering various Databases.
 *
 * @author nayasis@gmail.com
 * @since 2017-11-22
 */
public class ClassDialector {

    private Map<SqlType,SqltypeDialect> map = new HashMap<>();

    public final static ClassDialector $ = new ClassDialector();

    private ClassDialector() {
        init();
    }

    private void init() {

        for( SqlType sqlType : SqlType.values() ) {
            map.put( sqlType, new SqltypeDialect(sqlType) );
        }

        DatabaseName[] mysql = { MYSQL, MARIA };
        map.get( NUMERIC ).add( mysql, REAL );
        map.get( DECIMAL ).add( mysql, REAL );

        map.get( DATE ).add( H2, TIMESTAMP );

    }

    public SqlType getSqlType( Class<?> klass, DatabaseName dbName ) {
        SqlType sqlType = SqlType.find( klass );
        if( ! map.containsKey(sqlType) ) return SqlType.VARCHAR;
        return map.get( sqlType ).getSqlType( dbName );
    }

    public SqlType getSqlType( Class<?> klass, String environmentId ) {
        return getSqlType( klass, DatasourceManager.getDatabaseName(environmentId) );
    }

}
