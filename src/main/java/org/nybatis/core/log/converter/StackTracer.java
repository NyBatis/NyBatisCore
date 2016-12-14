package org.nybatis.core.log.converter;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import java.util.List;

public class StackTracer {

	public static final String CALLER_DEPTH = "nybatis.core.log.NLoggerPrinter.callerDepth";

	private StackTraceElement stackTrace = null;
	
	public StackTracer( ILoggingEvent event ) {

		StackTraceElement[] stackTraces = event.getCallerData();

		if( stackTraces == null ) return;

		int callerDepth = getCallerDepth( event );

		int depth = -1;

		for( StackTraceElement stackTrace : stackTraces ) {

			String className = stackTrace.getClassName();

			if( "org.nybatis.core.log.NLogger".equals( className ) ) continue;
			if( "org.nybatis.core.log.NLoggerPrinter".equals( className ) ) continue;

			depth++;

			if( callerDepth > 0  && callerDepth != depth ) continue;

			this.stackTrace = stackTrace;

			break;

		}

	}

	private int getCallerDepth( ILoggingEvent event ) {

		Object[] arguments = event.getArgumentArray();

		if( Validator.isNotEmpty( arguments ) ) {

			Object callerDepth = arguments[ arguments.length - 1 ];

			if( callerDepth != null && callerDepth instanceof String && ((String) callerDepth).startsWith( CALLER_DEPTH )) {

				List<String> split = StringUtil.split( callerDepth, "::" );

				if( split.size() == 2 ) {

					int depth = new PrimitiveConverter( split.get( 1 ) ).toInt();

					return Math.max( depth, 0 );
				}

			}

		}

		return 0;

	}

	public String getClassName() {
		return stackTrace == null ? CallerData.NA : stackTrace.getClassName(); 
	}
	
	public String getFileName() {
		return stackTrace == null ? CallerData.NA : stackTrace.getFileName(); 
	}
	
	public String getMethodName() {
		return stackTrace == null ? CallerData.NA : stackTrace.getMethodName(); 
	}
	
	public String getLineNumber() {
		return stackTrace == null ? CallerData.NA : Integer.toString( stackTrace.getLineNumber() ); 
	}
	
}
