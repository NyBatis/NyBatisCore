package org.nybatis.core.log.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CallerConverterForFile extends ClassicConverter {

	public String convert( ILoggingEvent event ) {
		return new StackTracer( event ).getFileName();
	}

}
