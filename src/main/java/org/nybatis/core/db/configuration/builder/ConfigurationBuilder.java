package org.nybatis.core.db.configuration.builder;

import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.node.Node;

import java.util.HashSet;
import java.util.Set;

public class ConfigurationBuilder {

	private static Set<String> loadedFiles = new HashSet<>();

	public void readFrom( String file ) {
		readFrom( file, false );
	}

	public void readFrom( String file, boolean reload ) {

		NLogger.debug( "load database configuration from [{}]", file );

		if( reload == false && isLoaded(file) ) {
			NLogger.debug( "cancel to load because already loaded. [{}]", file );
			return;
		}

		try {

			synchronized( loadedFiles ) {

				String xml = FileUtil.readResourceFrom( file );

				if( StringUtil.isEmpty(xml) ) {
					throw new DatabaseConfigurationException( "there is no contents in file path({})", file );
				}

				NXml xmlReader = new NXmlDeformed( xml );

				Node root = xmlReader.getRoot();
				PropertyResolver propertyResolver = new PropertyResolver( root.getChildElement("properties") );

				for( Node environment : root.getChildElements("environment") ) {
					new EnvironmentBuilder( environment, propertyResolver );
					new SqlBuilder( propertyResolver, getDirectory(file) ).setSql( environment );
				}

				loadedFiles.add( file.toString() );

            }

		} catch( ParseException | UncheckedIOException e ) {
	        throw new ParseException( e, "Error on reading Database configuration file({})\n\t{}", file, e.getMessage() );
        }

	}

	private String getDirectory( String file ) {
		int seperator = file.lastIndexOf( "/" );
		return seperator < 0 ? file : file.substring( 0, seperator );
	}

	private boolean isLoaded( String file ) {
		if( file == null ) return true;
		return loadedFiles.contains( file );
	}

}
