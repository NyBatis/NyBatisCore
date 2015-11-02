package org.nybatis.core.util;

import org.nybatis.core.conf.Const;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NListTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class FileUtilTest {

	@Test
	public void compress() {

		File sourceFile = new File( "d:/download/Ys 1 (y) (Falcom) [fdd].D88" );
		File targetFile = new File( "d:/download/Ys 1 (y) (Falcom) [fdd].zip" );

		FileUtil.zip( sourceFile, targetFile, StandardCharsets.UTF_8 );

	}

	@Test
	public void writeToCsv() throws IOException {

		String filePath = "d:/merong.csv";

		NList dummyData = new NListTest().getDummyData();

		System.out.println( dummyData );

		FileUtil.writeToCsv( filePath, dummyData, ",", "MS949" );

		System.out.println( FileUtil.readFrom( filePath ) );

	}

	@Test
	public void fileFind() {

		List<Path> list;

		list = FileUtil.getList( Const.path.getRoot(), true, false, -1, "**/*.xml" );
		printFiles(list);
		Assert.assertTrue( list.size() > 0 );

		list = FileUtil.getList( Const.path.getRoot() + "/config/db", true, false, 0, "*" );
		printFiles(list);
//		Assert.assertTrue( list.size() == 1 );

		list = FileUtil.getList( Const.path.getRoot() + "/config/db", true, false, 0, "**.*" );
		printFiles(list);

		list = FileUtil.getList( Const.path.getRoot() + "/config/db", true, false, 1, "*.xml" );
		printFiles( list );

		list = FileUtil.getList( Const.path.getRoot() + "/config/db", true, false, 1, "**.xml" );
		printFiles( list );

//		Assert.assertTrue( list.size() == 1 );

	}

	private void printFiles( List<Path> list ) {

		NLogger.debug( ">> list size : {}", list.size() );

		for( Path path : list ) {
			NLogger.debug(path);
		}
	}

	@Test
	public void moveFile() throws IOException {

//		FileUtil.makeDir( "d:/download/temp/b" );

		FileUtil.copy( "d:/download/temp/a", "d:/download/temp/b", true );

//		Files.copy( Paths.get("d:/download/temp/a"), Paths.get("d:/download/temp/b"), new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING } );

	}

}
