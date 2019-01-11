package org.nybatis.core.conf;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import org.nybatis.core.exception.unchecked.BaseRuntimeException;
import org.nybatis.core.file.FileUtil;
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
		try {
			if( ClassUtil.isRunningInJar() ) {
				return FileUtil.nomalizeSeparator( new File( "." ).getAbsolutePath() ).replaceFirst( "/\\.$", "" );
			} else {
				URL root = ClassUtil.getClassLoader().getResource( "" );
				return FileUtil.nomalizeSeparator( Paths.get( root.toURI() ) );
			}
		} catch( URISyntaxException e ) {
			throw new BaseRuntimeException( e );
		}
	}

	public boolean isWebInfExist() {
		if( ClassUtil.isRunningInJar() ) {
			return ClassUtil.isResourceExisted( "/WEB-INF/classes" );
		} else {
			return FileUtil.exists( getRoot() + "/WEB-INF/classes" );
		}
	}

}
