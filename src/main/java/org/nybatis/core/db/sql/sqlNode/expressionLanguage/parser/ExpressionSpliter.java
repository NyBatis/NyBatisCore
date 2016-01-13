package org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser;

import java.util.ArrayList;
import java.util.List;

import org.nybatis.core.validation.Validator;

public class ExpressionSpliter {

	private enum ModeStringReading { OFF, ON }
	private enum ModeVariableReading { OFF, ON }

	private StringBuilder buffer;
	
	public List<String> split( String expression ) {

		ModeStringReading   modeStringReading   = ModeStringReading.OFF;
		ModeVariableReading modeVariableReading = ModeVariableReading.OFF;

		List<String> result = new ArrayList<>();
		
		buffer = new StringBuilder();
		
		char currC, nextC;

		for( int i = 0, iCnt = expression.length(); i < iCnt; i++ ) {

			currC = expression.charAt( i );
			
			try {
				nextC = expression.charAt( i + 1 );
			} catch( StringIndexOutOfBoundsException e ) {
				nextC = ' ';
			}
			

			// "***\\'***" -> "***'***"
			if( currC == '\\' && nextC == '\'' ) {
				buffer.append( '\'' );
				i ++;
				continue;
			}

			if( modeStringReading == ModeStringReading.ON ) {

				buffer.append( currC );
				
				if( currC == '\'' ) {

					modeStringReading = ModeStringReading.OFF;
					
					writeBuffer( result );
					
				}

			} else if( modeVariableReading == ModeVariableReading.ON ) {

				buffer.append( currC );

				if( currC == '}' ) {
					
					modeVariableReading = ModeVariableReading.OFF;

					writeBuffer( result );
					
				}				

			} else {

				switch ( currC ) {

					// String ( '...' )
					case '\'' :
						modeStringReading = ModeStringReading.ON;
						
						writeBuffer( result );
						buffer.append( currC );
						continue;

					// Variable ( #{...} )
					case '#' :

						if( nextC == '{' ) {
							modeVariableReading = ModeVariableReading.ON;

							writeBuffer( result );
							buffer.append( "#{" );
							i ++;
							continue;
							
						}
						break;

					// Arithmetic Operation ( +, -, *, /, %, ^ )
					case '+' :
						writeBuffer( result );
						result.add( String.valueOf( currC ) );
						continue;
					case '-' :
						writeBuffer( result );
						result.add( String.valueOf( currC ) );
						continue;
					case '*' :
						writeBuffer( result );
						result.add( String.valueOf( currC ) );
						continue;
					case '/' :
						writeBuffer( result );
						result.add( String.valueOf( currC ) );
						continue;
					case '%' :
						writeBuffer( result );
						result.add( String.valueOf( currC ) );
						continue;
					case '^' :
						writeBuffer( result );
						result.add( String.valueOf( currC ) );
						continue;
						
					// < or <=
					case '<' :

						writeBuffer( result );
						
						if( nextC == '=' ) {
							result.add( "<=" );
							i ++;
							
						} else {
							result.add( String.valueOf( currC ) );
						}
						
						continue;

					// > or >=
					case '>' :

						writeBuffer( result );
						
						if( nextC == '=' ) {
							result.add( ">=" );
							i ++;
							
						} else {
							result.add( String.valueOf( currC ) );
						}
						
						continue;
					
					// = or ==
					case '=' :

						if( nextC == '=' ) i ++;

						writeBuffer( result );
						
						result.add( String.valueOf( currC ) );
						
						continue;
					
					// !=
					case '!' :

						if( nextC == '=' ) {
							i ++;
							writeBuffer( result );
							result.add( "!=" );
							continue;
						}
						
						break;

					// &&
					case '&' :

						if( nextC == '&' ) i++;

						writeBuffer( result );
						result.add( "&&" );

						continue;

					// ||
					case '|' :
						
						if( nextC == '|' ) {
							i ++;
							writeBuffer( result );
							result.add( "||" );
							continue;
						}

						break;

					// Brace
					case '(' : case ')' :

						writeBuffer( result );
						
						result.add( String.valueOf(currC) );
						
						continue;

					// Delimeter
					case ' ' :
						
						writeBuffer( result );
						continue;
						
				}

				buffer.append( currC );

			}

		}

		writeBuffer( result );
		
		return setNumberToPositiveOrNegative( result );
		
	}

	private void writeBuffer( List<String> sequence ) {
		
		if( buffer.length() > 0 ) {
			sequence.add( buffer.toString() );
		}

		this.buffer = new StringBuilder();
		
	}
	
	private List<String> setNumberToPositiveOrNegative( List<String> expression ) {

		List<String> result = new ArrayList<>();
		
		String curr, next;
		
		for( int i = 0, iCnt = expression.size(); i < iCnt; i++ ) {

			curr = expression.get( i );
			
			try {
				next = expression.get( i + 1 );

			} catch( IndexOutOfBoundsException e ) {
				next = "EOF";
			}
			
			if( "-".equals(curr) && Validator.isNumeric(next) ) {

				if( i == 0 || ! isOperation(expression, i) ) {
					result.add( curr + next );
					i++;
					
				} else {
					result.add( curr );
				}

			} else if( "+".equals(curr) && Validator.isNumeric(next) ) {

				if( i == 0 || ! isOperation(expression, i) ) {
					result.add( next );
					i++;
					
				} else {
					result.add( curr );
				}

			} else {
				result.add( curr );
			}

		}
		
		return result;
		
	}

	private boolean isOperation( List<String> expression, int index ) {

		String prev = expression.get( index - 1 );

		if( Validator.isNumeric(prev) ) return true;

		return ( prev.startsWith( "#{" ) && prev.endsWith( "}" ) );

	}

}
