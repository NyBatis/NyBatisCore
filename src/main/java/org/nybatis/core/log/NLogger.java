package org.nybatis.core.log;


import org.nybatis.core.conf.Const;

import ch.qos.logback.classic.Level;
import org.slf4j.Marker;

/**
 * Common Static Logger
 *
 * <pre>
 * it is simple logback wrapper and share configuration with other logback logger instances.
 * </pre>
 *
 * @author nayasis@gmail.com
 * @since  2013.01.14
 *
 */
public class NLogger {

	private static boolean isConfigurationIinitialized = false;

	private NLogger() {}
	
	private static void loadDefaultConfiguration() {
		if( ! isConfigurationIinitialized ) {
			loadConfiguration();
			isConfigurationIinitialized = true;
		}
	}

	public static void loadConfiguration() {
		loadConfiguration( Const.path.getConfigLogger() + "/logback.xml" );
	}

	/**
	 * Get Logger for specific name
	 * @param loggerName logger name
	 * @return logger
	 */
	public static NLoggerPrinter getLogger( String loggerName ) {
		loadDefaultConfiguration();
		return new NLoggerPrinter( loggerName );
	}

	/**
	 * get logger for specific call depth
	 *
	 * @param callDepth	caller depth (default : 4)
	 * @return logger
	 */
	public static NLoggerPrinter getLogger( int callDepth ) {
		loadDefaultConfiguration();
		return new NLoggerPrinter( new Caller( 4 + callDepth) );
	}

	private static NLoggerPrinter getLogger() {
		return getLogger( 0 );
	}

	/**
	 * Get Logger for class
	 * @param klass class
	 * @return logger
	 */
	public static NLoggerPrinter getLogger( Class klass ) {
		return new NLoggerPrinter( klass );
	}

	/**
	 * Load logback configuration
	 *
	 * @param filePath configuration file path
	 */
	public static void loadConfiguration( String filePath ) {
		new NLoggerConfigLoader().loadConfiguration( filePath );
	}

	public static void trace( Object message ) {
		getLogger().trace( message );
	}

	public static void trace( Object format, Object... param ) {
		getLogger().trace( format, param );
	}
	
	public static void trace( Throwable throwable ) {
		getLogger().trace( throwable );
	}

	public static void trace( Object message, Throwable throwable ) {
		getLogger().trace( message, throwable );
	}

	public static void debug( Object message ) {
		getLogger().debug( message );
	}

	public static void debug( Object format, Object... param ) {
		getLogger().debug( format, param );
	}
	
	public static void debug( Throwable throwable ) {
		getLogger().debug( throwable );
	}

	public static void debug( Object message, Throwable throwable ) {
		getLogger().debug( message, throwable );
	}

	public static void info( Object message ) {
		getLogger().info( message );
	}

	public static void info( Object format, Object... param ) {
		getLogger().info( format, param );
	}
	
	public static void info( Throwable throwable ) {
		getLogger().info( throwable );
	}

	public static void info( Object message, Throwable throwable ) {
		getLogger().info( message, throwable );
	}

	public static void warn( Object message ) {
		getLogger().warn( message );
	}

	public static void warn( Object format, Object... param ) {
		getLogger().warn( format, param );
	}

	public static void warn( Throwable throwable ) {
		getLogger().warn( throwable );
	}

	public static void warn( Object message, Throwable throwable ) {
		getLogger().warn( message, throwable );
	}

	public static void error( Object message ) {
		getLogger().error( message );
	}

	public static void error( Object format, Object... param ) {
		getLogger().error( format, param );
	}

	public static void error( Throwable throwable ) {
		getLogger().error( throwable );
	}

	public static void error( Object message, Throwable throwable ) {
		getLogger().error( message, throwable );
	}

	public static void trace( Marker marker, Object message ) {
		getLogger().trace( marker, message );
	}

	public static void trace( Marker marker, Object format, Object... param ) {
		getLogger().trace( marker, format, param );
	}

	public static void trace( Marker marker, Throwable throwable ) {
		getLogger().trace( marker, throwable );
	}

	public static void trace( Marker marker, Object message, Throwable throwable ) {
		getLogger().trace( marker, message, throwable );
	}

	public static void debug( Marker marker, Object message ) {
		getLogger().debug( marker, message );
	}

	public static void debug( Marker marker, Object format, Object... param ) {
		getLogger().debug( marker, format, param );
	}

	public static void debug( Marker marker, Throwable throwable ) {
		getLogger().debug( marker, throwable );
	}

	public static void debug( Marker marker, Object message, Throwable throwable ) {
		getLogger().debug( marker, message, throwable );
	}

	public static void info( Marker marker, Object message ) {
		getLogger().info( marker, message );
	}

	public static void info( Marker marker, Object format, Object... param ) {
		getLogger().info( marker, format, param );
	}

	public static void info( Marker marker, Throwable throwable ) {
		getLogger().info( marker, throwable );
	}

	public static void info( Marker marker, Object message, Throwable throwable ) {
		getLogger().info( marker, message, throwable );
	}

	public static void warn( Marker marker, Object message ) {
		getLogger().warn( marker, message );
	}

	public static void warn( Marker marker, Object format, Object... param ) {
		getLogger().warn( marker, format, param );
	}

	public static void warn( Marker marker, Throwable throwable ) {
		getLogger().warn( marker, throwable );
	}

	public static void warn( Marker marker, Object message, Throwable throwable ) {
		getLogger().warn( marker, message, throwable );
	}

	public static void error( Marker marker, Object message ) {
		getLogger().error( marker, message );
	}

	public static void error( Marker marker, Object format, Object... param ) {
		getLogger().error( marker, format, param );
	}

	public static void error( Marker marker, Throwable throwable ) {
		getLogger().error( marker, throwable );
	}

	public static void error( Marker marker, Object message, Throwable throwable ) {
		getLogger().error( marker, message, throwable );
	}

	/**
	 * specify caller depth to print class name and line in logback appender.
	 *
	 * @param depth	callder depth
	 * @return self instance
	 */
	public static NLoggerPrinter setCallderDepth( int depth ) {
		return getLogger().setCallerDepth( depth );
	}

	public static void setLevel( Level newLevel ) {
		getLogger().setLevel( newLevel );
	}

	public static void setAdditive( boolean additive ) {
		getLogger().setAdditive( additive );
	}

	public static boolean isTraceEnabled() {
		return getLogger().isEnabledFor( Level.TRACE );
	}

	public static boolean isDebugEnabled() {
		return getLogger().isEnabledFor( Level.DEBUG );
	}
	
	public static boolean isInfoEnabled() {
		return getLogger().isEnabledFor( Level.INFO );
	}
	
	public static boolean isWarnEnabled() {
		return getLogger().isEnabledFor( Level.WARN );
	}
	
	public static boolean isErrorEnabled() {
		return getLogger().isEnabledFor( Level.ERROR );
	}
	
}