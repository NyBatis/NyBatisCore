package org.nybatis.core.db.sql.sqlNode.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser.ExpressionParser;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.parser.PostfixCalculator;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

public class IfSqlElement extends SqlElement {

	private String     testExpression = "";
	private List<Node> testCondition  = new ArrayList<>();

	public IfSqlElement( String testExpression ) throws SqlParseException {

		this.testExpression = StringUtil.trim( testExpression );

		if( StringUtil.isEmpty( testExpression ) ) {

			testCondition.add( new BooleanValueNode( true ) );

		} else {

			ExpressionParser parser = new ExpressionParser();

			List<Node> nodes = parser.parse( testExpression );

			if( nodes.size() == 0 ) {
				testCondition.add( new BooleanValueNode( true ) );

			} else {

				PostfixCalculator calculator = new PostfixCalculator();

				testCondition = calculator.getPostfix( nodes );

			}

		}

	}

	public IfSqlElement( String testExpression, List<Node> testCondition, List<SqlElement> children ) {
		this.testExpression = testExpression;
		this.testCondition  = testCondition;
		this.children       = children;
	}

	@Override
    public String toString( NMap param ) throws SqlParseException {
		return isTrue( param ) ? super.toString( param ) : "";
	}

	private void toString( StringBuilder buffer, SqlElement node, int depth ) {

		String tab = StringUtil.lpad( "", ' ', depth * 2 );

		buffer.append( String.format( "%s%s", tab, node.toString() ) );

	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append( String.format("[IF test='%s']\n", testExpression) );

		for( SqlElement node : children ) {
			toString( sb, node, 1 );
		}

		return sb.toString();

	}

    public boolean isTrue( Map param ) throws SqlParseException {

		PostfixCalculator calculator = new PostfixCalculator();

		Node result = calculator.calculate( testCondition, param );

//		NLogger.trace( "expression : {}, param : {}, result : {}", testExpression, param, result );

		if( result instanceof BooleanValueNode ) {
			return result.toBooleanValueNode().getValue();

		} else if( result instanceof NumericValueNode ) {
			return result.toNumericValueNode().isGreaterThan( new NumericValueNode( 0 ) );

		} else if( result instanceof StringValueNode ) {
			return result.toStringValueNode().getValue() != null;

		} else {
			return false;
		}

	}

	protected String getTestExpression() {
		return testExpression;
	}


	protected WhenFirstSqlElement toWhenFirstSqlElement() {
		return new WhenFirstSqlElement( testExpression, testCondition, children );
	}

	protected WhenSqlElement toWhenSqlElement() {
		return new WhenSqlElement( testExpression, testCondition, children );
	}

}
