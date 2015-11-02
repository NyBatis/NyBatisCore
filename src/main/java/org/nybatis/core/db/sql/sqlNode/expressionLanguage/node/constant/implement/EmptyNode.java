package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement;

import java.math.BigDecimal;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;

public class EmptyNode extends ConstantNode {

	public String toString() {
		return  "empty";
	}

	@Override
    public boolean equals( Node node ) {

		if( node instanceof NullNode || node instanceof EmptyNode ) {
			return true;

		} else if( node instanceof StringValueNode ) {
			return "".equals( ((StringValueNode) node).getValue() );

		} else if( node instanceof NumericValueNode ) {
			return node.toNumericValueNode().getValue().equals( BigDecimal.ZERO );

		}

	    return false;

	}

}
