package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement;

import java.math.BigDecimal;
import java.math.MathContext;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.ConstantNode;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

public class NumericValueNode extends ConstantNode {

	private BigDecimal value;

	public NumericValueNode() {
		this( "" );
	}

	public NumericValueNode( String number ) {

		if( StringUtil.isEmpty( number ) ) {
			value = BigDecimal.ZERO;

		} else {
			value = new BigDecimal( number );
		}

	}

	public NumericValueNode( Number number ) {
		value = new BigDecimal( number.toString() );
	}

	public BigDecimal getValue() {
		return value;
	}

	public String toString() {
		return value.toString();
	}

	private int compare( NumericValueNode node ) {
		return value.compareTo( node.getValue() );
	}

	private int compare( StringValueNode node ) throws SqlParseException {

		if( toString().equals( node.getValue() ) ) return 0;

		if( ! Validator.isNumeric( node.getValue() ) ) throw new SqlParseException();

		return compare( new NumericValueNode( node.getValue()) );

	}

	public int compare( Node node ) throws SqlParseException {

		if( node instanceof NumericValueNode ) {
			return compare( node.toNumericValueNode() );

		} else if( node instanceof StringValueNode ) {
			return compare( node.toStringValueNode() );

		} else {
			throw new SqlParseException();
		}

	}

	public boolean equals( Node node ) {

		if( node instanceof NullNode ) {
			return false;

		} else if( node instanceof EmptyNode ) {
			return value.equals( BigDecimal.ZERO );

		} else if( node instanceof NumericValueNode ) {
			return compare( node.toNumericValueNode() ) == 0;

		} else if( node instanceof StringValueNode ) {
			return toString().equals( node.toStringValueNode().getValue() );
		}

		return false;

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

	public NumericValueNode plus( NumericValueNode value ) {

		this.value = this.value.add( value.value );

		return this;

	}

	public NumericValueNode minus( NumericValueNode value ) {

		this.value = this.value.subtract( value.value );

		return this;

	}

	public NumericValueNode multiply( NumericValueNode value ) {

		this.value = this.value.multiply( value.value );

		return this;

	}

	public NumericValueNode divide( NumericValueNode value ) throws SqlParseException {

		if( value.value.compareTo( BigDecimal.ZERO ) == 0 ) {
			throw new SqlParseException( "It is not possible to divide [{}] / [{}].", this, value );
		}

		this.value = this.value.divide( value.value, MathContext.DECIMAL128 );

		return this;

	}

	public NumericValueNode remain( NumericValueNode value ) {

		this.value = this.value.remainder( value.value );

		return this;

	}

	public NumericValueNode square( NumericValueNode value ) {

		this.value = this.value.pow( value.value.intValue() );

		return this;

	}

}
