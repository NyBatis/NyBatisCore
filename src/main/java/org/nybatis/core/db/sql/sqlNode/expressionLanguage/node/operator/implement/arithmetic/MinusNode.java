package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.ArithmeticNode;

public class MinusNode extends ArithmeticNode {

	public String toString() {
		return  "-";
	}

	@Override
	public NumericValueNode operate( Node pre, Node post ) {
		return getNumericValueNode( pre ).minus( getNumericValueNode( post ) );
	}

}
