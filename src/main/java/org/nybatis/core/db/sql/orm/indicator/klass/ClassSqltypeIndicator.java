package org.nybatis.core.db.sql.orm.indicator.klass;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.model.NDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.nybatis.core.db.datasource.driver.DatabaseName.*;

/**
 * Class to SqlType Indicator
 *
 * Indicate Java class to SqlType considering various Databases.
 *
 * @author nayasis@gmail.com
 * @since 2017-11-22
 */
public class ClassSqltypeIndicator {

    private Map<Class,ClassSqltypeDialect> map = new HashMap<>();

    private boolean initialized = false;

    public final static ClassSqltypeIndicator $ = new ClassSqltypeIndicator();

    private ClassSqltypeIndicator() {}

    private synchronized void init() {
        if( initialized ) return;

        map.put( null,                new ClassSqltypeDialect(SqlType.NULL)        );
        map.put( String.class,        new ClassSqltypeDialect(SqlType.VARCHAR,100) );
        map.put( StringBuilder.class, new ClassSqltypeDialect(SqlType.VARCHAR,100) );
        map.put( StringBuffer.class,  new ClassSqltypeDialect(SqlType.VARCHAR,100) );
        map.put( char.class,          new ClassSqltypeDialect(SqlType.VARCHAR,100) );
        map.put( Character.class,     new ClassSqltypeDialect(SqlType.VARCHAR,100) );
        map.put( boolean.class,       new ClassSqltypeDialect(SqlType.BOOLEAN)     );
        map.put( Boolean.class,       new ClassSqltypeDialect(SqlType.BOOLEAN)     );
        map.put( int.class,           new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( Integer.class,       new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( double.class,        new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( Double.class,        new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( float.class,         new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( Float.class,         new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( byte.class,          new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( Byte.class,          new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( short.class,         new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( Short.class,         new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( long.class,          new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( Long.class,          new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( BigInteger.class,    new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( BigDecimal.class,    new ClassSqltypeDialect(SqlType.DECIMAL)     );
        map.put( byte[].class,        new ClassSqltypeDialect(SqlType.BLOB)        );
        map.put( Byte[].class,        new ClassSqltypeDialect(SqlType.BLOB)        );
        map.put( Date.class,          new ClassSqltypeDialect(SqlType.DATE)        );
        map.put( Calendar.class,      new ClassSqltypeDialect(SqlType.DATE)        );
        map.put( NDate.class,         new ClassSqltypeDialect(SqlType.DATE)        );

        DatabaseName[] mysql = { MY_SQL, MARIA };
        map.get( int.class        ).add( mysql, SqlType.INT           );
        map.get( Integer.class    ).add( mysql, SqlType.INT           );
        map.get( double.class     ).add( mysql, SqlType.DOUBLE        );
        map.get( Double.class     ).add( mysql, SqlType.DOUBLE        );
        map.get( float.class      ).add( mysql, SqlType.FLOAT         );
        map.get( Float.class      ).add( mysql, SqlType.FLOAT         );
        map.get( byte.class       ).add( mysql, SqlType.TINYINT       );
        map.get( Byte.class       ).add( mysql, SqlType.TINYINT       );
        map.get( short.class      ).add( mysql, SqlType.SMALLINT      );
        map.get( Short.class      ).add( mysql, SqlType.SMALLINT      );
        map.get( long.class       ).add( mysql, SqlType.BIGINT        );
        map.get( Long.class       ).add( mysql, SqlType.BIGINT        );
        map.get( BigInteger.class ).add( mysql, SqlType.BIGINT        );
        map.get( BigDecimal.class ).add( mysql, SqlType.REAL          );
        map.get( byte[].class     ).add( mysql, SqlType.LONGVARBINARY );
        map.get( Byte[].class     ).add( mysql, SqlType.LONGVARBINARY );

        map.get( Date.class     ).add( H2, SqlType.TIMESTAMP );
        map.get( Calendar.class ).add( H2, SqlType.TIMESTAMP );
        map.get( NDate.class    ).add( H2, SqlType.TIMESTAMP );

        initialized = true;
    }

    public SqlType getSqlType( Class klass, DatabaseName dbName ) {
        init();
        if( ! map.containsKey(klass) ) return SqlType.VARCHAR;
        return map.get( klass ).getSqlType( dbName );
    }

    public Integer getLength( Class klass, DatabaseName dbName ) {
        init();
        if( ! map.containsKey(klass) ) return 1000;
        return map.get( klass ).getLength( dbName );
    }

    public SqlType getSqlType( Class klass, String environmentId ) {
        return getSqlType( klass, DatasourceManager.getDatabaseName(environmentId) );
    }

    public Integer getLength( Class klass, String environmentId ) {
        return getLength( klass, DatasourceManager.getDatabaseName(environmentId) );
    }

}
