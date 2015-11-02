package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.ArithmeticNode;

public class SquareNode extends ArithmeticNode {

	public String toString() {
		return  "^";
	}

	public int getPriority() {
		return super.getPriority() + 2;
	}

	@Override
	public NumericValueNode operate( Node pre, Node post ) {
		return getNumericValueNode( pre ).square( getNumericValueNode( post ) );
	}

}
