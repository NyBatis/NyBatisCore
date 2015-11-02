package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;
import org.nybatis.core.exception.unchecked.SqlParseException;

public abstract class OperatorNode extends Node {

	public abstract ConstantNode calculate( Node preNode, Node postNode ) throws SqlParseException;
	
}
