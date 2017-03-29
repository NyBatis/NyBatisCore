package org.nybatis.core.clone.processor;

import org.nybatis.core.clone.annotation.Ignorable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Immutable class checker
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class IgnorableChecker {

    private Map<Class,  Boolean> ignorableClass  = new ConcurrentHashMap<>();
    private Map<Method, Boolean> ignorableMethod = new ConcurrentHashMap<>();
    private Map<Field,  Boolean> ignorableField  = new ConcurrentHashMap<>();

    public IgnorableChecker() {}

    public boolean isIgnorable( Class klass ) {
        if( klass == null ) return true;
        if( ! ignorableClass.containsKey(klass) ) {
            for( Annotation annotation : klass.getAnnotations() ) {
                if( annotation.annotationType() == Ignorable.class ) {
                    ignorableClass.putIfAbsent( klass, Boolean.TRUE );
                    return true;
                }
            }
            ignorableClass.putIfAbsent( klass, Boolean.FALSE );
        }
        return ignorableClass.get( klass );
    }

    public boolean isIgnorable( Method method ) {
        if( method == null ) return true;
        if( ! ignorableMethod.containsKey(method) ) {
            for( Annotation annotation : method.getAnnotations() ) {
                if( annotation.annotationType() == Ignorable.class ) {
                    ignorableMethod.putIfAbsent( method, Boolean.TRUE );
                    return true;
                }
            }
            ignorableMethod.putIfAbsent( method, Boolean.FALSE );
        }
        return ignorableMethod.get( method );
    }

    public boolean isIgnorable( Field field ) {
        if( field == null ) return true;
        if( ! ignorableField.containsKey(field) ) {
            for( Annotation annotation : field.getAnnotations() ) {
                if( annotation.annotationType() == Ignorable.class ) {
                    ignorableField.putIfAbsent( field, Boolean.TRUE );
                    return true;
                }
            }
            ignorableField.putIfAbsent( field, Boolean.FALSE );
        }
        return ignorableField.get( field );
    }


    public boolean isIgnorable( Object object ) {
        if( object == null ) return true;
        return isIgnorable( object.getClass() );
    }

    public String toString() {
        return ignorableClass.toString();
    }

}
