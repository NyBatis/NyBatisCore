package org.nybatis.core.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.JoranException;
import org.nybatis.core.conf.Const;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.validation.Validator;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * NLogger configuration loader
 */
public class NLoggerConfigLoader {

	/**
	 * load configuration
	 *
	 * @param filePath file or resource path of logback configuration
	 */
	public void loadConfiguration( String filePath ) {

		InputStream stream = getConfiguration( filePath );
		
		if( stream == null ) return;
		
		LoggerContext context = getLoggerContext();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		context.reset();
		
		try {
			configurator.doConfigure( stream );

		} catch( JoranException e ) {
			e.printStackTrace();
			
        } finally {
        	try {
        		stream.close();
        	} catch( IOException e ) {}
        }

	}

	private LoggerContext getLoggerContext() {
	    return (LoggerContext) LoggerFactory.getILoggerFactory();
    }
	
	private boolean isConfigurationLoaded() {
		return getLoggerContext().getObject( CoreConstants.SAFE_JORAN_CONFIGURATION ) != null;
	}

	private String getDefaultConfiguration() {

		return
			"<configuration>" + 
			  "<appender name=\"console\" class=\"ch.qos.logback.core.ConsoleAppender\">" + 
			    "<encoder class=\"org.nybatis.core.log.layout.NLoggerPatternLayoutEncoder\">" +
				  "<pattern>%d{HH:mm:ss.SSS} %-5level %35(\\(%F:%L\\))</pattern>" +
			    "</encoder>" +
			  "</appender> " + 
			  "<root level=\"trace\">" +
			    "<appender-ref ref=\"console\" />" +
			  "</root>" +
			  "<logger name=\"com.jayway.jsonpath.internal.path.CompiledPath\" level=\"off\" additivity=\"false\" />" +
			"</configuration>";

	}
	
	private InputStream getConfiguration( String filePath ) {

		String configurationPath = Const.profile.apply( filePath );

		if( ! FileUtil.isResourceExisted(configurationPath) ) {

			if( ! FileUtil.isResourceExisted(filePath) ) {
				System.err.printf( "Logback external configuration file [%s] doesn't exist or can't be read.\n", filePath );
				return loadDefaultConfiguration();
			}

			configurationPath = filePath;

		}

		InputStream stream = FileUtil.getResourceAsStream( configurationPath );

		if( Validator.isEmpty(stream) ) {
			stream = loadDefaultConfiguration();
		}

		return stream;

	}

	private InputStream loadDefaultConfiguration() {
		if( isConfigurationLoaded() ) {
			System.err.printf( "NLogger maybe read default logback configuration.\n\n" );
			return null;
		}
		System.err.printf( "NLogger uses default simple configuration.\n\n" );
		return new ByteArrayInputStream( getDefaultConfiguration().getBytes() );
	}

}
