package org.nybatis.core.db.configuration.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.node.Node;

public class ConfigurationBuilder {

	private static Set<String> buildChecker = new HashSet<>();

	public ConfigurationBuilder( File file ) {
		readFrom( file );
	}

	public ConfigurationBuilder( String file ) {
		readFrom( new File(file) );
	}

	private boolean isBuildDone( File file ) {
		if( file == null ) return true;
		return buildChecker.contains( file.toString() );
	}

	private void readFrom( File file ) {

		if( isBuildDone(file) ) return;

		NLogger.debug( "load database configuration from [{}]", file );

		try {

			synchronized( buildChecker ) {

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

				// only clear file information to check build error. not clear sql information. :)
				new SqlRepository().clearFileLoadingLog();

				buildChecker.add( file.toString() );

            }


		} catch( ParseException | IoException e ) {
	        throw new ParseException( e, "Error on reading Database configuration file({})\n\t{}", file, e.getMessage() );
        }

	}

}
