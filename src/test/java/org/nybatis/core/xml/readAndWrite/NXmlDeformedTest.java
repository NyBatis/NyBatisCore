package org.nybatis.core.xml.readAndWrite;

import java.io.File;

import org.nybatis.core.conf.Const;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.node.Node;
import org.testng.annotations.Test;

public class NXmlDeformedTest {

	@Test
	public void read() {

		NXml xml = new NXmlDeformed( new File( Const.path.getRoot() + "/org/nybatis/core/xml/readAndWrite/Deformed.xml" ) );

		System.out.println( xml );

		Node sqlNode = xml.getRoot().getChildElement("sql");

		System.out.println( sqlNode.hasAttr( "pooled" ) );
		System.out.println( sqlNode.hasAttr( "pooledN" ) );

	}

}
