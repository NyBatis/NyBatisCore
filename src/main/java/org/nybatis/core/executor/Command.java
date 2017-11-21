package org.nybatis.core.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nybatis.core.executor.parser.CommandParser;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.worker.WorkerReadLine;

/**
 * 커맨드 클래스
 *
 * @author nayasis@gmail.com
 *
 */
public class Command {

	private List<String> command = new ArrayList<>();

	private StringBuffer outputPipe;

	private StringBuffer errorPipe;

	private WorkerReadLine worker;

	private File workingDirectory;

	public List<String> get() {
		return command;
	}

	public void add( String command ) {
		this.command.add( command );
	}

	public void addPath( String path ) {
		this.command.add( String.format("\"%s\"", path) );
	}

	public String toString() {
		return StringUtil.join( command, " " );
	}

	public void set( String command ) {
		this.command = new CommandParser().parse( command );
	}

	public void set( List<String> command ) {
		this.command = command;
	}

	public boolean hasCommand() {
		return command != null && command.size() > 0;
	}

	public StringBuffer getOutputPipe() {
		return outputPipe;
	}

	public void setOutputPipe( StringBuffer redirectPipe ) {
		this.outputPipe = redirectPipe;
	}

	public StringBuffer getErrorPipe() {
		return errorPipe;
	}

	public void setErrorPipe( StringBuffer redirectPipe ) {
		this.errorPipe = redirectPipe;
	}

	public WorkerReadLine getWorker() {
		return worker;
	}

	public void setWorker( WorkerReadLine worker ) {
		this.worker = worker;
	}

	public File getWorkingDirectory() {

		if( workingDirectory == null ) return null;

		if( workingDirectory.isDirectory() ) return workingDirectory;

		if( workingDirectory.isFile() ) return workingDirectory.getParentFile();

		return null;

	}

	public void setWorkingDirectory( File workingDirectory ) {
		this.workingDirectory = workingDirectory;
	}

	public void setWorkingDirectory( String workingDirectory ) {
		this.workingDirectory = new File( workingDirectory );
	}


}
