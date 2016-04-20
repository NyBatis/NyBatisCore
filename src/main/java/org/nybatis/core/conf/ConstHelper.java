package org.nybatis.core.conf;

import org.nybatis.core.exception.unchecked.BaseRuntimeException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.ClassUtil;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
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
		return FileUtil.nomalizeSeparator( getRootPath().toString() );
	}

	/**
	 * get root path
	 *
	 * @return root path
	 */
	private Path getRootPath(){

		URL root = ClassUtil.getClassLoader().getResource( "" );

		try {
			if( root != null ) {
                return Paths.get( root.toURI() );
            } else {
				// if class is running in JAR.
                return Paths.get( ClassUtil.getRootClass().getProtectionDomain().getCodeSource().getLocation().toURI() ).getParent();
            }
		} catch( URISyntaxException e ) {
			throw new BaseRuntimeException( e );
		}

	}

}
