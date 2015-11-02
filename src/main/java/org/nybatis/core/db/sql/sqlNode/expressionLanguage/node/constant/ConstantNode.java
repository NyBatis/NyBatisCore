package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;

public abstract class ConstantNode extends Node {

	public int getPriority() {
		return 99;
	}

	public abstract boolean equals( Node node );
	
}
