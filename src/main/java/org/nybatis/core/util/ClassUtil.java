package org.nybatis.core.util;

import java.lang.reflect.Type;

import org.nybatis.core.exception.unchecked.ClassCastException;


/**
 * Class Utility
 *
 * @author nayasis@gmail.com
 */
public class ClassUtil {

	/**
	 * Get class for name
	 *
	 * @param className
	 * @throws ClassNotFoundException
	 * @return Class for name
	 */
	public static Class<?> getClass( String className ) throws ClassNotFoundException {

		if( StringUtil.isEmpty(className) ) throw new ClassNotFoundException( String.format( "Expected class name is [%s].", className ) );

		className = className.replaceAll( " ", "" );

		int invalidCharacterIndex = className.indexOf( '<' );

		if( invalidCharacterIndex >= 0 ) {
			className = className.substring( 0, invalidCharacterIndex );
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try {
	        return classLoader.loadClass( className );
        } catch( ClassNotFoundException e ) {
        	throw new ClassNotFoundException( String.format( "Expected class name is [%s].", className ), e );
        }

	}

	/**
	 * Class의 Generic 정보로서 가져온 Type을 이용해 Class를 구한다.
	 *
	 * <pre>
	 *
	 * Type type = this.getClass().getGenericSuperclass();
	 *
	 * Class<?> klass = new ClassUtil().getClass( type );
	 * </pre>
	 *
	 * @param type
	 * @return Class by generic type
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass( Type type ) throws ClassNotFoundException {

		if( type == null ) return Object.class;

		String typeInfo = type.toString();

		int startIndex = typeInfo.indexOf( '<' );

		if( startIndex < 0 ) return Object.class;

		String typeClassName = typeInfo.substring( startIndex + 1, typeInfo.length() - 1 );

		startIndex = typeClassName.indexOf( '<' );

		if( startIndex >= 0 ) typeClassName = typeClassName.substring( 0, startIndex );

		return getClass( typeClassName );

	}

	public static Class<?> getClass( Object object ) {

	    if( object == null ) return null;

	    return object.getClass();

	}

	public static Class<?> getTopSuperClass( Class<?> klass ) {

	    Class<?> superclass = klass.getSuperclass();

	    if( superclass == null || superclass == Object.class ) return klass;

	    return getTopSuperClass( superclass );

	}

	public static Class<?> getTopSuperClass( Object object ) {

	    if( object == null ) return null;

	    return getTopSuperClass( object.getClass() );

	}

	public static <T> T getInstance( Class<T> klass ) {

		try {
			return klass.newInstance();
		} catch( InstantiationException | IllegalAccessException e ) {
        	throw new ClassCastException( e );
        }

	}

	@SuppressWarnings( "unchecked" )
    public static <T> T getInstance( Type type ) throws ClassNotFoundException {
		return (T) getInstance( getClass(type) );
	}

	/**
	 * Check if a class was extended or implemented by found class
	 *
	 * @param inspectClass  class to inspect
	 * @param foundClass	class to be extended in inspect class
	 * @return true if inspect class is extended of implemented by found class
	 */
	public static boolean isExtendedBy( Class<?> inspectClass, Class<?> foundClass ) {

		if( inspectClass == null || foundClass == null ) return false;

		if( inspectClass == foundClass ) return true;

		for( Class<?> klass : inspectClass.getInterfaces() ) {
			if( klass == foundClass ) return true;
		}

		Class<?> superclass = inspectClass.getSuperclass();

		if( superclass != Object.class ) {
			if( isExtendedBy( superclass, foundClass ) ) return true;
		}

		return false;

	}

	/**
	 * Check if an instnace was extended or implemented by found class
	 *
	 * @param inspectInstance  instance to inspect
	 * @param foundClass	   class to be extended in inspect instance
	 * @return true if inspect instance is extended of implemented by found class
	 */
	public static boolean isExtendedBy( Object inspectInstance, Class<?> foundClass ) {

		if( inspectInstance == null ) return false;

		return isExtendedBy( inspectInstance.getClass(), foundClass );

	}

}
