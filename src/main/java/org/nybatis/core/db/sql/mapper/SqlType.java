package org.nybatis.core.db.sql.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nybatis.core.model.NDate;
import org.nybatis.core.util.TypeUtil;

public enum SqlType {

	BIT                    ( Types.BIT                    , "BIT"                     ),
	TINYINT                ( Types.TINYINT                , "TINYINT"                 ),
	SMALLINT               ( Types.SMALLINT               , "SMALLINT"                ),
	INTEGER                ( Types.INTEGER                , "INTEGER"                 ),
	INT                    ( Types.INTEGER                , "INT"                     ), // shortcut for INTEGER
	BIGINT                 ( Types.BIGINT                 , "BIGINT"                  ),
	FLOAT                  ( Types.FLOAT                  , "FLOAT"                   ),
	REAL                   ( Types.REAL                   , "REAL"                    ),
	DOUBLE                 ( Types.DOUBLE                 , "DOUBLE"                  ),
	NUMERIC                ( Types.NUMERIC                , "NUMERIC"                 ),
	DECIMAL                ( Types.DECIMAL                , "DECIMAL"                 ),
	CHAR                   ( Types.CHAR                   , "CHAR"                    ),
	VARCHAR                ( Types.VARCHAR                , "VARCHAR"                 ),
	LONGVARCHAR            ( Types.LONGVARCHAR            , "LONGVARCHAR"             ),
	DATE                   ( Types.DATE                   , "DATE"                    ),
	TIME                   ( Types.TIME                   , "TIME"                    ),
	TIMESTAMP              ( Types.TIMESTAMP              , "TIMESTAMP"               ),
	BINARY                 ( Types.BINARY                 , "BINARY"                  ),
	ARRAY                  ( Types.ARRAY                  , "ARRAY"                   ),
	LIST                   ( 9001                         , "LIST"                    ),
	BLOB                   ( Types.BLOB                   , "BLOB"                    ),
	BLOB_BOXED             ( 9002                         , "BLOB_BOXED"              ), // shortcut for Byte[]
	CLOB                   ( Types.CLOB                   , "CLOB"                    ),
	BOOLEAN                ( Types.BOOLEAN                , "BOOLEAN"                 ),
	NULL                   ( Types.NULL                   , "NULL"                    ),

	SYS_REFCURSOR          ( -10                          , "SYS_REFCURSOR"           ), // shortcut for oracle cursor
	CURSOR                 ( -10                          , "CURSOR"                  ), // shortcut for oracle cursor
	RESULT_SET             ( -10                          , "RESULTSET"               ), // shortcut for oracle cursor
	RS                     ( -10                          , "RS"                      ), // shortcut for oracle cursor
	ORACLE_RESULT_SET      ( -10                          , "ORACLE_RESULTSET"        ), // shortcut for oracle cursor

	// struct
	REF                    ( Types.REF                    , "REF"                     ),
	STRUCT                 ( Types.STRUCT                 , "STRUCT"                  ),

	// not figured out
	VARBINARY              ( Types.VARBINARY              , "VARBINARY"               ),
	LONGVARBINARY          ( Types.LONGVARBINARY          , "LONGVARBINARY"           ),
	OTHER                  ( Types.OTHER                  , "OTHER"                   ),
	JAVA_OBJECT            ( Types.JAVA_OBJECT            , "JAVA_OBJECT"             ),
	DISTINCT               ( Types.DISTINCT               , "DISTINCT"                ),
	REF_CURSOR             ( Types.REF_CURSOR             , "REF_CURSOR"              ),
	DATALINK               ( Types.DATALINK               , "DATALINK"                ),
	ROWID                  ( Types.ROWID                  , "ROWID"                   ),
	NCHAR                  ( Types.NCHAR                  , "NCHAR"                   ),
	NVARCHAR               ( Types.NVARCHAR               , "NVARCHAR"                ),
	LONGNVARCHAR           ( Types.LONGNVARCHAR           , "LONGNVARCHAR"            ),
	NCLOB                  ( Types.NCLOB                  , "NCLOB"                   ),
	SQLXML                 ( Types.SQLXML                 , "SQLXML"                  ),
	TIME_WITH_TIMEZONE     ( Types.TIME_WITH_TIMEZONE     , "TIME_WITH_TIMEZONE"      ),
	TIMESTAMP_WITH_TIMEZONE( Types.TIMESTAMP_WITH_TIMEZONE, "TIMESTAMP_WITH_TIMEZONE" );

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
		if( TypeUtil.isMap( klass )      ) return SqlType.JAVA_OBJECT;
		if( TypeUtil.isArray( klass )    ) return SqlType.LIST;

		return SqlType.VARCHAR;

	}

}