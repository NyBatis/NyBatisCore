package org.nybatis.core.log.converter;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CallerConverter extends NamedConverter {

	protected String getFullyQualifiedName( ILoggingEvent event ) {
		StackTracer stackTracer = new StackTracer( event );
		return String.format( "%s.%s:%d", stackTracer.getClassName(), stackTracer.getMethodName(), stackTracer.getLineNumber() );
	}

}