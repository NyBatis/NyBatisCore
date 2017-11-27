package org.nybatis.core.reflection;

import com.fasterxml.jackson.core.type.TypeReference;
import org.nybatis.core.clone.Cloner;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.exception.unchecked.JsonIOException;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.core.BeanMerger;
import org.nybatis.core.reflection.core.CoreReflector;
import org.nybatis.core.reflection.core.JsonConverter;
import org.nybatis.core.reflection.mapper.MethodInvocator;
import org.nybatis.core.reflection.mapper.NInvocationHandler;
import org.nybatis.core.reflection.mapper.NObjectMapper;
import org.nybatis.core.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * Reflection Utility
 *
 * @author nayasis@gmail.com
 *
 */
public class Reflector {

	private static JsonConverter jsonConverter = new JsonConverter( new NObjectMapper() );

	/**
	 * Creates and returnes a copy of object
	 *
	 * @param object object to clone
	 * @param <T> object's generic type
	 * @return a clone of object
	 */
    @SuppressWarnings( "unchecked" )
    public static <T> T clone( T object ) {
		return new Cloner().deepClone( object );
    }

	/**
	 * Copy data in instance
	 *
	 * @param source	bean as source
	 * @param target	bean as target
	 */
    public static void copy( Object source, Object target ) {
		if( source == null || target == null ) return;
		if( ClassUtil.isExtendedBy(target.getClass(),source.getClass()) || ClassUtil.isExtendedBy(source.getClass(),target.getClass()) ) {
    		new Cloner().copy( source, target );
		} else {
			Object newTarget = toBeanFrom( source, target.getClass() );
			new Cloner().copy( newTarget, target );
		}
    }

	/**
	 * Merge data in instance
	 *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
	 * @param <T> target's generic type
	 * @return merged Map
	 */
    public static <T> T merge( Object source, T target ) {
		return merge( source, target, true );
	}

	/**
	 * Merge data in instance
	 *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @param skipEmpty if false, skip merging when source value is null. if true, skip merging when source vale is null or empty.
	 * @param <T> source's generic type
	 * @return merged Map
	 */
    public static <T> T merge( Object source, T target, boolean skipEmpty ) {
        return new BeanMerger().merge( source, target, skipEmpty );
    }

