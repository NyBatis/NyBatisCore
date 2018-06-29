package org.nybatis.core.log;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.BufferOverflowException;
import org.nybatis.core.conf.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NLoggerTest {

	private static final Logger logger = LoggerFactory.getLogger( NLoggerTest.class );

	@Test
	public void basicTest() {

		NLogger.debug( "start !!" );

		try {
			exceptionRaiser();
		} catch( Exception e ) {
			NLogger.error( e );
//			System.out.println( "-------------------------");
			NLogger.error( "error({})\n{}", e.getMessage(), e );
			logger.error( e.getMessage(), e );
		}
		
		NLogger.info(  "Merong\nMerong : {}", "Nayasis" );

		NLogger.warn( null );

		NLogger.trace( "trace what ??" );

		NLogger.info( "trace what ?? : {}", "Nayasis" );
		
	}
	
	private void exceptionRaiser() throws Exception {
		throw new Exception( "Merong", new BufferOverflowException() );
	}

	@Test
	public void multiLineTest() {
		String format = "사랑하는\n나의\n어머니";
		NLogger.debug( format );
		logger.debug( format );
	}


	@Test
	public void specificLogger() {
		NLogger.getLogger( "specific.test.log" ).debug( "사랑하는\n나의\n어머니" );
	}

	@Test
	public void specificCaller() {
		NLogger.debug( "merong" );
		NLogger.setCallderDepth( 0 ).debug( "merong" );
		NLogger.setCallderDepth( 1 ).debug( "merong" );
	}

	@Test
	public void loglevelTest() {

		NLogger.loadConfiguration( Const.path.getRoot() + "/config/log/logback-subclass.xml" );

		PrintStream  srcOut      = System.out;
		OutputStream testOut     = new ByteArrayOutputStream();
		PrintStream  printStream = new PrintStream( testOut );

		System.setOut( printStream );

		try {
			logger.debug( "merong by logback" );
			NLogger.debug( "merong by NLogger" );
			new SubClass().test();
		} finally {
			System.setOut( srcOut );
			printStream.close();
		}

		String consoleOutput = testOut.toString();

		// do not print in SubClass log
		Assert.assertEquals( consoleOutput.split( "\n" ).length, 2 );

	}

}
