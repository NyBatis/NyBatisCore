package org.nybatis.core.db.testExpression;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser.ExpressionParser;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser.ExpressionSpliter;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser.PostfixCalculator;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.validation.Validator;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TestExpressionParserTest {

	@Test
	public void compareValueTest() {
		
//		NLogger.debug( "1".compareTo( "1" ) );
//		NLogger.debug( "1".compareTo( "1.0" ) );
//		NLogger.debug( "1.0".compareTo( "1.00"  ) );
//		NLogger.debug( "1.0".compareTo( "1.1"  ) );
//		NLogger.debug( "A".compareTo( "B"  ) );
//		
//		NLogger.debug( "1.1".compareTo( "1.0"  ) );
//		NLogger.debug( "1.1".compareTo( "1.01"  ) );
		
		NLogger.debug( 1 <= 1.0 );
		
		NLogger.debug( (int) '1' );
		NLogger.debug( (int) '3' );
		
	}
	
	@Test
	public void numberValueTest() {

		NLogger.debug( "numberValue : {}", new NumericValueNode( "92" ) );
		NLogger.debug( "numberValue : {}", new NumericValueNode( "92.0" ) );
		
		NLogger.debug( "greaterEqual : {}", new NumericValueNode( "92.0" ).isGreaterEqual( new NumericValueNode( "91" ) ) );
		
	}
	
	@Test
	public void printChar() {

		String value = "\"\' '\\@\\'";
		
		for( char c : value.toCharArray() ) {
			NLogger.debug( "char : [{}], code : [{}]", c, (int) c );
		}
		
	}

	@Test
	public void printChar02() {
		
		char c = (char) 65536; 
		
		System.out.printf( "char : [%s], code : [%d]\n", c, (int) c );
		
	}
	
	@Test
	public void expressionParseTest() {

		for( String expression : testExpressionList ) {

			NLogger.debug( "\n---------------------" );
			
			parseTestExpression( expression );
		}
		
	}
	
	@Test
	public void expressionValidationTest() {
		
		String t = "'ABCDEF'";
		
		NLogger.debug( "isString : {}",  Validator.isMatched( "++", "[+|\\-|*|/|%|\\^]" ) );
		NLogger.debug( "isString : {}",  t.substring( 1, t.length() ) );
	}

	private void parseTestExpression( String expression ) {

		ExpressionSpliter spliter = new ExpressionSpliter();
		ExpressionParser  parser  = new ExpressionParser();

		try {
			
			NLogger.debug( "Expss : {}", expression );

			List<String> rawNode = spliter.split( expression );
			NLogger.debug( "Split : {}", rawNode );
			
			List<Node> parse = parser.parse( expression );
			NLogger.debug( "Parse : {}", parse );
			
		} catch( SqlParseException e ) {
			NLogger.error( " - SqlTestExpressionParseException : {}", e.getMessage() );
		}
		
		
	}
	
	private String convertPostfixExpression( String expression ) {
		
		ExpressionParser  parser  = new ExpressionParser();
		PostfixCalculator calculator = new PostfixCalculator();
        
		try {

			List<Node> nodes = parser.parse( expression );

			NLogger.debug( "Parse  : {}", nodes );
			
			List<Node> postfix = calculator.getPostfix( nodes );
			
			NLogger.debug( "Posix  : {}", postfix );

			Node result = calculator.calculate( postfix, null, true );

			NLogger.debug( "Result : {}", result );
			
			return result.toString();

        } catch( SqlParseException e ) {
         	NLogger.error( " - SqlTestExpressionParseException : {}", e.getMessage() );
         	return null;
            
        }
		
	}
	
	@Test
	public void postfixTest() {

		int index = 0;

		for( String expression : testExpressionList ) {
			index++;
			NLogger.debug( "\n" +
					"------------------\n{}\n------------------\n{}\n---------------------\n", index, convertPostfixExpression( expression ) );
		}
		
	}

	private List<String> testExpressionList = Arrays.asList(
			"1+2",
			"1+2*3+4/2^7",
			"1+2+((3*2)^7)",
			"'123'=123 * 2",
			"'123'=123.45",
			"'123'==123.45",
			"'123'==123.45",
			"''ABC'=='ABC", // [\\'] Test
			"'\\'ABC'=='ABC'", // [\\'] Test
			"#{context} = 'ABC' && #{testApp} != 3 && #{value} > 12",
			"#{context} != 'ABC' && #{testApp} != 3|true && #{value}>12", // Error
			"#{context} != 'ABC' && #{testApp} != 3||true && #{value}>12",
			"'' is empty",
			"   #{context} like '%'   ",
			"   #{context} like 'ABC%'   ",
			"   #{context} like '_ABC%'   ",
			"   #{context} not like 'ABC%'   ",
			"   #{context} = null   ",
			"   #{context} = empty   ",
			"   #{context} != empty   ",
			"   #{context} ! = empty   ",
			"   #{context} is null   ",
			"   #{context} is empty   ",
			"   #{context} is empty or #{mutant} is not empty  ",
			"   #{context} = 1 +2 / 3",
			"   #{context} + 1 = 2",
			"   #{context} + - 1 = 2",
			"   #{context} < -4.2",
			"   - 4.2 + 1 > #{context}",
			"(1+2",
			"((1+2()",
			"(1+2))"
	);


	@Test
	public void test() {

		assertEquals( convertPostfixExpression( "(1 + 2)" ), "3" );
		assertEquals( convertPostfixExpression( "#{context} + 1 = 2" ), "false" );
		assertEquals( convertPostfixExpression( "#{context} + -1 = 2" ), "false" );

	}
}
