package org.nybatis.core.log.layout;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

public class NLoggerPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

	@Override
	public void start() {

		NLoggerPatternLayout patternLayout = hasMessagePattern() ? new NLoggerBasicPatternLayout() : new NLoggerPatternLayout();

		patternLayout.setContext( context );
		patternLayout.setPattern( StringUtil.nvl(getPattern(), " ") );
		patternLayout.setOutputPatternAsHeader( outputPatternAsHeader );
		patternLayout.start();

		this.layout = patternLayout;

		super.start();

	}

	private boolean hasMessagePattern() {
		return Validator.isFound( getPattern(), "%([^ a-zA-Z%]*?)(message|msg|m)([^a-zA-Z]|$)");
	}

}
