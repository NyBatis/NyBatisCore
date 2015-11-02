package org.nybatis.core.log.converter;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class StackTracer {

	private StackTraceElement stackTrace = null;
	
	public StackTracer( ILoggingEvent event ) {

		StackTraceElement[] stackTraces = event.getCallerData();

		if( stackTraces == null ) return;

		for( StackTraceElement stackTrace : stackTraces ) {

			String className = stackTrace.getClassName();

			if( "org.nybatis.core.log.NLogger".equals( className ) ) continue;
			if( "org.nybatis.core.log.NLoggerPrinter".equals( className ) ) continue;

			this.stackTrace = stackTrace;

			break;

		}

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
