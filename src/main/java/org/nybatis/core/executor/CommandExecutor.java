package org.nybatis.core.executor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.CommandLineException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.worker.WorkerReadLine;

/**
 * OS command line executor
 *
 * @author nayasis@gmail.com
 */
public class CommandExecutor {

	private Process        process         = null;
	private BufferedWriter processPipe     = null;

	private ProcessOutputThread outputThread    = null;
	private ProcessOutputThread errorThread     = null;

	/**
	 * Command 명령어를 수행한다.
	 *
	 * @param commandLine 명령어
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine ) {
		return run( commandLine, null, null );
	}

	/**
	 * Command 명령어를 수행한다.
	 *
	 * @param commandLine 명령어
	 * @param worker      출력결과를 기반으로 처리를 수행할 작업자
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, WorkerReadLine worker ) {
		return run( commandLine, null, worker );
	}

	/**
	 * 명령어를 수행한다.
	 *
	 * @param commandLine	명령어
	 * @param outputMessage	프로세스에서 출력한 메세지를 담을 객체
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, StringBuffer outputMessage ) {
	    return run( commandLine, outputMessage, null );
	}

	/**
	 * 명령어를 수행한다.
	 *
	 * @param commandLine	명령어
	 * @param outputMessage	프로세스에서 출력한 메세지를 담을 객체
	 * @param worker        출력결과를 기반으로 처리를 수행할 작업자
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, StringBuffer outputMessage, WorkerReadLine worker ) {

		Command command = new Command();

		command.set( commandLine );
		command.setOutputPipe( outputMessage );
		command.setWorker( worker );

		return run( command );

	}

	/**
	 * 명령어를 수행한다.
	 *
	 * @param command 커맨드
	 * @return self instance
	 */
	public CommandExecutor run( Command command ) {

		if( command == null ) return this;

		if( isAlive() ) throw new CommandLineException( "pre-command is still running" );

		if( ! command.hasCommand() ) throw new CommandLineException( "there is no command to execute" );

		NLogger.trace( "Command Line : {}", command );

		try {

			ProcessBuilder builder = new ProcessBuilder( command.get() );

			if( command.getWorkingDirectory() != null ) {
				builder.directory( command.getWorkingDirectory() );
			}

			process = builder.start();

			errorThread = new ProcessOutputThread( process.getErrorStream(), command.getErrorPipe(), command.getWorker() );
			errorThread.setDaemon( true );

			outputThread = new ProcessOutputThread( process.getInputStream(), command.getOutputPipe(), command.getWorker() );
			outputThread.setDaemon( true );

			errorThread.start();
			outputThread.start();

			processPipe = new BufferedWriter( new OutputStreamWriter( process.getOutputStream(), Const.platform.osCharset ) );

			return this;

		} catch ( IOException e ) {

			throw new CommandLineException( e, "It happens ERROR while executing command ({})", command );

		}

	}

	/**
	 * 실행중인지 여부를 확인한다.
	 *
	 * @return 실행중인지 여부
	 */
	public boolean isAlive() {

		if( process      != null && process.isAlive()      ) return true;

		if( outputThread != null && outputThread.isAlive() ) return true;

		return errorThread != null && errorThread.isAlive();

	}

	/**
	 * get process termination code
	 *
	 * @return the exit value of the subprocess represented by this
     *         {@code Process} object.  By convention, the value
     *         {@code 0} indicates normal termination.
     * @throws IllegalThreadStateException if the subprocess represented
     *         by this {@code Process} object has not yet terminated
	 */
	public int getExitValue() {

		if( process == null ) throw new IllegalThreadStateException( "process is null." );

		return process.exitValue();

	}

	/**
	 * wait until process is closed.
	 *
	 * @param timeout	max wait time (mili-seconds)
	 * @return	process termination code ( 0 : success )
	 */
	public int waitFor( Long timeout ) {

		if( ! isAlive() ) return 0;

		int exitValue = 0;

		try {

			exitValue = process.waitFor();

		} catch ( InterruptedException e ) {
			process.destroy();
		} finally {
			Thread.interrupted();
		}

		waitThread( outputThread, timeout );
		waitThread( errorThread, timeout );

		destroy();

		return exitValue;

	}

	private void waitThread( ProcessOutputThread thread, Long timeOut ) {

		if( thread == null || ! thread.isAlive() ) return;

		try {

			if( timeOut == null ) {
				thread.join();
			} else {
				thread.join( timeOut );
			}

        } catch( InterruptedException e ) {
			thread.interrupt();
        }

	}

	/**
	 * wait until process is closed.
	 *
	 * @return	process termination code ( 0 : success )
	 */
	public int waitFor() {
		return waitFor( null );
	}

	/**
	 * terminate process forcibly.
	 */
	public void destroy() {

		if( process != null ) {
			process.destroyForcibly();
			process = null;
		}

		destroyThread( outputThread );
		destroyThread( errorThread );

		if( processPipe != null ) {

			try {
				processPipe.close();
			} catch( IOException e ) {
				NLogger.error( e );
			} finally {
				processPipe = null;
			}

		}

	}

	private void destroyThread( ProcessOutputThread thread ) {
		if( thread == null ) return;
		thread.interrupt();
	}

	/**
	 * 프로세스에 명령어를 전송한다.
	 *
	 * @param command 전송할 명령어
	 * @return self instance
	 */
	public CommandExecutor sendCommand( String command ) {

		if( processPipe == null ) return this;

		NLogger.debug( "command to send : {}", command );

		try {

			processPipe.write( command );
			processPipe.write( "\n" );
	        processPipe.flush();


		} catch( IOException e ) {
        	NLogger.error( e );
        }

		return this;

	}

}
