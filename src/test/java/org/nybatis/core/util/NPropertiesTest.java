package org.nybatis.core.util;

import java.io.File;
import java.io.IOException;

import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NPropertiesTest {

	@Test
	public void read() throws IOException {
		NProperties properties = new NProperties( new File( Const.path.getConfig() + "/properties/test.properties") );
		NLogger.debug( ">> Test properties :\n{}", properties );
		Assert.assertEquals("firstLine,SecondLine", properties.get("url") );
	}

}
