package org.nybatis.core.reflection;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.nybatis.core.exception.unchecked.ClassCastException;
import org.nybatis.core.exception.unchecked.JsonIOException;
import org.nybatis.core.exception.unchecked.ReflectiveException;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.reflection.mapper.MethodInvocator;
import org.nybatis.core.reflection.mapper.NInvocationHandler;
import org.nybatis.core.reflection.mapper.NObjectMapper;

import com.rits.cloning.Cloner;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

/**
 * Reflection Utility
 *
 * @author nayasis@gmail.com
 *
 */
public class Reflector {

	private static NObjectMapper objectMapper = new NObjectMapper();

    /**
     * 객체에 선언된 field 목록을 가져온다.
     *
     * @param bean 조사할 객체
     * @return 객체에 선언된 field 목록
     */
    public List<Field> getFieldsFrom( Object bean ) {

    	List<Field> result = new ArrayList<>();

    	if( bean != null ) {
    		for( Field field : bean.getClass().getDeclaredFields() ) {

    			// Skip Synthetic Field
    			if( (field.getModifiers() & 0x00001000 ) != 0 ) continue;

    			result.add( field );

    		}
    	}

        return result;

    }

    public List<Method> getMethodsFrom( Object bean ) {

    	List<Method> result = new ArrayList<>();

    	if( bean != null ) {
    		for( Method method : bean.getClass().getDeclaredMethods() ) {
    			result.add( method );
    		}
    	}

        return result;

    }

    /**
     * 객체에 선언된 field 이름 목록을 가져온다.
     *
     * @param bean 조사할 객체
     * @return 객체에 선언된 field 목록
     */
    public List<String> getFieldNamesFrom( Object bean ) {

    	List<Field>  from = getFieldsFrom( bean );
    	List<String> to   = new ArrayList<>( from.size() );

    	for( Field field : from ) {
    		to.add( field.getName() );
    	}

        return to;

    }

    /**
     * 객체에 선언된 field에 담긴 값을 가져온다.
     *
     * @param bean 조사할 객체
     * @param field 객체에 선언된 field
     * @return field에 담겨있는 값
     */
    public <T> T getFieldValueFrom( Object bean, Field field ) {

        field.setAccessible( true );

        try {

			Object val = field.get( bean );
			return val == null ? null : (T) val;

        } catch ( ReflectiveOperationException e ) {
            throw new ReflectiveException( e );
        }

    }

    /**
     * 객체에 선언된 field에 담긴 값을 가져온다.
     *
     * @param bean 조사할 객체
     * @param fieldName 객체에 선언된 field 명
     * @return field에 담겨있는 값
     * @throws ReflectiveOperationException field 접근 실패시
     */
    public <T> T getFieldValueFrom( Object bean, String fieldName ) {

        try {

            Field field = bean.getClass().getDeclaredField( fieldName );

			return getFieldValueFrom( bean, field );

        } catch ( ReflectiveOperationException e ) {
            throw new ReflectiveException( e );
        }


    }

    /**
     * 객체에 선언된 field에 값을 담는다.
     *
     * @param bean 조사할 객체
     * @param field 객체에 선언된 field
     * @throws ReflectiveOperationException field 접근 실패시
     */
    public void setFieldValueTo( Object bean, Field field, Object value ) {

        field.setAccessible( true );

        try {
            field.set( bean, value );

        } catch ( ReflectiveOperationException e ) {
            throw new ReflectiveException( e.getMessage(), e );
        }

    }

    /**
     * 객체에 선언된 field에 값을 담는다.
     *
     * @param bean 조사할 객체
     * @param fieldName 객체에 선언된 field 명
     * @param value field에 담을 값
     */
    public void setFieldValueTo( Object bean, String fieldName, Object value ) {

        Field field;

        try {

            field = bean.getClass().getField( fieldName );

            field.setAccessible( true );

            field.set(bean, value);

        } catch ( ReflectiveOperationException e ) {
            throw new ReflectiveException( e.getMessage(), e );
        }

    }

    /**
     * 객체의 field 이름과 값을 쌍으로 갖는 데이터를 구한다.
     *
     * @param instance 조사할 객체
     * @return field 이름과 값을 쌍으로 갖는 데이터
     */
    public Map<String, Object> getFieldMap( Object instance ) {

        Map<String, Object> result = new HashMap<String, Object>();

        for( Field field : getFieldsFrom(instance) ) {

            field.setAccessible( true );

            try {
                result.put( field.getName(), field.get(instance) );
            } catch ( ReflectiveOperationException e ) {
                throw new ReflectiveException( e.getMessage(), e );
            }

        }

        return result;

    }

