package org.nybatis.core.context;

import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class NThreadLocalTest {

	@Test
	public void basic() {

		GlobalSqlParameter.put( "Key", "Merong" );

		assertEquals( "Merong", (String) GlobalSqlParameter.get( "Key" ), "simple put and get" );

		GlobalSqlParameter.put( "Key", "1" );

		assertEquals( "1", (String) GlobalSqlParameter.get( "Key" ), "put and get after init thread local" );

		NThreadLocal.clear();

		assertEquals( null, GlobalSqlParameter.get( "Key" ), "put and get after init thread local" );

	}

}