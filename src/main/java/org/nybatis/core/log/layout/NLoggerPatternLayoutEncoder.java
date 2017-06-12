package org.nybatis.core.log.layout;

import org.nybatis.core.util.StringUtil;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

public class NLoggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

	@Override
	public void start() {

		boolean hasMessagePatternFlag = StringUtil.nvl(getPattern()).indexOf( "%m" ) >= 0;

		NLoggerPatternLayout patternLayout = hasMessagePatternFlag ? new NLoggerBasicPatternLayout() : new NLoggerPatternLayout();

		patternLayout.setContext( context );
		patternLayout.setPattern( StringUtil.nvl(getPattern(), " ") );
		patternLayout.setOutputPatternAsHeader( outputPatternAsHeader );
		patternLayout.start();

		this.layout = patternLayout;

		super.start();

	}

}