    /**
     * 객체를 복사한다. (객체에 담긴 데이터까지 모두 포함)
     *
     * @param object 복사할 객체
     * @return 복사한 객체
     */
    @SuppressWarnings( "unchecked" )
    public <T> T clone( T object ) {
		return new Cloner().deepClone( object );
    }

    /**
     * 객체에 담긴 데이터를 복사한다.
     *
     * @param source 복제할 원본 객체
     * @param target 복제될 대상 객체
     */
    public <T, E extends T> E copy( T source, E target ) {
    	new Cloner().copyPropertiesOfInheritedClass( source, target );
    	return target;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public void merge( Object fromBean, Object toBean ) {

    	if( fromBean == null || toBean == null ) return;

    	Map fromMap = (fromBean instanceof Map) ? (Map) fromBean : toMapFromBean( fromBean );

    	if( toBean instanceof Map ) {

    		((Map) toBean ).putAll( fromMap );

    	} else {

    		mergeToMethod( fromMap, toBean );
    		mergeToField( fromMap, toBean );

    	}

    }

    @SuppressWarnings( "rawtypes" )
    private void mergeToField( Map fromMap, Object toBean ) {

    	List<Field> fields = getFieldsFrom( toBean );

    	List<Object> removedKeys = new ArrayList<>();

    	for( Object rawKey : fromMap.keySet() ) {

    		String fromKey = rawKey.toString();
    		Object fromVal = fromMap.get( fromKey );

    		Field remove = null;

    		for( Field field : fields ) {

    			if( ! fromKey.equals(field.getName()) ) {
    				continue;
    			}

				if( ! field.isAccessible() ) field.setAccessible( true );

				try {

					Object castedVal = new PrimitiveConverter( fromVal ).cast( field.getType() );
					field.set( toBean, castedVal );

					removedKeys.add( rawKey );
					remove = field;
					break;

				} catch( IllegalArgumentException | IllegalAccessException e ) {
//					NLogger.warn( e );
				}

    		}

    		fields.remove( remove );

    	}

    	for( Object key : removedKeys ) {
    		fromMap.remove( key );
    	}

    }

    @SuppressWarnings( "rawtypes" )
    private void mergeToMethod( Map fromMap, Object toBean ) {

    	List<Method> methods = getMethodsFrom( toBean );

    	for( int i = methods.size() - 1; i >= 0; i-- ) {
    		if( methods.get(i).getParameterCount() == 1 ) continue;
    		methods.remove( i );
    	}

    	List<Object> removedKeys = new ArrayList<>();

    	for( Object rawKey : fromMap.keySet() ) {

    		String fromKey = rawKey.toString();
    		Object fromVal = fromMap.get( fromKey );

    		Method remove = null;

    		for( Method method : methods ) {

    			if( method.getName().equals(fromKey) || method.getName().equalsIgnoreCase("set" + fromKey) ) {
    				try {

    					if( ! method.isAccessible() ) method.setAccessible( true );

    					Object castedVal = new PrimitiveConverter( fromVal ).cast( method.getParameters()[0].getClass() );
    					method.invoke( toBean, castedVal );

    					removedKeys.add( rawKey );
    					remove = method; break;

    				} catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
//    					NLogger.warn( e );
    				}
    			}

    		}

    		methods.remove( remove );

    	}

    	for( Object key : removedKeys ) {
    		fromMap.remove( key );
    	}

    }

    public boolean isArray( Object bean ) {
    	return bean != null && isArray( bean.getClass() );
    }

    public boolean isList( Object bean ) {
    	return bean != null && isList( bean.getClass() );
    }

    public boolean isArray( Class<?> klass ) {
    	return klass.isArray();
    }

    public boolean isList( Class<?> klass ) {
    	return klass.getSuperclass() == AbstractList.class;
    }

    /**
     * 특정 Instance에 담긴 field값을 구한다. (디버깅용)
     *
     * @param bean 검사할 instance
     * @return 출력결과
     */
    public String getFieldReport( Object bean ) {

    	NList result = new NList();

        for( Field field : getFieldsFrom(bean) ) {

        	if( ! field.isAccessible() ) field.setAccessible( true );

        	result.addRow( "field", field.getName() );

        	try {

        		String typeName = field.getType().getName();

        		result.addRow( "type", typeName );

        		switch( typeName ) {
        			case "[C" :
        				result.addRow( "value", "[" + new String( (char[]) field.get( bean ) ) + "]" );
        				break;
        			default :
        				result.addRow( "value", field.get( bean ) );

        		}

        	} catch( IllegalArgumentException | IllegalAccessException e ) {
        		result.addRow( "value", e.getMessage() );
            }

        }

        return result.toString();

    }

