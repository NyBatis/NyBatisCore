package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;

public class BooleanValueNode extends ConstantNode {

	private boolean value;
	
	public BooleanValueNode( boolean value ) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public String toString() {
		return String.valueOf( value );
	}

	@Override
    public boolean equals( Node node ) {

		if( node instanceof BooleanValueNode ) {
			return value == ((BooleanValueNode) node).getValue();
		}
		
	    return false;

	}
	
}
