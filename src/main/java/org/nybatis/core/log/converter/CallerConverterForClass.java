package org.nybatis.core.log.converter;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CallerConverterForClass extends NamedConverter {

	protected String getFullyQualifiedName( ILoggingEvent event ) {
		return new StackTracer( event ).getClassName();
	}

}