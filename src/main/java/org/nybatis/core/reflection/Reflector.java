package org.nybatis.core.reflection;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.nybatis.core.clone.Cloner;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.exception.unchecked.JsonIOException;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.reflection.core.BeanMerger;
import org.nybatis.core.reflection.core.CoreReflector;
import org.nybatis.core.reflection.mapper.MethodInvocator;
import org.nybatis.core.reflection.mapper.NInvocationHandler;
import org.nybatis.core.reflection.mapper.NObjectMapper;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Validator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Reflection Utility
 *
 * @author nayasis@gmail.com
 *
 */
public class Reflector {

	private static NObjectMapper objectMapper       = new NObjectMapper( false );
	private static NObjectMapper objectMapperSorted = new NObjectMapper( true );

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
	public static String toJson( Object fromBean, boolean prettyPrint, boolean sort, boolean ignoreNull ) {
		if( fromBean == null ) return null;
		NObjectMapper mapper = sort ? objectMapperSorted : objectMapper;
		mapper.setSerializationInclusion( ignoreNull ? Include.NON_NULL : Include.ALWAYS );
		ObjectWriter writer = prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
		try {
			return writer.writeValueAsString( fromBean );
		} catch( IOException e ) {
        	throw new JsonIOException( e );
        }
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @return json text
	 */
	public static String toJson( Object fromBean, boolean prettyPrint ) {
		return toJson( fromBean, prettyPrint, false, false );

	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @return json text
	 */
	public static String toJson( Object fromBean ) {
		return toJson( fromBean, false );
	}

	/**
	 * get json text without null value
	 *
	 * @param fromBean		instance to convert as json
	 * @param prettyPrint	true if you want to see json text with indentation
	 * @return json text
	 */
	public static String toNullIgnoredJson( Object fromBean, boolean prettyPrint ) {
		return toJson( fromBean, prettyPrint, false, true );
	}

	/**
	 * get json text without null value
	 *
	 * @param fromBean	instance to convert as json data
	 * @return json text
	 */
	public static String toNullIgnoredJson( Object fromBean ) {
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
	public static Map<String, Object> toMapWithFlattenKey( Object object ) {
		Map<String, Object> map = new HashMap<>();
		if( Validator.isNull(object) ) return map;
		flattenKeyRecursivly( "", toMapFrom( object ), map );
		return map;
	}

	private static void flattenKeyRecursivly( String currentPath, Object json, Map result ) {

		if( json instanceof Map ) {

			Map<String, Object> map = (Map) json;

			String prefix = StringUtil.isEmpty( currentPath ) ? "" : currentPath + ".";

			for( String key : map.keySet() ) {
				flattenKeyRecursivly( prefix + key, map.get( key ), result );
			}

		} else if( json instanceof List ) {

			List list = (List) json;

			for( int i = 0, iCnt = list.size(); i < iCnt; i++ ) {
				flattenKeyRecursivly( String.format( "%s[%d]", currentPath, i ), list.get( i ), result );
			}

		} else {
			result.put( currentPath, json );
		}

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
	public static Map<String, Object> toMapWithUnflattenKey( Object object ) {

		Map<String, Object> map = new HashMap<>();

		if( Validator.isNull(object) ) return map;

		Map<String, Object> objectMap = toMapFrom( object );

		for( String key : objectMap.keySet() ) {
			unflattenKeyRecursivly( key, objectMap.get( key ), map );
		}

		return map;

	}

	private static void unflattenKeyRecursivly( String jsonPath, Object value, Map result ) {

		String path  = jsonPath.replaceFirst( "\\[.*\\]", "" ).replaceFirst( "\\..*?$", "" );
		String index = jsonPath.replaceFirst(  "^(" + path + ")\\[(.*?)\\](.*?)$", "$2" );

		if( index.equals( jsonPath ) ) index = "";

		boolean isArray = ! index.isEmpty();

		String currentPath = String.format( "%s%s", path, isArray ? String.format("[%s]", index) : "" );

		boolean isKey = currentPath.equals( jsonPath );

		if( isKey ) {
			if( isArray ) {
				int idx = new PrimitiveConverter( index ).toInt();
				setValueToListInJson( path, idx, value, result );
			} else {
				result.put( path, value );
			}
		} else {

			if( ! result.containsKey(path) ) {
				result.put( path, isArray ? new ArrayList() : new HashMap() );
			}

			Map newVal;

			if( isArray ) {

				List list = (List) result.get( path );

				int idx = new PrimitiveConverter( index ).toInt();

				if( list.size() <= idx || list.get(idx) == null ) {
					setValueToListInJson( path, idx, new HashMap(), result );
				}

				newVal = (Map) list.get( idx );

			} else {
				newVal = (Map) result.get( path );
			}

			String recursivePath = jsonPath.replaceFirst( currentPath.replaceAll( "\\[", "\\\\[" ) + ".", "" );

			unflattenKeyRecursivly( recursivePath, value, newVal );

		}

	}

	private static void setValueToListInJson( String key, int idx, Object value, Map json ) {

		if( ! json.containsKey( key ) ) {
			json.put( key, new ArrayList<>() );
		}

		List list = (List) json.get( key );

		int listSize = list.size();

		if( idx >= listSize ) {
			for( int i = listSize; i <= idx; i++ ) {
				list.add( null );
			}
		}

		list.set( idx, value );

	}


	private static String getContent( String fromJsonString ) {
		return StringUtil.isEmpty( fromJsonString ) ? "{}" : fromJsonString;
	}

	private static String getArrayContent( String fromJsonString ) {
		return StringUtil.isEmpty( fromJsonString ) ? "[]" : fromJsonString;
	}

	/**
	 * check text is valid json type
	 *
	 * @param json	json text
	 * @return valid or not
	 */
	public static boolean isValidJson( String json ) {
		try {
			objectMapper.readTree( json );
			return true;
		} catch( IOException e ) {
			return false;
		}
	}

	/**
	 * Convert as bean from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @param toClass	class to return
	 * @param <T>		return type
	 * @return	bean filled by object's value
	 */
	public static <T> T toBeanFrom( Object object, Class<T> toClass ) {

		if( Types.isString( object ) ) {
			return toBeanFromJson( object.toString(), toClass );
		} else {
			return objectMapper.convertValue( object, toClass );
		}

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
	public static <T> T toBeanFrom( Object object, TypeReference<T> typeReference ) {

		if( Types.isString( object ) ) {
			return toBeanFromJson( object.toString(), typeReference );
		} else {
			return objectMapper.convertValue( object, typeReference );
		}

	}

	/**
	 * Convert as bean from json text
	 * @param jsonString	json text
	 * @param toClass		class to return
	 * @param <T>			return type
	 * @return bean filled by json value
	 */
	private static <T> T toBeanFromJson( String jsonString, Class<T> toClass ) {
    	try {
    		return objectMapper.readValue( getContent( jsonString ), toClass );
        } catch( JsonParseException e ) {
			throw new JsonIOException( "JsonParseException : {}\n\t- json string :\n{}\n\t- target class : {}", e.getMessage(), jsonString, toClass );
    	} catch( IOException e ) {
    		throw new JsonIOException( e );
    	}
    }

	/**
	 * Convert as bean from json text
	 * @param jsonString	json text
	 * @param typeReference	type to return
	 * 	<pre>
	 *	  Examples are below.
	 *	  	- new TypeReference<List<HashMap<String, Object>>>() {}
	 *	    - new TypeReference<List<String>>() {}
	 *	    - new TypeReference<List>() {}
	 * 	</pre>
	 * @param <T>			return type
	 * @return bean filled by json value
	 */
	private static <T> T toBeanFromJson( String jsonString, TypeReference<T> typeReference ) {
		try {
			return objectMapper.readValue( getContent( jsonString ), typeReference );
		} catch( JsonParseException e ) {
			throw new JsonIOException( "JsonParseException : {}\n\t- json string :\n{}", e.getMessage(), jsonString );
		} catch( IOException e ) {
			throw new JsonIOException( e );
		}
	}

	/**
	 * Convert as List from json text
	 *
	 * @param jsonString	json text
	 * @param typeReference	type to return
	 * 	<pre>
	 *	  Examples are below.
	 *	  	- new TypeReference&lt;List&lt;HashMap&lt;String, Object&gt;&gt;&gt;() {}
	 *	    - new TypeReference&lt;List&lt;String&gt;&gt;() {}
	 *	    - new TypeReference&lt;List&gt;() {}
	 * 	</pre>
	 * @param <T> return type
	 * @return List
	 */
	public static <T> List<T> toListFromJsonAs( String jsonString, TypeReference typeReference ) {
		try {
			return objectMapper.readValue( getArrayContent(jsonString), typeReference );
		} catch( JsonParseException e ) {
			throw new JsonIOException( "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), jsonString );
		} catch( IOException e ) {
			throw new JsonIOException( e );
		}
	}

	/**
	 * Convert as List&lt;Map&gt;
	 * @param json	json text
	 * @return List
	 */
    public static List<Map<String,Object>> toListFromJsonAsMap( String json ) {
    	return toListFromJsonAs( json, new TypeReference<List<HashMap<String, Object>>>() {} );
    }

	/**
	 * Convert as List
	 *
	 * @param json json text
	 * @return List
	 */
	public static List toListFromJson( String json ) {
		return toListFromJsonAs( json, new TypeReference<List>() {} );
	}

	/**
	 * Convert as List&lt;String&gt;
	 * @param json	json text
	 * @return List
	 */
	public static List<String> toListFromJsonAsString( String json ) {
		return toListFromJsonAs( json, new TypeReference<List<String>>() {} );
	}

	/**
	 * Convert as Map from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @return	Map filled by object's value
	 */
	public static Map<String, Object> toMapFrom( Object object ) {

		if( object == null ) return new HashMap<>();

		if( Types.isString(object)  ) {
			return toMapFromJson( object.toString() );
		} else {
			return objectMapper.convertValue( object, Map.class );
		}

	}

	/**
	 * Convert as Map from json text
	 * @param jsonString	json text
	 * @return	Map filled by object's value
	 */
	public static Map<String, Object> toMapFromJson( String jsonString ) {
		try {
			Map<String, Object> stringObjectMap = objectMapper.readValue( getContent( jsonString ), new TypeReference<LinkedHashMap<String, Object>>() {} );
			return Validator.nvl( stringObjectMap, new LinkedHashMap<String, Object>() );
		} catch( JsonParseException e ) {
			throw new JsonIOException( e, "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), jsonString );
		} catch( IOException e ) {
			throw new JsonIOException( e );
		}
	}

	/**
	 * Convert as NMap from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @return	NMap filled by object's value
	 */
	public static NMap toNMapFrom( Object object ) {
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
		if( Types.isNotString(value) ) return false;
		String val = value.toString();
		return Validator.isMatched( val, "\\d{4}-(0[0-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])T([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\\.\\d{3}[+-]([0-1][0-9]|2[0-4])[0-5][0-9]" );
	}

}
