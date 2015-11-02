package org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.variable;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.BooleanValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NullNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.NumericValueNode;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.constant.implement.StringValueNode;
import org.nybatis.core.model.NDate;
import org.nybatis.core.validation.Validator;

public class VariableNode extends Node {

	private String key;

	public VariableNode( String key ) {
		this.key = key;
	}

	public Node getNode( Map<?, ?> param ) {

		if( param == null || ! param.containsKey(key) ) return new NullNode();

		Object value = param.get( key );

		if( value == null ) return new NullNode();

		Class<?> klass = value.getClass();

		if( klass == String.class ) {
			return new StringValueNode( value.toString() );

		} else if( klass == boolean.class || klass == Boolean.class ) {
			return new BooleanValueNode( (boolean) value );

		} else if( Validator.isNumericClass( value ) ) {
			return new NumericValueNode( (Number) value );

		} else if( value instanceof List ) {
			return new NumericValueNode( ((List<?>) value).size() );

		} else if( value.getClass().isArray() ) {
			return new NumericValueNode( Array.getLength(value) );

		} else if( klass == Date.class ) {
			return new StringValueNode( new NDate( (Date)value ).toString() );

		} else if( klass == Calendar.class ) {
			return new StringValueNode( new NDate( (Calendar)value ).toString() );

		} else if( klass == NDate.class ) {
			return new StringValueNode( value.toString() );
		}

		return new StringValueNode( value.toString() );

	}

	public Node getTestNode() {
		return new StringValueNode( "0" );
	}

	public String toString() {
		return  String.format( "#{%s}", key );
	}

	@Override
    public int getPriority() {
	    return 99;
    }

}