	public String toJson( Object fromBean, boolean prettyPrint ) {

		ObjectWriter writer = prettyPrint ? objectMapper.writerWithDefaultPrettyPrinter() : objectMapper.writer();

		try {
			return writer.writeValueAsString( fromBean );
		} catch( IOException e ) {
        	throw new JsonIOException( e );
        }

	}

	public String toJson( Object fromBean ) {
		return toJson( fromBean, false );
	}

    public Map<String, Object> toMapFromJson( String fromJson ) {
        try {
			Map<String, Object> stringObjectMap = objectMapper.readValue( getContent( fromJson ), new TypeReference<HashMap<String, Object>>() {} );
			return Validator.nvl( stringObjectMap, new LinkedHashMap<String, Object>() );
        } catch( JsonParseException e ) {
            throw new JsonIOException( "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), fromJson );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

	private String getContent( String fromJsonString ) {
		return StringUtil.isEmpty( fromJsonString ) ? "{}" : fromJsonString;
	}

	private String getArrayContent( String fromJsonString ) {
		return StringUtil.isEmpty( fromJsonString ) ? "[]" : fromJsonString;
	}

	public <T> T toBeanFromJson( String fromJsonString, Class<T> toClass ) {
    	try {
    		return objectMapper.readValue( getContent( fromJsonString ), toClass );
        } catch( JsonParseException e ) {
            throw new JsonIOException( "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), fromJsonString );
    	} catch( IOException e ) {
    		throw new JsonIOException( e );
    	}
    }

	public <T> List<T> toListFromJson( String fromJson, TypeReference typeReference ) {
		try {
			return objectMapper.readValue( getArrayContent(fromJson), typeReference );
		} catch( JsonParseException e ) {
			throw new JsonIOException( "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), fromJson );
		} catch( IOException e ) {
			throw new JsonIOException( e );
		}
	}

    public List<Map<String,Object>> toListFromJsonAsMap( String fromJson ) {
    	return toListFromJson( fromJson, new TypeReference<List<HashMap<String,Object>>>() {}  );
    }

	public List toListFromJson( String fromJson ) {
		return toListFromJson( fromJson, new TypeReference<List>() {}  );
	}

	public List toListFromJsonAsString( String fromJson ) {
		return toListFromJson( fromJson, new TypeReference<List<String>>() {}  );
	}

	public <T> T toBeanFromMap( Map<?, ?> fromMap, Class<T> toClass ) {
		return objectMapper.convertValue( fromMap, toClass );
	}

	public <T> T toBeanFromBean( Object fromBean, Class<T> toClass ) {
		return objectMapper.convertValue( fromBean, toClass );

	}

    @SuppressWarnings( "unchecked" )
    public <K, V> Map<K, V> toMapFromBean( Object fromBean ) {
    	return objectMapper.convertValue( fromBean, Map.class );
	}

    public NMap toNMapFromBean( Object fromBean ) {
    	return new NMap( toMapFromBean( fromBean ) );
    }

    @SuppressWarnings( "unchecked" )
    public <T> T makeProxyBean( T beanToProxy, Class<?>[] interfaces, MethodInvocator methodInvokator ) {
    	return (T) Proxy.newProxyInstance( beanToProxy.getClass().getClassLoader(), interfaces, new NInvocationHandler(beanToProxy, methodInvokator) );
    }

	public <T> T unwrapProxyBean( T proxyBean ) {

		if( proxyBean == null || ! Proxy.isProxyClass( proxyBean.getClass() ) ) return proxyBean;

		InvocationHandler invocationHandler = Proxy.getInvocationHandler( proxyBean );

		if( ! (invocationHandler instanceof  NInvocationHandler) ) {
			throw new ClassCastException( "Only proxy instance to generated by nayasis.common.reflection.Refector can be unwraped." );
		}

		return (T) ((NInvocationHandler) invocationHandler).getOriginalInstance();

	}

	private Throwable unwrapInvokeThrowable( Throwable throwable ) {

		Throwable unwrappedThrowable = throwable;

		while (true) {

			if ( unwrappedThrowable instanceof InvocationTargetException ) {
				unwrappedThrowable = ((InvocationTargetException) unwrappedThrowable).getTargetException();

			} else if ( unwrappedThrowable instanceof UndeclaredThrowableException ) {
				unwrappedThrowable = ((UndeclaredThrowableException) unwrappedThrowable).getUndeclaredThrowable();

			} else {
				return unwrappedThrowable;
			}

		}

	}

}
