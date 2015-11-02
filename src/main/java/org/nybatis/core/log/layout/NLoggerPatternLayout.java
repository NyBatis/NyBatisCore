package org.nybatis.core.log.layout;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import org.nybatis.core.log.converter.CallerConverterForClass;
import org.nybatis.core.log.converter.CallerConverterForFile;
import org.nybatis.core.log.converter.CallerConverterForLine;
import org.nybatis.core.log.converter.CallerConverterForMethod;
import ch.qos.logback.classic.pattern.ContextNameConverter;
import ch.qos.logback.classic.pattern.DateConverter;
import ch.qos.logback.classic.pattern.LevelConverter;
import ch.qos.logback.classic.pattern.LoggerConverter;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.pattern.MarkerConverter;
import ch.qos.logback.classic.pattern.NopThrowableInformationConverter;
import ch.qos.logback.classic.pattern.PropertyConverter;
import ch.qos.logback.classic.pattern.RelativeTimeConverter;
import ch.qos.logback.classic.pattern.ThreadConverter;
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.pattern.color.BlackCompositeConverter;
import ch.qos.logback.core.pattern.color.BlueCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldGreenCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldMagentaCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldRedCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter;
import ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter;
import ch.qos.logback.core.pattern.color.CyanCompositeConverter;
import ch.qos.logback.core.pattern.color.GrayCompositeConverter;
import ch.qos.logback.core.pattern.color.GreenCompositeConverter;
import ch.qos.logback.core.pattern.color.MagentaCompositeConverter;
import ch.qos.logback.core.pattern.color.RedCompositeConverter;
import ch.qos.logback.core.pattern.color.WhiteCompositeConverter;
import ch.qos.logback.core.pattern.color.YellowCompositeConverter;
import ch.qos.logback.core.pattern.parser.Parser;

public class NLoggerPatternLayout extends PatternLayoutBase<ILoggingEvent> {

	private static final Map<String, String> defaultConverterMap = new HashMap<String, String>();
	private static final String              HEADER_PREFIX       = "#nayasis.common.log pattern: ";

