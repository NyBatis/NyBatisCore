package org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.brace.implement.CloseBraceNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.brace.implement.OpenBraceNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.EmptyNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NullNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.DivideNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.MinusNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.MultipleNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.PlusNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.RemainderNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic.SquareNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.EqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.GreaterEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.GreaterThanNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.LessEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.LessThanNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.LikeEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.NotEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality.NotLikeEqualNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.logical.AndNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.logical.OrNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.variable.VariableNode;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.validation.Validator;

public class ExpressionParser {

	public List<Node> parse( String expression ) throws SqlParseException {
		
		List<Node> result = new ArrayList<>();

		List<String> expressionNodes = new ExpressionSpliter().split( expression );
		
		String currNodeString, nextNodeString;
		
		for( int i = 0, iCnt = expressionNodes.size(); i < iCnt; i++ ) {

			currNodeString = expressionNodes.get( i );
			
			try {
				nextNodeString = expressionNodes.get( i + 1 );

			} catch( IndexOutOfBoundsException e ) {
				nextNodeString = "";
			}
			
			if( isStringValue(result, currNodeString) ) continue;
			if( isVariable(result, currNodeString) ) continue;
			if( isArithmeticOperation(result, currNodeString) ) continue;
			if( isInequalitySign(result, currNodeString) ) continue;
			if( isEquality(result, currNodeString) ) continue;
			if( isLogical(result, currNodeString) ) continue;
			if( isBrace(result, currNodeString) ) continue;
			
			int status = isKeyword( result, currNodeString, nextNodeString );
			
			if( status < 0 ) {
				
				if( isNumericValue(result, currNodeString) ) continue;
				
				throw new SqlParseException( getErrorMessage( "unknown keyword : ", expressionNodes, i ) );
				
			} else {
				i += status;
			}

			
		}

		checkBraceClosed( result, expressionNodes );
		
		return result;
		
	}
	
	private String getErrorMessage( String message, List<String> expression, int errorPosition ) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append( message );
		
		for( int i = 0, iCnt = expression.size(); i < iCnt; i++ ) {
			
			if( i == errorPosition ) {
				sb.append( " [" ).append( expression.get( i ) ).append( "] " );
				
			} else {
				sb.append(' ' ).append( expression.get( i ) );
			}
			
		}
		
		return sb.toString();
		
	}
	
	private boolean isStringValue( List<Node> result, String checkVal ) {

		if( ! Validator.isMatched( checkVal, "'.*?'" ) ) return false;
		
		result.add( new StringValueNode( checkVal.substring( 1, checkVal.length() - 1 ) ) );
		
		return true;
		
	}
	
	private boolean isNumericValue( List<Node> result, String checkVal ) {
		
		if( ! Validator.isNumeric( checkVal ) ) return false;
		
		result.add( new NumericValueNode( checkVal) );
		
		return true;
		
	}
	
	private boolean isVariable( List<Node> result, String checkVal ) {
		
		if( ! Validator.isMatched( checkVal, "#\\{.+?\\}" ) ) return false;
		
		result.add( new VariableNode( checkVal.substring( 2, checkVal.length() - 1 ) ) );
		
		return true;
		
	}

	private boolean isArithmeticOperation( List<Node> result, String checkVal ) {

		if( ! Validator.isMatched( checkVal, "[+|\\-|*|/|%|\\^]" ) ) return false;
		
		switch( checkVal ) {
			case "+" : result.add( new PlusNode() );      break;
			case "-" : result.add( new MinusNode() );     break;
			case "*" : result.add( new MultipleNode() );  break;
			case "/" : result.add( new DivideNode() );    break;
			case "%" : result.add( new RemainderNode() ); break;
			case "^" : result.add( new SquareNode() );    break;
		}
		
		return true;
		
	}
	
	private boolean isInequalitySign( List<Node> result, String checkVal ) {

		switch( checkVal ) {
			case ">"  : result.add( new GreaterThanNode() );  return true;
			case ">=" : result.add( new GreaterEqualNode() ); return true;
			case "<"  : result.add( new LessThanNode() );  return true;
			case "<=" : result.add( new LessEqualNode() ); return true;
		}
		
		return false;
		
	}
	
	private boolean isEquality( List<Node> result, String checkVal ) {

		switch( checkVal ) {
			case "="  : result.add( new EqualNode() );  return true;
			case "!=" : result.add( new NotEqualNode() ); return true;
		}
		
		return false;
		
	}
	
	private boolean isLogical( List<Node> result, String checkVal ) {
		
		switch( checkVal ) {
			case "&&" : result.add( new AndNode() );  return true;
			case "||" : result.add( new OrNode() );   return true;
		}
		
		return false;
		
	}

	private boolean isBrace( List<Node> result, String checkVal ) {
		
		switch( checkVal ) {
			case "(" : result.add( new OpenBraceNode()  );  return true;
			case ")" : result.add( new CloseBraceNode() );  return true;
		}
		
		return false;
		
	}
	
	private int isKeyword( List<Node> result, String currVal, String nextVal ) {

		currVal = currVal.toLowerCase();
		nextVal = nextVal.toLowerCase();
		
		switch( currVal ) {
			case "true"  : result.add( new BooleanValueNode(true)  );  return 0;
			case "false" : result.add( new BooleanValueNode(false) );  return 0;
			case "null"  : result.add( new NullNode() );               return 0;
			case "empty" : result.add( new EmptyNode() );              return 0;
			case "like"  : result.add( new LikeEqualNode() );          return 0;
			case "not"   :
				if( "like".equals( nextVal ) ) {
					result.add( new NotLikeEqualNode() );
					return 1;
				} else {
					return -1;
				}
			case "is"    :
				if( "not".equals( nextVal ) ) {
					result.add( new NotEqualNode() );
					return 1;
				} else {
					result.add( new EqualNode() );
					return 0;
				}
			case "and"   : result.add( new AndNode() );                return 0;
			case "or"    : result.add( new OrNode() );                 return 0;
			
			default : return -1;
				
		}
		
	}
	
	private void checkBraceClosed( List<Node> nodes, List<String> expression ) throws SqlParseException {

		Stack<Integer> braceIndex = new Stack<>();

		for( int i = 0, iCnt = nodes.size(); i < iCnt; i++ ) {

			Node node = nodes.get( i );

			if( node instanceof OpenBraceNode ) {

				braceIndex.push( i );

			} else if( node instanceof CloseBraceNode ) {

				try {

					braceIndex.pop();

				} catch( EmptyStackException e ) {
					throw new SqlParseException( getErrorMessage( "brace is not opened : ", expression, i ) );

				}

			}

		}

		if( braceIndex.size() > 0 ) {
			throw new SqlParseException( getErrorMessage( "brace is not closed : ", expression, braceIndex.peek() ) );
		}
		
	}
	
}
