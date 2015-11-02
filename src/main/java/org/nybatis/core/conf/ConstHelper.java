package org.nybatis.core.conf;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.nybatis.core.exception.unchecked.BaseRuntimeException;

public class ConstHelper {

	/**
	 * 프로그램이 구동되는 root 디렉토리를 구한다.
	 * 
	 * @return root 디렉토리
	 */
	public String getRoot() {

		try {
			
			String rootPath = Paths.get( Const.path.class.getResource( "/" ).toURI() ).toString();
			
			if( File.separatorChar == '\\' ) {
				rootPath = rootPath.replaceAll( "\\\\", "/" );
			}

			return rootPath;
			
		} catch ( URISyntaxException e ) {
			
			throw new BaseRuntimeException( e );
			
		}
		
	}
	
}