	static {

		defaultConverterMap.putAll( Parser.DEFAULT_COMPOSITE_CONVERTER_MAP );

		defaultConverterMap.put( "d", DateConverter.class.getName() );
		defaultConverterMap.put( "date", DateConverter.class.getName() );

		defaultConverterMap.put( "r", RelativeTimeConverter.class.getName() );
		defaultConverterMap.put( "relative", RelativeTimeConverter.class.getName() );

		defaultConverterMap.put( "level", LevelConverter.class.getName() );
		defaultConverterMap.put( "le", LevelConverter.class.getName() );
		defaultConverterMap.put( "p", LevelConverter.class.getName() );

		defaultConverterMap.put( "t", ThreadConverter.class.getName() );
		defaultConverterMap.put( "thread", ThreadConverter.class.getName() );

		defaultConverterMap.put( "lo", LoggerConverter.class.getName() );
		defaultConverterMap.put( "logger", LoggerConverter.class.getName() );
		defaultConverterMap.put( "c", LoggerConverter.class.getName() );

//		defaultConverterMap.put( "m", MessageConverter.class.getName() );
//		defaultConverterMap.put( "msg", MessageConverter.class.getName() );
//		defaultConverterMap.put( "message", MessageConverter.class.getName() );

		defaultConverterMap.put( "C",     CallerConverterForClass.class.getName() );
		defaultConverterMap.put( "class", CallerConverterForClass.class.getName() );

		defaultConverterMap.put( "M",      CallerConverterForMethod.class.getName() );
		defaultConverterMap.put( "method", CallerConverterForMethod.class.getName() );

		defaultConverterMap.put( "L", CallerConverterForLine.class.getName() );
		defaultConverterMap.put( "line", CallerConverterForLine.class.getName() );

		defaultConverterMap.put( "F",    CallerConverterForFile.class.getName() );
		defaultConverterMap.put( "file", CallerConverterForFile.class.getName() );

		defaultConverterMap.put( "X",   MDCConverter.class.getName() );
		defaultConverterMap.put( "mdc", MDCConverter.class.getName() );

		defaultConverterMap.put( "ex",            ThrowableProxyConverter.class.getName()               );
		defaultConverterMap.put( "exception",     ThrowableProxyConverter.class.getName()               );
		defaultConverterMap.put( "rEx",           RootCauseFirstThrowableProxyConverter.class.getName() );
		defaultConverterMap.put( "rootException", RootCauseFirstThrowableProxyConverter.class.getName() );
		defaultConverterMap.put( "throwable",     ThrowableProxyConverter.class.getName()               );

		defaultConverterMap.put( "xEx",        ExtendedThrowableProxyConverter.class.getName() );
		defaultConverterMap.put( "xException", ExtendedThrowableProxyConverter.class.getName() );
		defaultConverterMap.put( "xThrowable", ExtendedThrowableProxyConverter.class.getName() );

		defaultConverterMap.put( "nopex",        NopThrowableInformationConverter.class.getName() );
		defaultConverterMap.put( "nopexception", NopThrowableInformationConverter.class.getName() );

		defaultConverterMap.put( "cn",          ContextNameConverter.class.getName() );
		defaultConverterMap.put( "contextName", ContextNameConverter.class.getName() );

//		defaultConverterMap.put( "caller", CallerDataConverter.class.getName() );

		defaultConverterMap.put( "marker", MarkerConverter.class.getName() );

		defaultConverterMap.put( "property", PropertyConverter.class.getName() );

//		defaultConverterMap.put( "n", LineSeparatorConverter.class.getName() );

		defaultConverterMap.put( "black",       BlackCompositeConverter.class.getName()        );
		defaultConverterMap.put( "red",         RedCompositeConverter.class.getName()          );
		defaultConverterMap.put( "green",       GreenCompositeConverter.class.getName()        );
		defaultConverterMap.put( "yellow",      YellowCompositeConverter.class.getName()       );
		defaultConverterMap.put( "blue",        BlueCompositeConverter.class.getName()         );
		defaultConverterMap.put( "magenta",     MagentaCompositeConverter.class.getName()      );
		defaultConverterMap.put( "cyan",        CyanCompositeConverter.class.getName()         );
		defaultConverterMap.put( "white",       WhiteCompositeConverter.class.getName()        );
		defaultConverterMap.put( "gray",        GrayCompositeConverter.class.getName()         );
		defaultConverterMap.put( "boldRed",     BoldRedCompositeConverter.class.getName()      );
		defaultConverterMap.put( "boldGreen",   BoldGreenCompositeConverter.class.getName()    );
		defaultConverterMap.put( "boldYellow",  BoldYellowCompositeConverter.class.getName()   );
		defaultConverterMap.put( "boldBlue",    BoldBlueCompositeConverter.class.getName()     );
		defaultConverterMap.put( "boldMagenta", BoldMagentaCompositeConverter.class.getName()  );
		defaultConverterMap.put( "boldCyan",    BoldCyanCompositeConverter.class.getName()     );
		defaultConverterMap.put( "boldWhite",   BoldWhiteCompositeConverter.class.getName()    );
		defaultConverterMap.put( "highlight",   HighlightingCompositeConverter.class.getName() );

	}

	public NLoggerPatternLayout() {}

	public Map<String, String> getDefaultConverterMap() {
		return defaultConverterMap;
	}

	public String doLayout( ILoggingEvent event ) {

		if( ! isStarted() ) { return CoreConstants.EMPTY_STRING; }

		String header = writeLoopOnConverters( event );

		header = ( " ".equals(header) ) ? "" : header + " ";

		String message = event.getFormattedMessage();

		if( event.getThrowableProxy() != null ) {
			message += "\n" + ThrowableProxyUtil.asString( event.getThrowableProxy() );
		}

		StringBuilder sb = new StringBuilder( 128 );

    	StringBuilder buffer = new StringBuilder();

		for( char c : message.toCharArray() ) {

			switch( c ) {

				case '\r' : continue;
				case '\n' :
					sb.append( header ).append( buffer ).append( CoreConstants.LINE_SEPARATOR );
					buffer = new StringBuilder();
					break;

				default :
					buffer.append( c );

			}

		}

		sb.append( header ).append( buffer ).append( CoreConstants.LINE_SEPARATOR );

    	return sb.toString();

	}

	@Override
	protected String getPresentationHeaderPrefix() {
		return HEADER_PREFIX;
	}

}
