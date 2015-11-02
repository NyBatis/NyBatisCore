package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.logical;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.LogicalNode;
import org.nybatis.core.exception.unchecked.SqlParseException;

public class OrNode extends LogicalNode {

	public String toString() {
		return  "||";
	}

	@Override
  public BooleanValueNode calculate( Node preNode, Node postNode ) throws SqlParseException {

		if( ! (preNode  instanceof BooleanValueNode) ) throw new SqlParseException( "Node[{}] is not boolean.", preNode  );
		if( ! (postNode instanceof BooleanValueNode) ) throw new SqlParseException( "Node[{}] is not boolean.", postNode );

		boolean value =  preNode.toBooleanValueNode().getValue() || postNode.toBooleanValueNode().getValue();
		
		return new BooleanValueNode( value );

  }

}
