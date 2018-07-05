package org.nybatis.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activity.InvalidActivityException;

import org.nybatis.core.log.NLogger;

import org.nybatis.core.model.NMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ClassUtilTest {

	@Test( expectedExceptions = InvalidActivityException.class )
	public void makeInstance() throws ClassNotFoundException, InvalidActivityException {

		NLogger.debug( ClassUtil.getClass( "java.util.ArrayList<java.lang.String>" ) );
		NLogger.debug( ClassUtil.getClass( "java.util.ArrayList <java.lang.String" ) );
		NLogger.debug( ClassUtil.getClass( "java. util.ArrayList" ) );
		NLogger.debug( ClassUtil.getClass( "java.util.ArrayList" ) );

		try {
			NLogger.debug( ClassUtil.getClass( "java.util.ArrayLis" ) );
		} catch( ClassNotFoundException e ) {
			throw new InvalidActivityException( e );
		}

	}

	@Test
	public void hasClassTest() {
		assertEquals( true,   ClassUtil.isExtendedBy( Integer.class, Integer.class ) );
		assertEquals( true,   ClassUtil.isExtendedBy( ArrayList.class, List.class ) );
		assertEquals( true,   ClassUtil.isExtendedBy( NMap.class, Map.class ) );
		assertEquals( false,  ClassUtil.isExtendedBy( NMap.class, List.class ) );
	}

}
