package org.nybatis.core.log;


import org.nybatis.core.conf.Const;

import ch.qos.logback.classic.Level;

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
	
	private static NLoggerPrinter getLogger() {
		loadConfiguration();
		return new NLoggerPrinter( new Caller(4) );
	}

	private static void loadConfiguration() {
		if( ! isConfigurationIinitialized ) {
			loadConfiguration( Const.path.getConfigLogger() + "/logback.xml" );
			isConfigurationIinitialized = true;
		}
	}

	/**
	 * Get Logger for specific name
	 * @param loggerName logger name
	 * @return logger
	 */
	public static NLoggerPrinter getLogger( String loggerName ) {
		loadConfiguration();
		return new NLoggerPrinter( loggerName );
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