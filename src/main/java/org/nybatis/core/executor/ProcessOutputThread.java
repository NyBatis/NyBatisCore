package org.nybatis.core.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.worker.WorkerReadLine;

public class ProcessOutputThread extends Thread {

	private InputStream    inputStream;
	private StringBuffer   message;
	private WorkerReadLine worker;

	/**
	 * 생성자
	 *
	 * @param inputStream Process 에서 출력될 메세지 Stream 객체
	 * @param message 메세지를 저장할 공간
	 */
	public ProcessOutputThread( InputStream inputStream, StringBuffer message, WorkerReadLine worker ) {
		this.inputStream = inputStream;
		this.message     = message;
		this.worker      = worker;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		try {

			readInputStream( inputStream, message, worker );

		} catch ( Exception e ) {
			NLogger.error( e );

		} finally {
			try { if (inputStream != null) inputStream.close(); } catch ( IOException e ) {}
			NLogger.trace( "ProcessOuputThread({}) is closed", Thread.currentThread().getName() );
		}
	}

	/**
	 * 입력되는 문자열 Stream 을 문자로 변경해 돌려준다.
	 *
	 * @param inputStream 문자열 Stream
	 * @param message     InputStream 결과를 적재할 공간
	 * @return 문자
	 */
	private void readInputStream( InputStream inputStream, StringBuffer message, WorkerReadLine worker ) {

		String  buffer;

		try (
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, Const.platform.osCharset) )
		){

			while ( ! isInterrupted() && (buffer = reader.readLine()) != null ) {

				if( message != null ) {
					message.append( buffer ).append( '\n' );
				}

				if( worker != null ) {
					worker.execute( buffer );
				}

			}

		} catch ( IOException e ) {
			NLogger.error( e );
			interrupt();
		}

	}

}
