package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.equality;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.EmptyNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NullNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.operator.implement.EqualityNode;
import org.nybatis.core.exception.unchecked.SqlParseException;

public class EqualNode extends EqualityNode {

	public String toString() {
		return  "=";
	}

    public BooleanValueNode calculate( Node preNode, Node postNode ) throws SqlParseException {

    	boolean result = false;

    	if( preNode instanceof NullNode ) {
    		result = preNode.toNullNode().equals( postNode );

    	} else if( preNode instanceof EmptyNode ) {
    		result = preNode.toEmptyNode().equals( postNode );

    	} else if( preNode instanceof StringValueNode ) {
    		result = preNode.toStringValueNode().equals( postNode );

    	} else if( preNode instanceof NumericValueNode ) {
    		result = preNode.toNumericValueNode().equals( postNode );

    	} else if( preNode instanceof BooleanValueNode ) {
    		result = preNode.toBooleanValueNode().equals( postNode );

    	} else {
    		throw new SqlParseException();
    	}

		return new BooleanValueNode( result );

    }

}
