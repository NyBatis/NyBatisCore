package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.EqualityNode;
import org.nybatis.core.exception.unchecked.SqlParseException;

public class NotLikeEqualNode extends EqualityNode {

	public String toString() {
		return  "not like";
	}

	@Override
  public BooleanValueNode calculate( Node preNode, Node postNode ) throws SqlParseException {
	  return new BooleanValueNode( ! new LikeEqualNode().calculate( preNode, postNode ).getValue() );
	}

}
