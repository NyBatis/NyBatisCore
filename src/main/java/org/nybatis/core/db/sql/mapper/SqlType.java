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

	BIT                     ( java.sql.Types.BIT                     , "BIT"                     , null )  ,
	TINYINT                 ( java.sql.Types.TINYINT                 , "TINYINT"                 , null )  ,
	SMALLINT                ( java.sql.Types.SMALLINT                , "SMALLINT"                , null )  ,
	INTEGER                 ( java.sql.Types.INTEGER                 , "INTEGER"                 , null )  ,
	INT                     ( java.sql.Types.INTEGER                 , "INT"                     , null )  , // shortcut for INTEGER
	BIGINT                  ( java.sql.Types.BIGINT                  , "BIGINT"                  , null )  ,
	FLOAT                   ( java.sql.Types.FLOAT                   , "FLOAT"                   , null )  ,
	REAL                    ( java.sql.Types.REAL                    , "REAL"                    , null )  ,
	DOUBLE                  ( java.sql.Types.DOUBLE                  , "DOUBLE"                  , null )  ,
	NUMERIC                 ( java.sql.Types.NUMERIC                 , "NUMERIC"                 , null )  ,
	DECIMAL                 ( java.sql.Types.DECIMAL                 , "DECIMAL"                 , null )  ,
	CHAR                    ( java.sql.Types.CHAR                    , "CHAR"                    , 100  )  ,
	VARCHAR                 ( java.sql.Types.VARCHAR                 , "VARCHAR"                 , 100  )  ,
	LONGVARCHAR             ( java.sql.Types.LONGVARCHAR             , "LONGVARCHAR"             , 100  )  ,
	DATE                    ( java.sql.Types.DATE                    , "DATE"                    , null )  ,
	TIME                    ( java.sql.Types.TIME                    , "TIME"                    , null )  ,
	TIMESTAMP               ( java.sql.Types.TIMESTAMP               , "TIMESTAMP"               , null )  ,
	BINARY                  ( java.sql.Types.BINARY                  , "BINARY"                  , null )  ,
	ARRAY                   ( java.sql.Types.ARRAY                   , "ARRAY"                   , null )  ,
	LIST                    ( 9001                                   , "LIST"                    , null )  ,
	BLOB                    ( java.sql.Types.BLOB                    , "BLOB"                    , null )  ,
	BLOB_BOXED              ( 9002                                   , "BLOB_BOXED"              , null )  , // shortcut for Byte[]
	CLOB                    ( java.sql.Types.CLOB                    , "CLOB"                    , null )  ,
	BOOLEAN                 ( java.sql.Types.BOOLEAN                 , "BOOLEAN"                 , null )  ,
	NULL                    ( java.sql.Types.NULL                    , "NULL"                    , null )  ,

	SYS_REFCURSOR           ( -10                                    , "SYS_REFCURSOR"           , null )  , // shortcut for oracle cursor
	CURSOR                  ( -10                                    , "CURSOR"                  , null )  , // shortcut for oracle cursor
	RESULT_SET              ( -10                                    , "RESULTSET"               , null )  , // shortcut for oracle cursor
	RS                      ( -10                                    , "RS"                      , null )  , // shortcut for oracle cursor
	ORACLE_RESULT_SET       ( -10                                    , "ORACLE_RESULTSET"        , null )  , // shortcut for oracle cursor

	// struct
	REF                     ( java.sql.Types.REF                     , "REF"                     , null )  ,
	STRUCT                  ( java.sql.Types.STRUCT                  , "STRUCT"                  , null )  ,

	// not figured out
	VARBINARY               ( java.sql.Types.VARBINARY               , "VARBINARY"               , null )  ,
	LONGVARBINARY           ( java.sql.Types.LONGVARBINARY           , "LONGVARBINARY"           , null )  ,
	OTHER                   ( java.sql.Types.OTHER                   , "OTHER"                   , null )  ,
	JAVA_OBJECT             ( java.sql.Types.JAVA_OBJECT             , "JAVA_OBJECT"             , null )  ,
	DISTINCT                ( java.sql.Types.DISTINCT                , "DISTINCT"                , null )  ,
	REF_CURSOR              ( java.sql.Types.REF_CURSOR              , "REF_CURSOR"              , null )  ,
	DATALINK                ( java.sql.Types.DATALINK                , "DATALINK"                , null )  ,
	ROWID                   ( java.sql.Types.ROWID                   , "ROWID"                   , null )  ,
	NCHAR                   ( java.sql.Types.NCHAR                   , "NCHAR"                   , null )  ,
	NVARCHAR                ( java.sql.Types.NVARCHAR                , "NVARCHAR"                , null )  ,
	LONGNVARCHAR            ( java.sql.Types.LONGNVARCHAR            , "LONGNVARCHAR"            , null )  ,
	NCLOB                   ( java.sql.Types.NCLOB                   , "NCLOB"                   , null )  ,
	SQLXML                  ( java.sql.Types.SQLXML                  , "SQLXML"                  , null )  ,
	TIME_WITH_TIMEZONE      ( java.sql.Types.TIME_WITH_TIMEZONE      , "TIME_WITH_TIMEZONE"      , null )  ,
	TIMESTAMP_WITH_TIMEZONE ( java.sql.Types.TIMESTAMP_WITH_TIMEZONE , "TIMESTAMP_WITH_TIMEZONE" , null )  ;

	public final int     code;
	public final Integer length;
	public final String  name;

	private static Map<Integer,SqlType> codes = new LinkedHashMap<>();
	private static Map<String,SqlType>  names = new LinkedHashMap<>();

	static {
		for( SqlType type : SqlType.values() ) {
			codes.put( type.code, type );
			names.put( type.name, type );
		}
	}

	SqlType( int code, String string, Integer defaultLength ) {
		this.code   = code;
		this.name   = string;
		this.length = defaultLength;
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
		if( klass == boolean.class       ) return SqlType.BOOLEAN;
		if( klass == Boolean.class       ) return SqlType.BOOLEAN;

//		if( klass == int.class           ) return SqlType.INTEGER;
//		if( klass == Integer.class       ) return SqlType.INTEGER;
//		if( klass == double.class        ) return SqlType.DOUBLE;
//		if( klass == Double.class        ) return SqlType.DOUBLE;
//		if( klass == float.class         ) return SqlType.FLOAT;
//		if( klass == Float.class         ) return SqlType.FLOAT;
//		if( klass == boolean.class       ) return SqlType.BOOLEAN;
//		if( klass == Boolean.class       ) return SqlType.BOOLEAN;
//		if( klass == byte.class          ) return SqlType.TINYINT;
//		if( klass == Byte.class          ) return SqlType.TINYINT;
//		if( klass == short.class         ) return SqlType.SMALLINT;
//		if( klass == Short.class         ) return SqlType.SMALLINT;
//		if( klass == long.class          ) return SqlType.BIGINT;
//		if( klass == Long.class          ) return SqlType.BIGINT;
//		if( klass == BigInteger.class    ) return SqlType.BIGINT;
//		if( klass == BigDecimal.class    ) return SqlType.NUMERIC;

		if( klass == int.class           ) return SqlType.DECIMAL;
		if( klass == Integer.class       ) return SqlType.DECIMAL;
		if( klass == double.class        ) return SqlType.DECIMAL;
		if( klass == Double.class        ) return SqlType.DECIMAL;
		if( klass == float.class         ) return SqlType.DECIMAL;
		if( klass == Float.class         ) return SqlType.DECIMAL;
		if( klass == byte.class          ) return SqlType.DECIMAL;
		if( klass == Byte.class          ) return SqlType.DECIMAL;
		if( klass == short.class         ) return SqlType.DECIMAL;
		if( klass == Short.class         ) return SqlType.DECIMAL;
		if( klass == long.class          ) return SqlType.DECIMAL;
		if( klass == Long.class          ) return SqlType.DECIMAL;
		if( klass == BigInteger.class    ) return SqlType.DECIMAL;
		if( klass == BigDecimal.class    ) return SqlType.DECIMAL;

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