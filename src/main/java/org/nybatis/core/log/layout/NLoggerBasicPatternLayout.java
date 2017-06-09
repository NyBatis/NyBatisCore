package org.nybatis.core.log.layout;

import ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Pattern layout that have all logback pattern flag including [message].
 *
 * It works like logback's PatternLayout except that
 */
public class NLoggerBasicPatternLayout extends NLoggerPatternLayout {

	private static final Map<String, String> defaultConverterMap = new HashMap<String, String>();

	public NLoggerBasicPatternLayout() {

		defaultConverterMap.putAll( super.getDefaultConverterMap() );
		defaultConverterMap.put( "m", MessageConverter.class.getName() );
		defaultConverterMap.put( "msg", MessageConverter.class.getName() );
		defaultConverterMap.put( "message", MessageConverter.class.getName() );

		this.postCompileProcessor = new EnsureExceptionHandling();

	}

	public Map<String, String> getDefaultConverterMap() {
		return defaultConverterMap;
	}

	@Override
	public String doLayout( ILoggingEvent event ) {
		return ! this.isStarted() ? "" : this.writeLoopOnConverters(event);
	}

}
