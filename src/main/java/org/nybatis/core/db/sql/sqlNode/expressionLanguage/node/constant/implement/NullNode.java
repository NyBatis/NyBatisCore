package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;

public class NullNode extends ConstantNode {

	public String toString() {
		return  "null";
	}

	@Override
    public boolean equals( Node node ) {

		return node instanceof NullNode || node instanceof EmptyNode;
		
    }
	
}
