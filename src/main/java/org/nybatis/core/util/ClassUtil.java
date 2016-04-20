package org.nybatis.core.util;

import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.exception.unchecked.ClassNotExistException;
import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.file.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


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
	 * get root class in thread stack
	 *
	 * @return root class
	 */
	public static Class getRootClass() {

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement   topElement = stackTrace[ stackTrace.length - 1 ];

		try {
			return ClassUtil.getClass( topElement.getClassName() );
		} catch( ClassNotFoundException e ) {
			throw new ClassNotExistException( e );
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
	 * Check resource exists
	 * @param name resource name
	 * @return true if resource is exist in class path.
	 */
	public static boolean isResourceExisted( String name ) {
		return getClassLoader().getResource( refineResourceName(name) ) != null;
	}

	/**
	 * Get resource as stream
	 *
	 * @param name	resource name
	 * @return resource input stream
	 */
	public static InputStream getResourceAsStream( String name ) {
		return getClassLoader().getResourceAsStream( refineResourceName(name) );
	}

	/**
	 * revmoe first "/" character in resource name
	 *
	 * @param name resource name
	 * @return refined resource name
	 */
	private static String refineResourceName( String name ) {
		name = StringUtil.nvl( name ).replaceFirst( "^/", "" );
		return name;
	}

	/**
	 * Get resource names
	 *
	 * @param pattern   path matching pattern (glob expression. if not exists, add all result)
	 * <pre>
	 * ** : ignore directory variation
	 * *  : filename LIKE search
	 *
	 * 1. **.xml : searchDir 아래의 모든 디렉토리에 있는 파일 중 확장자가 xml로 끝나는 모든 파일
	 * 2. *.xml  : searchDir 바로 1depth 아래 디렉토리에 있는 파일 중 확장자가 xml로 끝나는 모든 파일
	 * 3. c:\home\*\*.xml : 'c:\home\ 바로 1 depth 아래의 하위 디렉토리에 있는 확장자가 xml로 끝나는 모든 파일
	 * 4. c:\home\**\*.xml : 'c:\home\ 아래의 모든 하위 디렉토리에 있는 확장자가 xml로 끝나는 모든 파일
	 *
	 * 1. *  It matches zero , one or more than one characters. While matching, it will not cross directories boundaries.
	 * 2. ** It does the same as * but it crosses the directory boundaries.
	 * 3. ?  It matches only one character for the given name.
	 * 4. \  It helps to avoid characters to be interpreted as special characters.
	 * 5. [] In a set of characters, only single character is matched. If (-) hyphen is used then, it matches a range of characters. Example: [efg] matches "e","f" or "g" . [a-d] matches a range from a to d.
	 * 6. {} It helps to matches the group of sub patterns.
	 *
	 * 1. *.java when given path is java , we will get true by PathMatcher.matches(path).
	 * 2. *.* if file contains a dot, pattern will be matched.
	 * 3. *.{java,txt} If file is either java or txt, path will be matched.
	 * 4. abc.? matches a file which start with abc and it has extension with only single character.
	 *
	 * </pre>
	 * @return
	 */
	public static List<String> getResourceNames( String... pattern ) {

		List<String> resourceNames = new ArrayList<>();

		URL root;

		if( Const.path.isRunInJar() ) {
			root = getClassLoader().getResource( getRootClass().getName().replace( ".", "/" ) + ".class" );
		} else {
			root = getClassLoader().getResource( "" );
		}

		if( "jar".equals( root.getProtocol() ) ) {

			JarFile jar = getJarFile( root );

			Set<PathMatcher> matchers = FileUtil.toPathMacher( toJarPattern( pattern ) );
			boolean addAll = ( matchers.size() == 0 );

			for( JarEntry entry : Collections.list( jar.entries() ) ) {
				if( addAll ) {
					resourceNames.add( entry.getName() );
				} else {
					for( PathMatcher matcher : matchers ) {
						if( matcher.matches( Paths.get( entry.getName() ) )) {
							resourceNames.add( entry.getName() );
							break;
						}
					}
				}
			}

		} else if( "file".equals( root.getProtocol() ) ) {

			for( Path path : FileUtil.search( Const.path.getBase(), true, false, -1, toFilePattern( pattern ) ) ) {
				String pathVal = FileUtil.nomalizeSeparator( path.toString() );
				resourceNames.add( pathVal.replace( Const.path.getBase(), "" ).replaceFirst( "^/", "" ) );
			}

		}

		return resourceNames;

	}

	private static String[] toFilePattern( String[] pattern ) {
		String[] result = new String[ pattern.length ];
		for( int i = 0, iCnt = pattern.length; i < iCnt; i++ ) {
			result[ i ] = ( Const.path.getBase() + "/" + pattern[ i ] ).replaceAll( "//", "/" );
        }
		return result;
	}

	private static String[] toJarPattern( String[] pattern ) {
		String[] result = new String[ pattern.length ];
		for( int i = 0, iCnt = pattern.length; i < iCnt; i++ ) {
			result[ i ] = pattern[ i ].replaceAll( "//", "/" ).replaceFirst( "^/", "" );
        }
		return result;
	}

	private static JarFile getJarFile( URL root ) {
		String jarPath = root.getPath().replaceFirst( "^file:", "" );
		jarPath = jarPath.substring( 0, jarPath.indexOf( "!" ) );
		return getJarFile( jarPath );
	}

	private static JarFile getJarFile( String jarPath ) {
		try {
            return new JarFile( URLDecoder.decode( jarPath, "UTF-8" ) );
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }
	}

}
