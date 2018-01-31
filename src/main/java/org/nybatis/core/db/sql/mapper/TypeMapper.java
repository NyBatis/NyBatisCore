package org.nybatis.core.db.sql.mapper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.nybatis.core.db.sql.mapper.implement.*;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlException;

public class TypeMapper {

    private static Map<String, Map<SqlType, TypeMapperIF<?>>> mapper = new HashMap<>();

    private static final String DEFAULT = TypeMapper.class.getName() + ".default";

    static {

        put( SqlType.NULL,          new NullMapper()       ); // treat null value

        put( SqlType.BOOLEAN,       new BooleanMapper()    );
    	put( SqlType.BIT,           new BooleanMapper()    );
    	put( SqlType.TINYINT,       new ByteMapper()       );
    	put( SqlType.SMALLINT,      new ShortMapper()      );
    	put( SqlType.INTEGER,       new IntegerMapper()    );
    	put( SqlType.INT,           new IntegerMapper()    );
    	put( SqlType.BIGINT,        new LongMapper()       );
    	put( SqlType.FLOAT,         new FloatMapper()      );
    	put( SqlType.DOUBLE,        new DoubleMapper()     );
    	put( SqlType.REAL,          new BigDecimalMapper() );
    	put( SqlType.DECIMAL,       new BigDecimalMapper() );
    	put( SqlType.NUMERIC,       new BigDecimalMapper() );
    	put( SqlType.CHAR,          new StringMapper()     );
    	put( SqlType.CLOB,          new ClobMapper()       );
    	put( SqlType.VARCHAR,       new StringMapper()     );
    	put( SqlType.LONGVARCHAR,   new StringMapper()     );
    	put( SqlType.NVARCHAR,      new StringMapper()     );
    	put( SqlType.NCHAR,         new StringMapper()     );
    	put( SqlType.NCLOB,         new ClobMapper()       );
    	put( SqlType.ARRAY,         new ArrayMapper()      );
    	put( SqlType.BLOB,          new BlobMapper()       );
    	put( SqlType.BLOB_BOXED,    new BlobBoxedMapper()  ); // for java
    	put( SqlType.LONGVARBINARY, new BlobMapper()       );

		/**
		 * java.sql.Date handle data as Date only and miss time information(HH:MI:SS).<br>
		 * So It is necessary to handle data as TimeStamp
		 */

		put( SqlType.DATE,          new TimeStampMapper()  );
    	put( SqlType.TIME,          new TimeStampMapper()  );
    	put( SqlType.TIMESTAMP,     new TimeStampMapper()  );

    	// cursor outPatameter
    	put( SqlType.SYS_REFCURSOR, new ResultsetMapper()  );
    	put( SqlType.REF_CURSOR,    new ResultsetMapper()  );
    	put( SqlType.CURSOR,        new ResultsetMapper()  );
    	put( SqlType.RESULT_SET,    new ResultsetMapper()  );
    	put( SqlType.RS,            new ResultsetMapper()  );
    	put( SqlType.ORACLE_RESULT_SET, new ResultsetMapper()  );

    	// struct ?
    	put( SqlType.REF,           new ObjectMapper()     );
    	put( SqlType.STRUCT,        new ObjectMapper()     );

    }

    private static void put( SqlType sqlType, TypeMapperIF<?> typeMapper ) {
    	put( DEFAULT, sqlType, typeMapper );
    }

	public static synchronized void put( String environmentId, SqlType sqlType, TypeMapperIF<?> typeMapper ) {
		if( ! mapper.containsKey(environmentId) ) {
			mapper.put( environmentId, new HashMap<>() );
		}
		mapper.get( environmentId ).put( sqlType, typeMapper );
	}

	@SuppressWarnings( "rawtypes" )
	public static TypeMapperIF get( SqlType sqlType ) {
		return mapper.get( DEFAULT ).get( sqlType );
	}

	@SuppressWarnings( "rawtypes" )
    public static synchronized TypeMapperIF get( String environmentId, SqlType sqlType ) {
		if( ! mapper.containsKey(environmentId) ) environmentId = DEFAULT;
		TypeMapperIF<?> typeMapper = mapper.get( environmentId ).get( sqlType );
		return ( typeMapper == null ) ? get( sqlType ) : typeMapper;
	}

	@SuppressWarnings( "rawtypes" )
	public static TypeMapperIF get( int sqlType ) {
		return mapper.get( DEFAULT ).get( SqlType.find( sqlType ) );
	}

	/**
	 * set mapper for unimplmented jdbc type
	 * @param environmentId	db configuration environment id
	 * @param sqlType column type
	 * @param e       previous throable
	 * @throws SQLException occurs when raise error in SQL handling.
     */
	public static synchronized void setUnimplementedMapper( String environmentId, SqlType sqlType, Throwable e ) throws SQLException {
		TypeMapperIF prevTypeMapper = get( environmentId, sqlType );
		if( sqlType == SqlType.BLOB && ! (prevTypeMapper instanceof ByteArrayMapper) ) {
			TypeMapper.put( environmentId, SqlType.BLOB, new ByteArrayMapper() );
			return;
		} else if( sqlType == SqlType.CLOB && ! (prevTypeMapper instanceof StringMapper) ) {
			TypeMapper.put( environmentId, SqlType.CLOB, new StringMapper() );
			return;
		}
		throw new SQLException( String.format("there is no mapper to handle sqlType(%s) in environment(id:%s)", sqlType, environmentId), e.getCause() );
	}

}
