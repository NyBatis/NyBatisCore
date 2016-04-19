package org.nybatis.core.conf;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.nybatis.core.exception.unchecked.BaseRuntimeException;
import org.nybatis.core.exception.unchecked.ClassNotExistException;
import org.nybatis.core.util.ClassUtil;

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

		String rootPath = getRootPath().toString();

		if( File.separatorChar == '\\' ) {
			rootPath = rootPath.replaceAll( "\\\\", "/" );
		}

		return rootPath;
			
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
                return Paths.get( getRootClass().getProtectionDomain().getCodeSource().getLocation().toURI() ).getParent();
            }
		} catch( URISyntaxException e ) {
			throw new BaseRuntimeException( e );
		}

	}

	/**
	 * get root class in thread stack
	 *
	 * @return root class
	 */
	private Class getRootClass() {

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement   topElement = stackTrace[ stackTrace.length - 1 ];

		try {
			return ClassUtil.getClass( topElement.getClassName() );
		} catch( ClassNotFoundException e ) {
			throw new ClassNotExistException( e );
		}

	}
	
}
