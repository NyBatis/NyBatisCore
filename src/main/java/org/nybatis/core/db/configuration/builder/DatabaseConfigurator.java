package org.nybatis.core.db.configuration.builder;

import java.util.ArrayList;
import org.nybatis.core.conf.Const;
import org.nybatis.core.context.NThreadLocal;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.ClassUtil;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import org.nybatis.core.util.StringUtil;

import static com.sun.tools.doclint.Entity.exist;
import static com.sun.tools.doclint.Entity.not;

/**
 * Database Configuration Builder
 *
 * @author nayasis@gmail.com
 *
 */
public class DatabaseConfigurator {

	/**
	 * Build Database Configration from given path<br><br>
	 *
	 * First it builds from given path itself.<br>
	 * but if given path dose not exist, it will build from basepath({@link org.nybatis.core.conf.Const.path#getConfigDatabase}) + given path.
	 *
	 * @param filePath 	given configuration path
	 * @param reload  	reload configuration
	 * @throws DatabaseConfigurationException occurs when database configuration is not acceptable
	 */
	public static void build( String filePath, boolean reload ) {

		try {
			new ConfigurationBuilder().readFrom( getFilePath(filePath), reload );
		} catch( InvalidPathException e ) {
			throw new DatabaseConfigurationException( e );
		} catch( DatabaseConfigurationException e ) {
			throw e;
		}

		// Delete temporary thread local key
		NThreadLocal.clear();

	}

	private static String getFilePath( String filePath ) {

		List<String> checkList = new ArrayList<>();

		filePath = FileUtil.nomalizeSeparator( filePath );
		String checkPath = filePath;
		checkList.add( checkPath );

		if( FileUtil.exists(checkPath) ) return checkPath;

		checkPath = resolvePath( Const.path.getConfigDatabase(), filePath );
		checkList.add( checkPath );

		if( FileUtil.exists(checkPath) ) return checkPath;

		checkPath = resolvePath( Const.path.getBase(), filePath );
		checkList.add( checkPath );

		if( FileUtil.exists(checkPath) ) return checkPath;

		StringBuilder errorMessage = new StringBuilder( "Database configuration file is not exist." );
		for( String path : checkList ) {
			errorMessage.append( "\n\tin [" ).append( path ).append( "]" );
		}

		throw new DatabaseConfigurationException( errorMessage.toString() );

	}

	private static String resolvePath( String root, String path ) {
		path = Paths.get( root, path ).toString();
		return FileUtil.nomalizeSeparator( path );
	}

	/**
	 * Build All Database Configration from files in dafault path<br><br>
	 *
	 * default path is DatabaseConfigurationPath({@link org.nybatis.core.conf.Const.path#getConfigDatabase})
	 *
	 * @param reload  	reload configuration
	 */
	public static void build( boolean reload ) {

		String dbConfDir = Const.path.toResourceName( Const.path.getConfigDatabase() );

		List<String> resourceNames = ClassUtil.findResources( dbConfDir + "/*.xml" );

		for( String resourceName : resourceNames ) {
			new ConfigurationBuilder().readFrom( resourceName, reload );
		}

	}

	/**
	 * Build Database Configration from given path<br><br>
	 *
	 * First it builds from given path itself.<br>
	 * but if given path dose not exist, it will build from basepath({@link org.nybatis.core.conf.Const.path#getConfigDatabase}) + given path.
	 *
	 * @param filePath given configuration path
	 */
	public static void build( String filePath ) {
		build( filePath, false );
	}

	/**
	 * Build All Database Configration from files in dafault path<br><br>
	 *
	 * default path is DatabaseConfigurationPath({@link org.nybatis.core.conf.Const.path#getConfigDatabase})
	 *
	 */
	public static void build() {
		build( false );
	}

}
