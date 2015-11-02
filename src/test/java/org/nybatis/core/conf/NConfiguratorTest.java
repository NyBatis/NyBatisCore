package org.nybatis.core.conf;

import org.testng.annotations.Test;

import java.io.File;


public class NConfiguratorTest {

	@Test
	public void replaceTest() {

		String val = "#root#/cache/queryList";
		
//		String root = "D:\\development\\NIDE\\workspace\\SqlExplorer\\target\\classes";
		String root = "D:/development/NIDE/workspace/SqlExplorer/target/classes";
		
		System.out.println( File.separator );
		System.out.println( Const.path.getConfig() );
		
		System.out.println( val.replaceAll( val, root ) );
	}
	
}
