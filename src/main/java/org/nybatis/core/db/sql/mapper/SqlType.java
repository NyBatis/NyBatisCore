package org.nybatis.core.db.sql.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nybatis.core.model.NDate;
import org.nybatis.core.util.Types;

public enum SqlType {

	BIT                     ( java.sql.Types.BIT                     , "BIT"                     )  ,
	TINYINT                 ( java.sql.Types.TINYINT                 , "TINYINT"                 )  ,
	SMALLINT                ( java.sql.Types.SMALLINT                , "SMALLINT"                )  ,
	INTEGER                 ( java.sql.Types.INTEGER                 , "INTEGER"                 )  ,
	INT                     ( java.sql.Types.INTEGER                 , "INT"                     )  , // shortcut for INTEGER
	BIGINT                  ( java.sql.Types.BIGINT                  , "BIGINT"                  )  ,
	FLOAT                   ( java.sql.Types.FLOAT                   , "FLOAT"                   )  ,
	REAL                    ( java.sql.Types.REAL                    , "REAL"                    )  ,
	DOUBLE                  ( java.sql.Types.DOUBLE                  , "DOUBLE"                  )  ,
	NUMERIC                 ( java.sql.Types.NUMERIC                 , "NUMERIC"                 )  ,
	DECIMAL                 ( java.sql.Types.DECIMAL                 , "DECIMAL"                 )  ,
	CHAR                    ( java.sql.Types.CHAR                    , "CHAR"                    )  ,
	VARCHAR                 ( java.sql.Types.VARCHAR                 , "VARCHAR"                 )  ,
	LONGVARCHAR             ( java.sql.Types.LONGVARCHAR             , "LONGVARCHAR"             )  ,
	DATE                    ( java.sql.Types.DATE                    , "DATE"                    )  ,
	TIME                    ( java.sql.Types.TIME                    , "TIME"                    )  ,
	TIMESTAMP               ( java.sql.Types.TIMESTAMP               , "TIMESTAMP"               )  ,
	BINARY                  ( java.sql.Types.BINARY                  , "BINARY"                  )  ,
	ARRAY                   ( java.sql.Types.ARRAY                   , "ARRAY"                   )  ,
	LIST                    ( 9001                                   , "LIST"                    )  ,
	BLOB                    ( java.sql.Types.BLOB                    , "BLOB"                    )  ,
	BLOB_BOXED              ( 9002                                   , "BLOB_BOXED"              )  , // shortcut for Byte[]
	CLOB                    ( java.sql.Types.CLOB                    , "CLOB"                    )  ,
	BOOLEAN                 ( java.sql.Types.BOOLEAN                 , "BOOLEAN"                 )  ,
	NULL                    ( java.sql.Types.NULL                    , "NULL"                    )  ,

	SYS_REFCURSOR           ( -10                                    , "SYS_REFCURSOR"           )  , // shortcut for oracle cursor
	CURSOR                  ( -10                                    , "CURSOR"                  )  , // shortcut for oracle cursor
	RESULT_SET              ( -10                                    , "RESULTSET"               )  , // shortcut for oracle cursor
	RS                      ( -10                                    , "RS"                      )  , // shortcut for oracle cursor
	ORACLE_RESULT_SET       ( -10                                    , "ORACLE_RESULTSET"        )  , // shortcut for oracle cursor

	// struct
	REF                     ( java.sql.Types.REF                     , "REF"                     )  ,
	STRUCT                  ( java.sql.Types.STRUCT                  , "STRUCT"                  )  ,

