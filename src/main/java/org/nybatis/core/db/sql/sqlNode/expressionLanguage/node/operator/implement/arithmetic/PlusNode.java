package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.arithmetic;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.ArithmeticNode;
import org.nybatis.core.exception.unchecked.SqlParseException;

public class PlusNode extends ArithmeticNode {

	public String toString() {
		return  "+";
	}

	@Override
    public ConstantNode calculate( Node pre, Node post ) throws SqlParseException {

		if( pre.isString() || post.isString() ) {
			return new StringValueNode( pre.toString() + post.toString() );
		}

		return super.calculate( pre, post );

	}

	@Override
	public NumericValueNode operate( Node pre, Node post ) {
		return getNumericValueNode( pre ).plus( getNumericValueNode( post ) );
	}
	
}
