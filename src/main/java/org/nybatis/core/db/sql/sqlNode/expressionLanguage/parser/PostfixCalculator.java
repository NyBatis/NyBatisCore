package org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.brace.implement.CloseBraceNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.brace.implement.OpenBraceNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.OperatorNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.variable.VariableNode;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.util.StringUtil;

public class PostfixCalculator {

	public List<Node> getPostfix( List<Node> infixNodes ) {
		
		List<Node> result = new ArrayList<>();
		
		Stack<Node> stack = new Stack<>();
		
		for( Node node : infixNodes ) {

			if( node instanceof ConstantNode || node instanceof VariableNode ) {

				result.add( node );
				
			} else if( node instanceof OpenBraceNode ) {
				stack.push( node );

			} else if( node instanceof CloseBraceNode ) {

				while( ! (stack.peek() instanceof OpenBraceNode) ) {
					result.add( stack.pop() );
				}
				
				stack.pop(); // remove OpenBraceNode
				
			} else {

				while( ! stack.isEmpty() && ! (stack.peek() instanceof OpenBraceNode) && (node.getPriority() <= stack.peek().getPriority()) ) {

					result.add( stack.pop() );
					
				}
				
				stack.push( node );
				
			}

		}
		
		while( ! stack.isEmpty() ) {
			result.add( stack.pop() );
		}
		
		return result;
		
	}

	public Node calculate( List<Node> postfixNodes, Map<?,?> param ) throws SqlParseException {
		return calculate( postfixNodes, param, false );
	}
	
	public Node calculate( List<Node> postfixNodes, Map<?,?> param, boolean testMode ) throws SqlParseException {

		Stack<Node> stack = new Stack<>();
		
		if( param == null ) param = new HashMap<String, String>();
		
		for( Node node : postfixNodes ) {

			if( node instanceof VariableNode ) {
				stack.push( testMode ? node.toVariableNode().getTestNode() : node.toVariableNode().getNode( param ) );

			} else if( node instanceof ConstantNode ) {
				stack.push( node );
			
			} else {

				Node postNode = stack.pop();
				Node preNode  = stack.pop();

				OperatorNode operNode = node.toOperatorNode();
				
				Node result = operNode.calculate( preNode, postNode );
				
//				NLogger.debug( "{} {} {} = {}", preNode, operNode, postNode, result );
				
				stack.push( result );
				
			}
			
		}

		if( stack.size() > 1 ) {
			throw new SqlParseException( "It is not a valid expression. [{}]", StringUtil.join( postfixNodes, " " ) );
		}
		
		return stack.peek();
		
	}
	
}