package org.nybatis.core.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import org.nybatis.core.util.StringUtil;
import org.slf4j.LoggerFactory;

/**
 * Common Logger Printer
 *
 * @author nayasis@gmail.com
 * @since  2015.09.30
 *
 */
public class NLoggerPrinter {

	Logger logger;

	public NLoggerPrinter( Caller caller ) {
		this( caller.getClassName() );
	}

	public NLoggerPrinter( Class klass ) {
		this( klass == null ? NLogger.class.getName() : klass.getName() );
	}

	public NLoggerPrinter( String loggerName ) {
		logger = (Logger) LoggerFactory.getLogger( loggerName );
	}

	public void trace( Object message ) {
		log( Level.TRACE, message );
	}

	public void trace( Object format, Object... param ) {
		log( Level.TRACE, format, param );
	}
	
	public void trace( Throwable throwable ) {
		log( Level.TRACE, "{}", throwable );
	}

	public void trace( Object message, Throwable throwable ) {
		log( Level.TRACE, "{}\n{}", message, throwable );
	}

	public void debug( Object message ) {
		log( Level.DEBUG, message );
	}

	public void debug( Object format, Object... param ) {
		log( Level.DEBUG, format, param );
	}
	
	public void debug( Throwable throwable ) {
		log( Level.DEBUG, "{}", throwable );
	}

	public void debug( Object message, Throwable throwable ) {
		log( Level.DEBUG, "{}\n{}", message, throwable );
	}

	public void info( Object message ) {
		log( Level.INFO, message );
	}

	public void info( Object format, Object... param ) {
		log( Level.INFO, format, param );
	}
	
	public void info( Throwable throwable ) {
		log( Level.INFO, "{}", throwable );
	}

	public void info( Object message, Throwable throwable ) {
		log( Level.INFO, "{}\n{}", message, throwable );
	}

	public void warn( Object message ) {
		log( Level.WARN, message );
	}

	public void warn( Object format, Object... param ) {
		log( Level.WARN, format, param );
	}

	public void warn( Throwable throwable ) {
		log( Level.WARN, "{}", throwable );
	}

	public void warn( Object message, Throwable throwable ) {
		log( Level.WARN, "{}\n{}", message, throwable );
	}

	public void error( Object message ) {
		log( Level.ERROR, message );
	}

	public void error( Object format, Object... param ) {
		log( Level.ERROR, format, param );
	}

	public void error( Throwable throwable ) {
		log( Level.ERROR, "{}", throwable );
	}

	public void error( Object message, Throwable throwable ) {
		log( Level.ERROR, "{}\n{}", message, throwable );
	}

	private void log( Level level, Object format, Object... param ) {

		if( ! logger.isEnabledFor( level ) ) return;

		if( format == null ) {
			printLog( level, logger, null );

		} else {

			for( int i = 0, iCnt = param.length; i < iCnt; i++ ) {
				if( param[i] instanceof Throwable ) {
					param[i] = getThrowableString( (Throwable) param[i] );
				}
			}

			printLog( level, logger, StringUtil.format( format, param ) );

		}

	}

	protected boolean isEnabledFor( Level level ) {
		return logger.isEnabledFor( level );
	}

	public boolean isTraceEnabled() {
		return isEnabledFor( Level.TRACE );
	}

	public boolean isDebugEnabled() {
		return isEnabledFor( Level.DEBUG );
	}
	
	public boolean isInfoEnabled() {
		return isEnabledFor( Level.INFO );
	}
	
	public boolean isWarnEnabled() {
		return isEnabledFor( Level.WARN );
	}
	
	public boolean isErrorEnabled() {
		return isEnabledFor( Level.ERROR );
	}
	
	private void printLog( Level level, Logger logger, String value ) {

		switch( level.levelInt ) {
			
			case Level.TRACE_INT:
				logger.trace( value );
				break;
			case Level.DEBUG_INT:
				logger.debug( value );
				break;
			case Level.INFO_INT:
				logger.info( value );
				break;
			case Level.WARN_INT:
				logger.warn( value );
				break;
			case Level.ERROR_INT:
				logger.error( value );
				break;
				
		}

	}

	protected String getThrowableString( Throwable throwable ) {

		if( throwable == null ) return null;

		ThrowableProxy throwableProxy = new ThrowableProxy( throwable );
		throwableProxy.calculatePackagingData();
		return ThrowableProxyUtil.asString( throwableProxy );

	}

}