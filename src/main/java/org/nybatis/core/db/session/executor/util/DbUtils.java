package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NDate;
import org.nybatis.core.reflection.core.JsonConverter;
import org.nybatis.core.reflection.mapper.NObjectSqlMapper;
import org.nybatis.core.util.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

public class DbUtils {

	private static final Logger logger = LoggerFactory.getLogger( Const.db.LOG_SQL );
    private static SqlCallerFinder sqlCallerFinder = new SqlCallerFinder();

	public static JsonConverter jsonConverter = new JsonConverter( new NObjectSqlMapper() );

	public static boolean isPrimitive( Object object ) {
		return object == null || isPrimitive( object.getClass() );
	}

	public static boolean isPrimitive( Class<?> klass ) {

		if( klass == null ) return false;

		if( klass == String.class     ) return true;
		if( klass == int.class        ) return true;
		if( klass == Integer.class    ) return true;
		if( klass == double.class     ) return true;
		if( klass == Double.class     ) return true;
		if( klass == float.class      ) return true;
		if( klass == Float.class      ) return true;
		if( klass == long.class       ) return true;
		if( klass == Long.class       ) return true;
		if( klass == boolean.class    ) return true;
		if( klass == Boolean.class    ) return true;
		if( klass == byte.class       ) return true;
		if( klass == Byte.class       ) return true;
		if( klass == short.class      ) return true;
		if( klass == Short.class      ) return true;
		if( klass == BigDecimal.class ) return true;
		if( klass == BigInteger.class ) return true;
		if( klass == char.class       ) return true;
		if( klass == Character.class  ) return true;
		if( klass == byte[].class     ) return true;
		if( klass == Byte[].class     ) return true;
		if( klass == Date.class       ) return true;
		if( klass == Calendar.class   ) return true;
		if( klass == NDate.class      ) return true;
		if( klass == Object.class     ) return true;
		return Types.isArrayOrList( klass );

	}

	public static void logCaller() {
		if( ! logger.isDebugEnabled() ) return;
		logger.debug( ">> called from : {}", sqlCallerFinder.get() );
	}

}