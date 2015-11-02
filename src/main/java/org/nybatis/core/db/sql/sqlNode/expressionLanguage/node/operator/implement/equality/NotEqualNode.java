package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.EqualityNode;
import org.nybatis.core.exception.unchecked.SqlParseException;

public class NotEqualNode extends EqualityNode {

	public String toString() {
		return  "!=";
	}

	public BooleanValueNode calculate( Node pre, Node post ) throws SqlParseException {
		return new BooleanValueNode ( ! new EqualNode().calculate( pre, post ).getValue() );
	}
}
