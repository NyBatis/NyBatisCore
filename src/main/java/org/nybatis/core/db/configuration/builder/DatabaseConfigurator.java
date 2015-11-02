package org.nybatis.core.db.configuration.builder;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.file.FileUtil;

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
	 * but if given path dose not exist, it will build from basepath({@link Const.path.getConfigDatabase}) + given path.
	 *
	 * @param filePath given configuration path
	 */
	public static void build( String filePath ) {

		filePath = Const.profile.getFileName( filePath );

		if( FileUtil.isExist( filePath ) ) {
			new ConfigurationBuilder( filePath );

		} else {

			try {

				String modifiedPath = Paths.get( Const.path.getConfigDatabase(), filePath ).toString();

				if( FileUtil.isExist(modifiedPath) ) {
					new ConfigurationBuilder( modifiedPath );
					return;
				}

				NLogger.error( "Database configuration file is not exist.\n\t - in [{}]\n\t - in [{}]", filePath, modifiedPath );

			} catch( InvalidPathException e ) {
				NLogger.error( e, "Database configuration file is not exist.\n\t - in [{}]\n\t - in [{}]", filePath );

			}

		}

	}

	/**
	 * Build All Database Configration from files in dafault path<br><br>
	 *
	 * default path is DatabaseConfigurationPath({@link Const.path.getConfigDatabase})
	 *
	 */
	public static void build() {

		List<Path> confLists = FileUtil.getList( Const.path.getConfigDatabase(), true, false, 0, "*.xml" );

		for( Path confPath : confLists ) {
			build( confPath.toString() );
		}

	}

}
