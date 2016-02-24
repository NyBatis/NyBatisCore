package org.nybatis.core.db.configuration.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.node.Node;

public class ConfigurationBuilder {

	private static Set<String> loadedFiles = new HashSet<>();

	public void readFrom( String file ) {
		readFrom( Paths.get(file), false );
	}

	public void readFrom( String file, boolean reload ) {
		readFrom( Paths.get( file ), reload );
	}

	public void readFrom( Path path ) {
		readFrom( path, false );
	}

	public void readFrom( Path path, boolean reload ) {
		if( path == null ) return;
		readFrom( path.toFile(), reload );
	}

	public void readFrom( File file ) {
		readFrom( file, false );
	}

	public void readFrom( File file, boolean reload ) {

		if( reload == false && isLoaded(file) ) return;

		NLogger.debug( "load database configuration from [{}]", file );

		try {

			synchronized( loadedFiles ) {

				NXml xmlReader = new NXmlDeformed( file );

				Node root = xmlReader.getRoot();

				PropertiesBuilder propertiesBuilder = new PropertiesBuilder( root.getChildElement("properties") );

				CacheBuilder cacheBuilder = new CacheBuilder();

				for( Node cache : root.getChildElements("cache") ) {
					cacheBuilder.setCache( cache, propertiesBuilder );
				}

				cacheBuilder.setDefaultCache();

				for( Node environment : root.getChildElements("environment") ) {
					new DatasourceBuilder( environment, propertiesBuilder );
					try {
						new SqlBuilder( environment, propertiesBuilder, FileUtil.getDirectory(file) );
					} catch (FileNotFoundException e) {}
				}

				cacheBuilder.checkEachSqlCache();

				loadedFiles.add( file.toString() );

            }


		} catch( ParseException | IoException e ) {
	        throw new ParseException( e, "Error on reading Database configuration file({})\n\t{}", file, e.getMessage() );
        }

	}

	private boolean isLoaded( File file ) {
		if( file == null ) return true;
		return loadedFiles.contains( file.toString() );
	}


}
