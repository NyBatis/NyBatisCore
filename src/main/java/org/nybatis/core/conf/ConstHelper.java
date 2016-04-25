package org.nybatis.core.conf;

import org.nybatis.core.exception.unchecked.BaseRuntimeException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.ClassUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Constant helper
 */
public class ConstHelper {

	/**
	 * get root directory where program is running
	 *
	 * @return root directory
	 */
	public String getRoot() {

		URL root = ClassUtil.getClassLoader().getResource( "" );

		try {
			if( root != null ) {
				return FileUtil.nomalizeSeparator( Paths.get( root.toURI() ) );
			} else {
				return FileUtil.nomalizeSeparator( new File(".").getAbsolutePath() ).replaceFirst( "/\\.$", "" );
			}
		} catch( URISyntaxException e ) {
			throw new BaseRuntimeException( e );
		}

	}

}
