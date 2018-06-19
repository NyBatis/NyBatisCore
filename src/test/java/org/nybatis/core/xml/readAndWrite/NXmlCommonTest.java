package org.nybatis.core.xml.readAndWrite;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.xml.NXml;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Paths;

/**
 * @author 1002159
 * @since 2015-12-14
 */
public class NXmlCommonTest {

    @BeforeClass
    public void before() {
    }

    @Test
    public void read() {

        NXml xml02 = new NXml( Paths.get( "C:\\NIDE\\workspace\\NayasisCore\\NyBatisCore\\src\\test\\java\\org\\nybatis\\core\\xml\\readAndWrite\\common.xml" ) );

        NLogger.debug( xml02 );

    }


}
