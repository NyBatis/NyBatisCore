package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.brace;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;

public abstract class BraceNode extends Node {

	public int getPriority() {
		return 80;
	}
	
}
