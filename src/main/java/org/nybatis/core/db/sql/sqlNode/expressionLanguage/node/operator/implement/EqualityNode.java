package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.OperatorNode;

public abstract class EqualityNode extends OperatorNode {

	public int getPriority() {
		return 40;
	}

	protected int getBitFlag( Node pre, Node post ) {

		int flag1st = ( pre  instanceof NumericValueNode ) ? 0x10 : ( pre  instanceof StringValueNode ) ? 0x20 : 0x00;
		int flag2nd = ( post instanceof NumericValueNode ) ? 0x01 : ( post instanceof StringValueNode ) ? 0x02 : 0x00;

		return flag1st | flag2nd;
		
	}

}
