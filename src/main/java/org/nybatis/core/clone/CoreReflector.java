package org.nybatis.core.clone;

import org.nybatis.core.exception.unchecked.InvalidAccessException;
import org.nybatis.core.exception.unchecked.ReflectiveException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Core reflector
 *
 * @author nayasis@gmail.com
 * @since 2017-03-24
 */
public class CoreReflector {

    private final ConcurrentHashMap<Class,Set<Field>>       cacheField       = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class,Set<Method>>      cacheMethod      = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class,Set<Constructor>> cacheConstructor = new ConcurrentHashMap<>();

    public CoreReflector() {}

    private ParameterNameReader parameterNameReader = new ParameterNameReader();

    public void setField( Object bean, Field field, Object value ) {

        if( field == null ) return;
        setAccessible( field );

        if( isExclusive(field) ) return;

        try {
            if( Modifier.isStatic(field.getModifiers()) ){
                field.set( null, value );
            } else {
                field.set( bean, value );
            }
        } catch( IllegalAccessException e ) {
            throw new InvalidAccessException( e );
        }

    }

    public <T> T getFieldValue( Object bean, Field field ) {

        if( field == null ) return null;
        setAccessible( field );

        Object val;
        try {
            if( Modifier.isStatic(field.getModifiers()) ){
                val = field.get( null );
            } else {
                val = field.get( bean );
            }
            return val == null ? null : (T) val;
        } catch( IllegalAccessException e ) {
            throw new InvalidAccessException( e );
        }

    }

    private void setAccessible( Field field ) {
        if( ! field.isAccessible() ) {
            field.setAccessible( true );
        }
    }

    public  Set<Field> getFields( Object object ) {
        if( object == null ) return new LinkedHashSet<>();
        return getFields( object.getClass() );
    }

    public Set<Field> getFields( Class klass ) {

        if( klass == null ) return new LinkedHashSet<>();

        if( ! cacheField.containsKey(klass) ) {

            Set<Field> fields = new LinkedHashSet<>();

            for( Field field : klass.getDeclaredFields() ) {
                if( field.isSynthetic() ) continue; // if field is generated by compiler, skip it.
                fields.add( field );
            }

            Class<?> superClass = klass.getSuperclass();

            if( superClass != null && superClass != Object.class ) {
                fields.addAll( getFields( superClass ) );
            }

            cacheField.putIfAbsent( klass, fields );

        }

        return cacheField.get( klass );

    }

    private boolean isExclusive( Field field ) {
        if( field.isSynthetic() ) return true; // if field is generated by compiler, skip it.
        if( field.isEnumConstant() ) return true;
        return false;
    }

    public  Set<Method> getMethods( Object object ) {
        if( object == null ) return new LinkedHashSet<>();
        return getMethods( object.getClass() );
    }

    public Set<Method> getMethods( Class klass ) {

        if( klass == null ) return new LinkedHashSet<>();

        if( ! cacheMethod.containsKey(klass) ) {

            Set<Method> methods = new LinkedHashSet<>();

            for( Method method : klass.getDeclaredMethods() ) {
                if( method.isSynthetic() ) continue; // if method is generated by compiler, skip it.
                if( Modifier.isInterface(method.getModifiers()) ) continue;
                if( Modifier.isAbstract(method.getModifiers()) ) continue;
                methods.add( method );
            }

            Class<?> superClass = klass.getSuperclass();

            if( superClass != null && superClass != Object.class ) {
                methods.addAll( getMethods( superClass ) );
            }

            cacheMethod.putIfAbsent( klass, methods );

        }

        return cacheMethod.get( klass );

    }

    public  Set<Constructor> getContructors( Object object ) {
        if( object == null ) return new LinkedHashSet<>();
        return getContructors( object.getClass() );
    }

    public Set<Constructor> getContructors( Class klass ) {

        if( klass == null ) return new LinkedHashSet<>();

        if( ! cacheConstructor.containsKey(klass) ) {

            Set<Constructor> constructors = new LinkedHashSet<>();

            for( Constructor constructor : klass.getDeclaredConstructors() ) {
                if( constructor.isSynthetic() ) continue; // if constructor is generated by compiler, skip it.
                constructors.add( constructor );
            }

            cacheConstructor.putIfAbsent( klass, constructors );

        }

        return cacheConstructor.get( klass );

    }

    public List<String> getParameterNames( Method method ) {
        return parameterNameReader.read( method );
    }

    public List<String> getParameterNames( Constructor constructor ) {
        return parameterNameReader.read( constructor );
    }

}
