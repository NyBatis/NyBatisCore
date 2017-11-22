package org.nybatis.core.db.sql.orm.indicator.sqltype;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.sql.mapper.SqlType;

import java.util.HashMap;
import java.util.Map;

import static org.nybatis.core.db.datasource.driver.DatabaseName.MARIA;
import static org.nybatis.core.db.datasource.driver.DatabaseName.MY_SQL;
import static org.nybatis.core.db.sql.mapper.SqlType.*;

/**
 * java.sql.Types to SqlType Indicator
 *
 * Indicate java.sql.Types to SqlType considering various Databases.
 *
 * @author nayasis@gmail.com
 * @since 2017-11-22
 */
public class SqltypeIndicator {

    private Map<Integer,SqltypeDialect> map = new HashMap<>();

    private boolean initialized = false;

    public final static SqltypeIndicator $ = new SqltypeIndicator();

    private SqltypeIndicator() {}

    private synchronized void init() {
        if( initialized ) return;

        for( SqlType sqlType : SqlType.values() ) {
            map.put( sqlType.code, new SqltypeDialect(sqlType) );
        }

        DatabaseName[] mysql = { MY_SQL, MARIA };
        map.get( NUMERIC.code ).add( mysql, REAL          );
        map.get( DECIMAL.code ).add( mysql, REAL          );
        map.get( CLOB.code    ).add( mysql, LONGVARBINARY );

        initialized = true;
    }

    public SqlType getSqlType( int sqlType, DatabaseName dbName ) {
        init();
        if( ! map.containsKey(sqlType) ) return SqlType.VARCHAR;
        return map.get( sqlType ).getSqlType( dbName );
    }

    public SqlType getSqlType( int sqlType, String environmentId ) {
        return getSqlType( sqlType, DatasourceManager.getDatabaseName(environmentId) );
    }

}