	// not figured out
	VARBINARY               ( java.sql.Types.VARBINARY               , "VARBINARY"               )  ,
	LONGVARBINARY           ( java.sql.Types.LONGVARBINARY           , "LONGVARBINARY"           )  ,
	OTHER                   ( java.sql.Types.OTHER                   , "OTHER"                   )  ,
	JAVA_OBJECT             ( java.sql.Types.JAVA_OBJECT             , "JAVA_OBJECT"             )  ,
	DISTINCT                ( java.sql.Types.DISTINCT                , "DISTINCT"                )  ,
	REF_CURSOR              ( java.sql.Types.REF_CURSOR              , "REF_CURSOR"              )  ,
	DATALINK                ( java.sql.Types.DATALINK                , "DATALINK"                )  ,
	ROWID                   ( java.sql.Types.ROWID                   , "ROWID"                   )  ,
	NCHAR                   ( java.sql.Types.NCHAR                   , "NCHAR"                   )  ,
	NVARCHAR                ( java.sql.Types.NVARCHAR                , "NVARCHAR"                )  ,
	LONGNVARCHAR            ( java.sql.Types.LONGNVARCHAR            , "LONGNVARCHAR"            )  ,
	NCLOB                   ( java.sql.Types.NCLOB                   , "NCLOB"                   )  ,
	SQLXML                  ( java.sql.Types.SQLXML                  , "SQLXML"                  )  ,
	TIME_WITH_TIMEZONE      ( java.sql.Types.TIME_WITH_TIMEZONE      , "TIME_WITH_TIMEZONE"      )  ,
	TIMESTAMP_WITH_TIMEZONE ( java.sql.Types.TIMESTAMP_WITH_TIMEZONE , "TIMESTAMP_WITH_TIMEZONE" )  ;

	private final int    code;
	private final String name;

	private static Map<Integer, SqlType> codes = new LinkedHashMap<Integer, SqlType>();
	private static Map<String,  SqlType> names = new LinkedHashMap<String,  SqlType>();

	static {
		for( SqlType type : SqlType.values() ) {
			codes.put( type.code, type );
			names.put( type.name, type );
		}
	}

	SqlType( int code, String string ) {
		this.code = code;
		this.name = string;
	}

	public static SqlType find( int code )  {
		return codes.get( code );
	}

	public static SqlType find( String name )  {
		if( name == null ) return null;
		return names.get( name.toUpperCase() );
	}

	public int toCode() {
		return code;
	}

	public String toString() {
		return name;
	}

	public static SqlType findByValue( Object value ) {
		if( value == null ) return SqlType.NULL;
        return find( value.getClass() );
	}

	public static SqlType find( Class<?> klass ) {

		if( klass == null ) return SqlType.NULL;

		if( klass == String.class        ) return SqlType.VARCHAR;
		if( klass == StringBuilder.class ) return SqlType.VARCHAR;
		if( klass == StringBuffer.class  ) return SqlType.VARCHAR;
		if( klass == char.class          ) return SqlType.CHAR;
		if( klass == Character.class     ) return SqlType.CHAR;
		if( klass == int.class           ) return SqlType.INTEGER;
		if( klass == Integer.class       ) return SqlType.INTEGER;
		if( klass == double.class        ) return SqlType.DOUBLE;
		if( klass == Double.class        ) return SqlType.DOUBLE;
		if( klass == float.class         ) return SqlType.FLOAT;
		if( klass == Float.class         ) return SqlType.FLOAT;
		if( klass == boolean.class       ) return SqlType.BOOLEAN;
		if( klass == Boolean.class       ) return SqlType.BOOLEAN;
		if( klass == byte.class          ) return SqlType.TINYINT;
		if( klass == Byte.class          ) return SqlType.TINYINT;
		if( klass == short.class         ) return SqlType.SMALLINT;
		if( klass == Short.class         ) return SqlType.SMALLINT;
		if( klass == long.class          ) return SqlType.BIGINT;
		if( klass == Long.class          ) return SqlType.BIGINT;
		if( klass == BigInteger.class    ) return SqlType.BIGINT;
		if( klass == BigDecimal.class    ) return SqlType.NUMERIC;
		if( klass == byte[].class        ) return SqlType.BLOB;
		if( klass == Byte[].class        ) return SqlType.BLOB_BOXED;
		if( klass == Date.class          ) return SqlType.DATE;
		if( klass == Calendar.class      ) return SqlType.DATE;
		if( klass == NDate.class         ) return SqlType.DATE;
		if( Types.isMap(klass)           ) return SqlType.JAVA_OBJECT;
		if( Types.isArrayOrList(klass)   ) return SqlType.LIST;

		return SqlType.VARCHAR;

	}

}