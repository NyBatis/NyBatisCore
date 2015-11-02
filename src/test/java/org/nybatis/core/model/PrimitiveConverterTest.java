package org.nybatis.core.model;

import org.nybatis.core.log.NLogger;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Calendar;

public class PrimitiveConverterTest {

	@Test
	public void basic() {

		String value = "1";

		NLogger.debug( new PrimitiveConverter(value).cast( String.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( int.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( Integer.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( long.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( Long.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( float.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( Float.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( double.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( Double.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( BigDecimal.class ) );
		NLogger.debug( new PrimitiveConverter(value).cast( BigInteger.class ) );

	}

	@Test
	public void date() {

		String value1 = "1";

		NLogger.debug( new PrimitiveConverter(value1).cast( Date.class ) );
		NLogger.debug( new PrimitiveConverter(value1).cast( Calendar.class ) );
		NLogger.debug( new PrimitiveConverter(value1).cast( NDate.class ) );

		String value2 = "2015-05-20";
		NLogger.debug( new PrimitiveConverter(value2).cast( Date.class ) );
		NLogger.debug( new PrimitiveConverter(value2).cast( Calendar.class ) );
		NLogger.debug( new PrimitiveConverter(value2).cast( NDate.class ) );

	}

	@Test
	public void exceptional() {

		String value = "((";

		NLogger.debug( new PrimitiveConverter(value).toUUID() );
		NLogger.debug( new PrimitiveConverter(value).toURI() );
		NLogger.debug( new PrimitiveConverter(value).toURL() );
		NLogger.debug( new PrimitiveConverter(value).toVoid() );
		NLogger.debug( new PrimitiveConverter(value).toPattern() );

	}

}
