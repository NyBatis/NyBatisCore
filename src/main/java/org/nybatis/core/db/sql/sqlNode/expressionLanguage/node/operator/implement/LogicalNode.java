package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.OperatorNode;

public abstract class LogicalNode extends OperatorNode {

	public int getPriority() {
		return 30;
	}

}
