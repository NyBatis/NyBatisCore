package org.nybatis.core.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import java.util.Arrays;
import java.util.List;
import org.nybatis.core.log.converter.StackTracer;
import org.nybatis.core.model.NList;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.Types;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Common Logger Printer
 *
 * @author nayasis@gmail.com
 * @since  2015.09.30
 *
 */
public class NLoggerPrinter {

	private Logger logger;

	private Integer callerDepth;

	public NLoggerPrinter( Caller caller ) {
		this( caller.getClassName() );
	}

	public NLoggerPrinter( Class klass ) {
		this( klass == null ? NLogger.class.getName() : klass.getName() );
	}

	public NLoggerPrinter( String loggerName ) {
		logger = (Logger) LoggerFactory.getLogger( loggerName );
	}

	public Level getLevel() {
		return logger.getLevel();
	}

	public void setLevel( Level newLevel ) {
		logger.setLevel( newLevel );
	}

	public void setAdditive( boolean additive ) {
		logger.setAdditive( additive );
	}

	public void trace( Object message ) {
		log( null, Level.TRACE, message );
	}

	public void trace( Object format, Object... param ) {
		log( null, Level.TRACE, format, param );
	}

	public void trace( Throwable throwable ) {
		log( null, Level.TRACE, "{}", throwable );
	}

	public void trace( Object message, Throwable throwable ) {
		log( null, Level.TRACE, "{}\n{}", message, throwable );
	}

	public void debug( Object message ) {
		log( null, Level.DEBUG, message );
	}

	public void debug( Object format, Object... param ) {
		log( null, Level.DEBUG, format, param );
	}

	public void debug( Throwable throwable ) {
		log( null, Level.DEBUG, "{}", throwable );
	}

	public void debug( Object message, Throwable throwable ) {
		log( null, Level.DEBUG, "{}\n{}", message, throwable );
	}

	public void info( Object message ) {
		log( null, Level.INFO, message );
	}

	public void info( Object format, Object... param ) {
		log( null, Level.INFO, format, param );
	}

	public void info( Throwable throwable ) {
		log( null, Level.INFO, "{}", throwable );
	}

	public void info( Object message, Throwable throwable ) {
		log( null, Level.INFO, "{}\n{}", message, throwable );
	}

	public void warn( Object message ) {
		log( null, Level.WARN, message );
	}

	public void warn( Object format, Object... param ) {
		log( null, Level.WARN, format, param );
	}

	public void warn( Throwable throwable ) {
		log( null, Level.WARN, "{}", throwable );
	}

	public void warn( Object message, Throwable throwable ) {
		log( null, Level.WARN, "{}\n{}", message, throwable );
	}

	public void error( Object message ) {
		log( null, Level.ERROR, message );
	}

	public void error( Object format, Object... param ) {
		log( null, Level.ERROR, format, param );
	}

	public void error( Throwable throwable ) {
		log( null, Level.ERROR, "{}", throwable );
	}

	public void error( Object message, Throwable throwable ) {
		log( null, Level.ERROR, "{}\n{}", message, throwable );
	}

	public void trace( Marker marker, Object message ) {
		log( marker, Level.TRACE, message );
	}

	public void trace( Marker marker, Object format, Object... param ) {
		log( marker, Level.TRACE, format, param );
	}

	public void trace( Marker marker, Throwable throwable ) {
		log( marker, Level.TRACE, "{}", throwable );
	}

	public void trace( Marker marker, Object message, Throwable throwable ) {
		log( marker, Level.TRACE, "{}\n{}", message, throwable );
	}

	public void debug( Marker marker, Object message ) {
		log( marker, Level.DEBUG, message );
	}

	public void debug( Marker marker, Object format, Object... param ) {
		log( marker, Level.DEBUG, format, param );
	}

	public void debug( Marker marker, Throwable throwable ) {
		log( marker, Level.DEBUG, "{}", throwable );
	}

	public void debug( Marker marker, Object message, Throwable throwable ) {
		log( marker, Level.DEBUG, "{}\n{}", message, throwable );
	}

