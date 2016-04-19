package org.nybatis.core.util;

import java.io.InputStream;
import java.lang.reflect.Type;

import org.nybatis.core.exception.unchecked.ClassCastingException;


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

		ClassLoader classLoader = getClassLoader();

		try {
	        return classLoader.loadClass( className );
        } catch( ClassNotFoundException e ) {
        	throw new ClassNotFoundException( String.format( "Expected class name is [%s].", className ), e );
        }

	}

	/**
	 * Get class loader
	 *
	 * @return get class loader in current thread.
	 */
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * Get Class from Type like class's generic type.
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
		return ( object == null ) ? null : object.getClass();
	}

	public static Class<?> getTopSuperClass( Class<?> klass ) {
	    Class<?> superclass = klass.getSuperclass();
	    if( superclass == null || superclass == Object.class ) return klass;
	    return getTopSuperClass( superclass );
	}

	public static Class<?> getTopSuperClass( Object object ) {
	    return ( object == null ) ? null : getTopSuperClass( object.getClass() );
	}

	public static <T> T getInstance( Class<T> klass ) {
		try {
			return klass.newInstance();
		} catch( InstantiationException | IllegalAccessException e ) {
        	throw new ClassCastingException( e );
        }
	}

	@SuppressWarnings( "unchecked" )
    public static <T> T getInstance( Type type ) throws ClassNotFoundException {
		return (T) getInstance( getClass( type ) );
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
		return ( inspectInstance == null ) ? false : isExtendedBy( inspectInstance.getClass(), foundClass );
	}

	/**
	 * Get resource as stream
	 *
	 * @param name	resource name
	 * @return resource input stream
	 */
	public static InputStream getResourceAsStream( String name ) {
		name = StringUtil.nvl( name ).replaceFirst( "^/", "" );
		return getClassLoader().getResourceAsStream( name );
	}

}
