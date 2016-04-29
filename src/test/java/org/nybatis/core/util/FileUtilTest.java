package org.nybatis.core.util;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.nybatis.core.conf.Const;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NListTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
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

		list = FileUtil.search( Const.path.getRoot(), true, false, -1, "**/*.xml" );
		printFiles(list);
		Assert.assertTrue( list.size() > 0 );

		list = FileUtil.search( Const.path.getRoot() + "/config/db", true, false, 0, "*" );
		printFiles(list);
//		Assert.assertTrue( list.size() == 1 );

		list = FileUtil.search( Const.path.getRoot() + "/config/db", true, false, 0, "**.*" );
		printFiles(list);

		list = FileUtil.search( Const.path.getRoot() + "/config/db", true, false, 1, "*.xml" );
		printFiles( list );

		list = FileUtil.search( Const.path.getRoot() + "/config/db", true, false, 1, "**.xml" );
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

	@Test @Ignore
	public void copyDir() {
		String source = "e:\\download\\_testaa";
		String target = "\\\\NAS\\emul\\image\\Apple2\\_testaa";
		FileUtil.copy( source, target, true );
	}

	@Test
	public void isFile() {
		Assert.assertFalse( FileUtil.isFile( "/d:/f제목 없음" ) );
	}

	@Test
	public void readInClassPath() throws IOException {

		Enumeration<URL> resources = ClassUtil.getClassLoader().getResources( "config/**" );

		for( URL url : Collections.list( resources ) ) {

			URLConnection urlConnection = url.openConnection();

		}

//		InputStream inputStream = ClassUtil.getResourceAsStream( "/config/log/logback.xml" );
//		FileUtil.readFrom( inputStream, readLine -> {
//            NLogger.debug( readLine );
//        });
	}

	@Test
	public void pathMatcher() {

		List<String> entries = Arrays.asList( "WEB-INF\\classes\\config\\message\\merong\\messageMerong.prop", "WEB-INF\\classes\\config\\message\\message.prop" );

		System.out.println( entries );

		NLogger.debug( entries );

		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher( "glob:" + "**.prop" );

		for( String entry : entries ) {
			boolean matches = pathMatcher.matches( Paths.get( entry ) );
			if( matches ) {
				NLogger.debug( "matched !! : {}", entry );
			}
		}


	}

}
