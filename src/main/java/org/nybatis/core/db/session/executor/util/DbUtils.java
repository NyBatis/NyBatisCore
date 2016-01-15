package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.model.NDate;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.TypeUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbUtils {

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
		return TypeUtil.isArray( klass );

	}

}