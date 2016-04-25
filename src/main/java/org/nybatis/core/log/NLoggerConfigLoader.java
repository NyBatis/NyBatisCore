package org.nybatis.core.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.JoranException;
import org.nybatis.core.conf.Const;
import org.nybatis.core.file.FileUtil;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NLoggerConfigLoader {

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
        	} catch( IOException e ) {
	            e.printStackTrace();
            }
        	
        }
		
	}

	private LoggerContext getLoggerContext() {
	    return (LoggerContext) LoggerFactory.getILoggerFactory();
    }
	
	private boolean isConfigurationNotLoaded() {
		return getLoggerContext().getObject( CoreConstants.SAFE_JORAN_CONFIGURATION ) == null;
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

		InputStream stream;

		String configurationPath = Const.profile.apply( filePath );

		if( ! FileUtil.isResourceExisted(configurationPath) ) {
			
			System.err.printf( "Logback external configuration file [%s] doesn't exist or can't be read.\n", filePath );
			
			if( ! isConfigurationNotLoaded() ) {
				System.err.printf( "NLogger maybe read default logback configuration.\n\n" );
				return null;
				
			} else {
				System.err.printf( "NLogger uses default simple configuration.\n\n" );
				stream = new ByteArrayInputStream( getDefaultConfiguration().getBytes() );
				
			}
			
		} else {
			stream = FileUtil.getResourceAsStream( configurationPath );
		}
		
		return stream;
		
	}
	
}
