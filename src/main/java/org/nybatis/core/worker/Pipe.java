package org.nybatis.core.worker;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nybatis.core.util.ClassUtil;

public class Pipe<T> {

	private static final String KEY = "_PIPE_";

	private Map<String, T> pipe = new HashMap<>();

    public Pipe() {}

    public Pipe( T value ) {
    	set( value );
    }

    public Pipe( Class<T> klass ) {
    	init( klass );
    }

	public T get() {
		return pipe.get( KEY );
	}

	public T set( T value ) {
		return pipe.put( KEY, value );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
    private void init( Class<?> klass ) {

		if( klass == null ) {
			set( (T) new Object() );
		} else if( klass == Short.class ) {
			set( (T) new Short(Short.MIN_VALUE) );
		} else if( klass == Byte.class ) {
			set( (T) new Byte(Byte.MIN_VALUE) );
		} else if( klass == Integer.class ) {
			set( (T) new Integer(0) );
		} else if( klass == Long.class ) {
			set( (T) new Long(0) );
		} else if( klass == Float.class ) {
			set( (T) new Float(0) );
		} else if( klass == Double.class ) {
			set( (T) new Double(0) );
		} else if( klass == BigInteger.class ) {
			set( (T) BigInteger.ZERO );
		} else if( klass == BigDecimal.class ) {
			set( (T) BigDecimal.ZERO );
		} else if( klass == String.class ) {
			set( (T) "" );
		} else if( klass == List.class ) {
			set( (T) new ArrayList() );
		} else if( klass == Object.class ) {
			set( (T) new Object() );
		} else {
			set( (T) new ClassUtil().createInstance( klass ) );
		}

	}

	public String toString() {
		return String.valueOf( pipe.get(KEY) );
	}

}