	public void info( Marker marker, Object message ) {
		log( marker, Level.INFO, message );
	}

	public void info( Marker marker, Object format, Object... param ) {
		log( marker, Level.INFO, format, param );
	}

	public void info( Marker marker, Throwable throwable ) {
		log( marker, Level.INFO, "{}", throwable );
	}

	public void info( Marker marker, Object message, Throwable throwable ) {
		log( marker, Level.INFO, "{}\n{}", message, throwable );
	}

	public void warn( Marker marker, Object message ) {
		log( marker, Level.WARN, message );
	}

	public void warn( Marker marker, Object format, Object... param ) {
		log( marker, Level.WARN, format, param );
	}

	public void warn( Marker marker, Throwable throwable ) {
		log( marker, Level.WARN, "{}", throwable );
	}

	public void warn( Marker marker, Object message, Throwable throwable ) {
		log( marker, Level.WARN, "{}\n{}", message, throwable );
	}

	public void error( Marker marker, Object message ) {
		log( marker, Level.ERROR, message );
	}

	public void error( Marker marker, Object format, Object... param ) {
		log( marker, Level.ERROR, format, param );
	}

	public void error( Marker marker, Throwable throwable ) {
		log( marker, Level.ERROR, "{}", throwable );
	}

	public void error( Marker marker, Object message, Throwable throwable ) {
		log( marker, Level.ERROR, "{}\n{}", message, throwable );
	}

	private void log( Marker marker, Level level, Object format, Object... param ) {

		try {

			if( ! logger.isEnabledFor( level ) ) return;

			if( format == null ) {
				printLog( marker, level, logger, "null" );

			} else if( param.length == 0 ) {

				if( format instanceof List ) {

					try {
						printLog( marker, level, logger, new NList( (List) format ).toString() );
					} catch( Exception e ) {
						printLog( marker, level, logger, format.toString() );
					}

				} else if( Types.isArray(format) ) {
					printLog( marker, level, logger, Arrays.deepToString( (Object[]) format ) );
				} else if( format instanceof Throwable ) {
					printLog( marker, level, logger, toString( (Throwable) format ) );
				} else {
					printLog( marker, level, logger, format.toString() );
				}

			} else {

				for( int i = 0, iCnt = param.length; i < iCnt; i++ ) {
					if( param[i] instanceof Throwable ) {
						param[i] = toString( (Throwable) param[i] );
					} else if( Types.isArray(param[i]) ) {
						param[i] = Arrays.deepToString( (Object[]) param[i] );
					}
				}

				printLog( marker, level, logger, StringUtil.format( format, param ) );

			}

		} finally {
			callerDepth = null;
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

	/**
	 * specify caller depth to print class name and line in logback appender.
	 *
	 * @param depth	callder depth
	 * @return self instance
	 */
	public NLoggerPrinter setCallerDepth( int depth ) {
		callerDepth = depth;
		return this;
	}

	private void printLog( Marker marker, Level level, Logger logger, String value ) {

		String callerDepth = null;

		if( this.callerDepth != null ) {
			callerDepth = String.format( "%s::%d", StackTracer.CALLER_DEPTH, this.callerDepth );
		}

		switch( level.levelInt ) {

			case Level.TRACE_INT:
				logger.trace( marker, value, callerDepth );
				break;
			case Level.DEBUG_INT:
				logger.debug( marker, value, callerDepth );
				break;
			case Level.INFO_INT:
				logger.info( marker, value, callerDepth );
				break;
			case Level.WARN_INT:
				logger.warn( marker, value, callerDepth );
				break;
			case Level.ERROR_INT:
				logger.error( marker, value, callerDepth );
				break;
				
		}

	}

	protected String toString( Throwable throwable ) {

		if( throwable == null ) return null;

		ThrowableProxy throwableProxy = new ThrowableProxy( throwable );
		throwableProxy.calculatePackagingData();
		return ThrowableProxyUtil.asString( throwableProxy );

	}

	public Logger getNativeLogger() {
		return this.logger;
	}

}