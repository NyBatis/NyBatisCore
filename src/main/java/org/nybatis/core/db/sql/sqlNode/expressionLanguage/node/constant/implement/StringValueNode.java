package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.util.StringUtil;

public class StringValueNode extends ConstantNode {

	private String value;

	public StringValueNode( String value ) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return StringUtil.nvl( value, "null" );
	}

	@Override
  public boolean equals( Node node ) {

		if( node instanceof NullNode ) {
			return false;

		} else if( node instanceof EmptyNode ) {
			return "".equals( value );

		} else if( node instanceof StringValueNode ) {
			return value.equals( ((StringValueNode) node).getValue() );

		} else if( node instanceof NumericValueNode ) {
			return value.equals( node.toString() );
		}

		return false;

	}

	private int compare( Node node ) throws SqlParseException {

		if( node instanceof NumericValueNode ) {
			return value.compareTo( node.toNumericValueNode().toString() );

		} else if( node instanceof StringValueNode ) {
			return value.compareTo( node.toStringValueNode().getValue() );

		} else {
			throw new SqlParseException();
		}

	}

	public boolean isGreaterEqual( Node node ) {

    try {

    	int val= compare( node );

    	return val == 0 || val > 0;

    } catch( SqlParseException e ) {
    	return false;
    }

	}

	public boolean isGreaterThan( Node node ) {

    try {

    	int val= compare( node );

    	return val > 0;

    } catch( SqlParseException e ) {
    	return false;
    }

	}

	public boolean isLessEqual( Node node ) {

    try {

    	int val= compare( node );

    	return val == 0 || val < 0;

    } catch( SqlParseException e ) {
    	return false;
    }

	}

	public boolean isLessThan( Node node ) {

    try {

    	int val= compare( node );

    	return val < 0;

    } catch( SqlParseException e ) {
    	return false;
    }

	}

}