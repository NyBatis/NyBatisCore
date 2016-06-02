package org.nybatis.core.message;

import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.StringUtil;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 목적 : 메세지 관리를 ResourceBundle 을 이용해 수행하고자 함
 *
 * 결론 : 모든 기능이 훌륭하게 작동되나, Locale 동적변경은 비효율적임
 *
 * @author "nayasis@gmail.com"
 *
 */
public class ResourceBundleTest {

	@Test
	public void getLocale() throws FileNotFoundException {


		NLogger.debug( "Locale Korean : {}", Locale.KOREAN );

		NLogger.debug( "Default Locale : {}", Locale.getDefault() );

		Locale locale = new Locale( "", Locale.getDefault().getCountry() );

		NLogger.debug( "Locale To Use: {}", locale );

        ResourceBundle bundle = ResourceBundle.getBundle( "messageTest", Locale.getDefault(), getClassLoader( Const.path.getConfigMessage() ), new Utf8Control() );

        NLogger.debug( bundle.getString( "ui.err.0001" ) );
        NLogger.debug( bundle.getString( "ui.err.0002" ) );
        NLogger.debug( bundle.getString( "ui.err.0003" ) );

	}

	private ClassLoader getClassLoader( String filePath ) throws FileNotFoundException {

		File file = new File( filePath );

		try {

			URL[] urls = { file.toURI().toURL() };

			ClassLoader loader = new URLClassLoader(urls);

			return loader;

		} catch( MalformedURLException e ) {
			throw new FileNotFoundException( filePath );
		}

	}

	@Test
	public void catalogTest() throws IOException {

    	List<Path> list = FileUtil.search( Const.path.getConfigMessage(), true, false, -1, "*.properties" );

    	Set<String> baseNames = new HashSet<>();

    	for( Path path : list ) {

    		List<String> baseName = StringUtil.tokenize( FileUtil.removeExtention( path.getFileName().toString() ), "_" );

//    		int endIndex = ( baseName.size() - 2 < 0 ) ? 0 : baseName.size() - 2;

    		switch ( baseName.size() ) {

    			case 1 :
    				baseNames.add( baseName.get(0) );
    				break;
    			case 2 :
    				baseNames.add( baseName.get(0) );
    				break;
    			case 3 :
    				baseNames.add( baseName.get(0) );
    				break;

    		}

    		if( ! baseName.contains( "_" ) )

    		NLogger.debug( baseName );

    	}

	}

}