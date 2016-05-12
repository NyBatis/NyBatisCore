package org.nybatis.core.executor.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * OS 명령어(Command Line)를 배열로 변환하는 유틸리티 클래스
 *
 * @author nayasis
 *
 */
public class CommandParser {
	
	List<String>  commandList = null;
	StringBuilder command     = null;

	public List<String> parse( String commandLine ) {

		if( commandLine == null || commandLine.length() == 0 ) return new ArrayList<String>();
		
		commandList = new ArrayList<>();
		command     = new StringBuilder();
		
		boolean quotationMode = false;
		
		for( int i = 0, iCnt = commandLine.length(); i < iCnt; i++ ) {
			
			char c = commandLine.charAt( i );
			
			if( quotationMode == true ) {

				if( c == '"' ) {

					quotationMode = false;

					command.append( c );
					addCommandList( command );
					continue;
					
				}
					
			} else {
				
				if( c == '"' ) {
					
					quotationMode = true;

					command.append( c );
					continue;
					
				} else if ( c == ' ' || c == '\t' || c == '\r' || c == '\n'  ) {
					addCommandList( command );
					continue;
				}
				
			}
			
			command.append( c );
			
		}
		
		addCommandList( command );
		
		return commandList;
		
	}
	
	private void addCommandList( StringBuilder command ) {
		
		if( command == null ) return;
		
		String appendCommand = command.toString().trim();
		
		if( command.length() == 0 ) return;
		
		this.commandList.add( appendCommand );
		
		this.command = new StringBuilder();
		
	}
	
}
