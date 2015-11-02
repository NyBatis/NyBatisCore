package org.nybatis.core.validation;

import org.nybatis.core.message.Message;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Assertion checker to assists in validation arguments
 */
public class Assertion {

	public static void isNull( Object object, Object... errorMessage ) throws IllegalArgumentException {
		if( object != null ) throwException( errorMessage );
	}

	public static void isNotNull( Object object, Object... errorMessage ) throws IllegalArgumentException {
		if( object == null ) throwException( errorMessage );
	}

	public static void isExists( File file, Object... errorMessage ) throws IllegalArgumentException {
		if( file == null || ! file.exists() ) throwException( errorMessage );
	}

	public static void isNotExists( File file, Object... errorMessage ) throws IllegalArgumentException {
		if( file != null && file.exists() ) throwException( errorMessage );
	}

	public static void isTrue( boolean result, Object... errorMessage ) throws IllegalArgumentException {
		if( ! result ) throwException( errorMessage );
	}

	public static void isNotTrue( boolean result, Object... errorMessage ) throws IllegalArgumentException {
		if( result ) throwException( errorMessage );
	}

	public static void isTrue( BiFunction logic, Object... errorMessage ) throws IllegalArgumentException {

		try {
			if( ! logic.run() ) throwException( errorMessage );
		} catch( Throwable t ) {
			throwException( t, errorMessage );
		}

	}

	public static void isNotTrue( BiFunction logic, Object... errorMessage ) throws IllegalArgumentException {

		try {
			if( logic.run() ) throwException( errorMessage );
		} catch( Throwable t ) {
			throwException( t, errorMessage );
		}

	}

	public static void isEmpty( Collection<?> collection, Object... errorMessage ) throws IllegalArgumentException {
		if( ! Validator.isEmpty(collection) ) throwException( errorMessage );
	}

	public static void isNotEmpty( Collection<?> collection, Object... errorMessage ) throws IllegalArgumentException {
		if( Validator.isEmpty(collection) ) throwException( errorMessage );
	}

	public static void isEmpty( Map<?, ?> map, Object... errorMessage ) throws IllegalArgumentException {
		if( ! Validator.isEmpty(map) ) throwException( errorMessage );
	}

	public static void isNotEmpty( Map<?, ?> map, Object... errorMessage ) throws IllegalArgumentException {
		if( Validator.isEmpty(map) ) throwException( errorMessage );
	}

	public static void isEmpty( Object[] array, Object... errorMessage ) throws IllegalArgumentException {
		if( ! Validator.isEmpty(array) ) throwException( errorMessage );
	}

	public static void isNotEmpty( Object[] array, Object... errorMessage ) throws IllegalArgumentException {
		if( Validator.isEmpty(array) ) throwException( errorMessage );
	}

	public static void isEmpty( String string, Object... errorMessage ) throws IllegalArgumentException {
		if( ! Validator.isEmpty(string) ) throwException( errorMessage );
	}

	public static void isNotEmpty( String string, Object... errorMessage ) throws IllegalArgumentException {
		if( Validator.isEmpty(string) ) throwException( errorMessage );
	}


	private static void throwException( Object... errorMessage  ) throws IllegalArgumentException {

		if( errorMessage == null || errorMessage.length == 0 ) {
			throw new IllegalArgumentException();

		} else {

			if( errorMessage[0] instanceof Throwable ) {

				if( errorMessage[0] instanceof RuntimeException ) {
					throw (RuntimeException) errorMessage[0];
				} else {
					throw new RuntimeException( (Throwable) errorMessage[0] );
				}

			}

			if( errorMessage.length == 1 ) {

				throw new IllegalArgumentException( Message.get( errorMessage[0] ) );

			} else {

				int size = errorMessage.length - 1;

				Object[] param = new Object[ size ];

				System.arraycopy( errorMessage, 1, param, 0, size );

				throw new IllegalArgumentException( Message.get( errorMessage[0], param ) );

			}
		}

	}

	private static void throwException( Throwable throwable, Object... errorMessage  ) throws IllegalArgumentException {

		if( errorMessage == null || errorMessage.length == 0 ) {
			throw new IllegalArgumentException( throwable );

		} if( errorMessage.length == 1 ) {
			throw new IllegalArgumentException( Message.get(errorMessage[0]), throwable );

		} else {

			int size = errorMessage.length - 1;

			Object[] param = new Object[ size ];

			System.arraycopy( errorMessage, 1, param, 0, size );

			throw new IllegalArgumentException( Message.get( errorMessage[0], param ), throwable );

		}

	}
}
