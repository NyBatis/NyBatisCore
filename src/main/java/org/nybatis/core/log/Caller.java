package org.nybatis.core.log;

import ch.qos.logback.classic.spi.CallerData;

public class Caller {

	private String className  = CallerData.NA;
	private String lineNumber = CallerData.NA;
	private String fileName   = CallerData.NA;
	private String methodName = CallerData.NA;
	
	private StackTraceElement currentStackTraceElement = null;
	
	public Caller( int callDepth ) {

		try {
			
			currentStackTraceElement = Thread.currentThread().getStackTrace()[ callDepth ];
			
			className  = currentStackTraceElement.getClassName();
			fileName   = currentStackTraceElement.getFileName();
			methodName = currentStackTraceElement.getMethodName();
			lineNumber = Integer.toString( currentStackTraceElement.getLineNumber() );
			
		} catch( Exception e ) {}
	
	}
	
	public String getClassName() {
		return className;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMethodName() {
		return methodName;
	}

}
