package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.EmptyNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NullNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.OperatorNode;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.validation.Validator;

public abstract class ArithmeticNode extends OperatorNode {

	public int getPriority() {
		return 50;
	}

	public abstract NumericValueNode operate( Node pre, Node post );

	public ConstantNode calculate( Node pre, Node post ) throws SqlParseException {

		if( pre.isOperator() || post.isOperator() ) {
			throw new SqlParseException( "It is not possible to operate [{}] {} [{}]", pre, toString(), post );
		}

		return operate( pre, post );

	}

	protected NumericValueNode getNumericValueNode( Node node ) throws SqlParseException {

		if( node instanceof NumericValueNode ) {
			return (NumericValueNode) node;

		} else if( node instanceof StringValueNode ) {
			StringValueNode n = (StringValueNode) node;
			if( Validator.isNumeric( n.getValue() ) ) return new NumericValueNode( n.getValue() );

		} else if( node instanceof NullNode || node instanceof EmptyNode ) {
			return new NumericValueNode();
		}

		throw new SqlParseException( "Node[{}] can not be converted to numeric value.", node );

	}

}
