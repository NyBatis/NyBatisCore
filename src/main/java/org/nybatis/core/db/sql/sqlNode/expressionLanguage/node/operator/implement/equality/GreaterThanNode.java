package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.EqualityNode;
import org.nybatis.core.exception.unchecked.SqlParseException;

public class GreaterThanNode extends EqualityNode {

	public String toString() {
		return  ">";
	}

	@Override
  	public BooleanValueNode calculate( Node pre, Node post ) throws SqlParseException {

		if( pre.isNull() || post.isNull() ) return new BooleanValueNode( false );

		switch( getBitFlag( pre, post ) ) {
			case 0x11 :
			case 0x12 :
				return new BooleanValueNode( pre.toNumericValueNode().isGreaterThan( post ) );
			case 0x21 :
			case 0x22 :
				return new BooleanValueNode( pre.toStringValueNode().isGreaterThan( post ) );
			case 0x10 :
			case 0x20 :
				throw new SqlParseException( "Node[{}] is not a string or number.", post );
			case 0x01 :
			case 0x02 :
				throw new SqlParseException( "Node[{}] is not a string or number.", pre );
			default   :
				throw new SqlParseException( "It is not possible to operate [{}] {} [{}]", pre, toString(), post );
		}

	}

}