	/**
	 * Get inspection result of fields within instance
	 *
	 * @param bean    instance to inspect
	 * @return report of fields' value
	 */
    public static String toString( Object bean ) {
		CoreReflector coreReflector = new CoreReflector();
    	NList result = new NList();
        for( Field field : coreReflector.getFields(bean) ) {
        	if( ! field.isAccessible() ) field.setAccessible( true );
			String typeName = field.getType().getName();
        	result.add( "field", field.getName() );
			result.add( "type", typeName );
        	try {
        		switch( typeName ) {
        			case "[C" :
        				result.add( "value", "[" + new String( (char[]) field.get( bean ) ) + "]" );
        				break;
        			default :
        				result.add( "value", field.get( bean ) );

        		}
        	} catch( IllegalArgumentException | IllegalAccessException e ) {
        		result.add( "value", e.getMessage() );
            }
        }
        return result.toString();
    }

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @param sort			whether or not to sort key of json
	 * @param ignoreNull	whether or not to ignore null value
	 * @return json text
	 */
	public static String toJson( Object fromBean, boolean prettyPrint, boolean sort, boolean ignoreNull ) throws JsonIOException {
		return jsonConverter.toJson( fromBean, prettyPrint, sort, ignoreNull );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @return json text
	 */
	public static String toJson( Object fromBean, boolean prettyPrint ) throws JsonIOException {
		return toJson( fromBean, prettyPrint, false, false );

	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @return json text
	 */
	public static String toJson( Object fromBean ) throws JsonIOException {
		return toJson( fromBean, false );
	}

	/**
	 * get json text without null value
	 *
	 * @param fromBean		instance to convert as json
	 * @param prettyPrint	true if you want to see json text with indentation
	 * @return json text
	 */
	public static String toNullIgnoredJson( Object fromBean, boolean prettyPrint ) throws JsonIOException {
		return toJson( fromBean, prettyPrint, false, true );
	}

	/**
	 * get json text without null value
	 *
	 * @param fromBean	instance to convert as json data
	 * @return json text
	 */
	public static String toNullIgnoredJson( Object fromBean ) throws JsonIOException {
		return toNullIgnoredJson( fromBean, false );
	}

	/**
	 * Get map with flatten key
	 *
	 * <pre>
	 * json text or map like this
	 *
	 * {
	 *   "name" : {
	 *     "item" : [
	 *     	  { "key" : "A", "value" : 1 }
	 *     	]
	 *   }
	 * }
	 *
	 * will be converted as below.
	 *
	 * { "name.item[0].key" : "A", "name.item[0].value" : 1 }
	 * </pre>
	 *
	 * @param object	json string, Map or bean
	 * @return map with flattern key
	 */
	public static Map<String, Object> toMapWithFlattenKey( Object object ) throws JsonIOException {
		return jsonConverter.toMapWithFlattenKey( object );
	}

	/**
	 * Get map with unflatten key
	 *
	 * <pre>
	 * json text or map like this
	 *
	 * { "name.item[0].key" : "A", "name.item[0].value" : 1 }
	 *
	 * will be converted as below.
	 *
	 * {
	 *   "name" : {
	 *     "item" : [
	 *     	  { "key" : "A", "value" : 1 }
	 *     	]
	 *   }
	 * }
	 * </pre>
	 *
	 * @param object	json string, Map or bean
	 * @return map with flattern key
	 */
	public static Map<String, Object> toMapWithUnflattenKey( Object object ) throws JsonIOException {
		return jsonConverter.toMapWithUnflattenKey( object );
	}

	/**
	 * check text is valid json type
	 *
	 * @param json	json text
	 * @return valid or not
	 */
	public static boolean isValidJson( String json ) {
		return jsonConverter.isValidJson( json );
	}

	/**
	 * Convert as bean from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @param toClass	class to return
	 * @param <T>		return type
	 * @return	bean filled by object's value
	 */
	public static <T> T toBeanFrom( Object object, Class<T> toClass ) throws JsonIOException {
		return jsonConverter.toBeanFrom( object, toClass );
	}

	/**
	 * Convert as bean from object
	 * @param object		json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @param typeReference	type to return
	 * 	<pre>
	 *	  Examples are below.
	 *	  	- new TypeReference&lt;List&lt;HashMap&lt;String, Object&gt;&gt;&gt;() {}
	 *	    - new TypeReference&lt;List&lt;String&gt;&gt;() {}
	 *	    - new TypeReference&lt;List&gt;() {}
	 * 	</pre>
	 * @param <T>		return type
	 * @return	bean filled by object's value
	 */
	public static <T> T toBeanFrom( Object object, TypeReference<T> typeReference ) throws JsonIOException {
		return jsonConverter.toBeanFrom( object, typeReference );
	}

	/**
	 * convert json to list
	 *
	 * @param jsonString	json text
	 * @param typeClass   	list's generic type
	 * @param <T> generic type
     * @return list
     */
	public static <T> List<T> toListFromJson( String jsonString, Class<T> typeClass ) throws JsonIOException {
		return jsonConverter.toListFromJson( jsonString, typeClass );
	}

	/**
	 * Convert as List
	 *
	 * @param json json text
	 * @return List
	 */
	public static List toListFromJson( String json ) throws JsonIOException {
		return toListFromJson( json, Object.class );
	}

	/**
	 * Convert as List&lt;String&gt;
	 * @param json	json text
	 * @return List
	 */
	public static List<String> toListFromJsonAsString( String json ) throws JsonIOException {
		return toListFromJson( json, String.class );
	}

	/**
	 * Convert as Map from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @return	Map filled by object's value
	 */
	public static Map<String, Object> toMapFrom( Object object ) throws JsonIOException {
		return jsonConverter.toMapFrom( object );
	}

	/**
	 * Convert as NMap from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @return	NMap filled by object's value
	 */
	public static NMap toNMapFrom( Object object ) throws JsonIOException {
		return new NMap( toMapFrom( object ) );
	}

	/**
	 * Wrap bean with invocation method
	 *
	 * @param beanToWrapProxy	target bean to wrap method
	 * @param interfaces		target interfaces to wrap method
	 * @param methodInvocator	method invocator
	 * @param <T> 				expected class of return
	 * @return	proxy bean to wrap
	 */
    public static <T> T wrapProxy( T beanToWrapProxy, Class<?>[] interfaces, MethodInvocator methodInvocator ) {
    	return (T) Proxy.newProxyInstance( beanToWrapProxy.getClass().getClassLoader(), interfaces, new NInvocationHandler( beanToWrapProxy, methodInvocator ) );
    }

	/**
	 * Unwrap proxy invocator from bean
	 * @param beanToUnwrapProxy	target bean to unwrap proxy method
	 * @param <T> 				expected class of return
	 * @return	original bean
	 * @throws ClassCastingException if beanToUnwrapProxy is not proxy bean.
	 */
	public static <T> T unwrapProxy( T beanToUnwrapProxy ) {
		if( beanToUnwrapProxy == null || ! Proxy.isProxyClass( beanToUnwrapProxy.getClass() ) ) {
			return beanToUnwrapProxy;
		}
		InvocationHandler invocationHandler = Proxy.getInvocationHandler( beanToUnwrapProxy );
		if( ! (invocationHandler instanceof  NInvocationHandler) ) {
			throw new ClassCastingException( "Only proxy instance to generated by nayasis.common.reflection.Refector can be unwraped." );
		}
		return (T) ((NInvocationHandler) invocationHandler).getOriginalInstance();
	}

	/**
	 * Check json date format (yyyyMMdd'T'hh24:mi:ss.SSSZ)
	 * @param value value to check
	 * @return true if value is json date format
	 */
	public static boolean isJsonDate( Object value ) {
		return jsonConverter.isJsonDate( value );
	}

}
