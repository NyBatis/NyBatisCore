package org.nybatis.core.cloneNew.processor;

import org.nybatis.core.cloneNew.annotation.Immutable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Immutable class checker
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class ImmutableChecker {

    private Map<Class,  Boolean> imutableClass  = new ConcurrentHashMap<>();
    private Map<Method, Boolean> imutableMethod = new ConcurrentHashMap<>();
    private Map<Field,  Boolean> imutableField  = new ConcurrentHashMap<>();

    public ImmutableChecker() {

        add( Void.class );
        add( String.class );
        add( Character.class );
        add( Integer.class );
        add( Long.class );
        add( Class.class );
        add( Float.class );
        add( Double.class );
        add( BigDecimal.class );
        add( BigInteger.class );
        add( Byte.class );
        add( Short.class );
        add( Boolean.class );

        add( void.class );
        add( char.class );
        add( int.class );
        add( long.class );
        add( float.class );
        add( double.class );
        add( byte.class );
        add( short.class );
        add( boolean.class );

        add( URI.class );
        add( URL.class );
        add( UUID.class );
        add( Pattern.class );
        add( LocalDate.class );
        add( LocalTime.class );
        add( TimeZone.class );
        add( Period.class );
        add( Instant.class );

    }

    public ImmutableChecker add( Class klass ) {
        imutableClass.putIfAbsent( klass, Boolean.TRUE );
        return this;
    }

    public boolean isImmutable( Class klass ) {
        if( klass == null ) return true;
        if( ! imutableClass.containsKey(klass) ) {
            for( Annotation annotation : klass.getAnnotations() ) {
                if( annotation.annotationType() == Immutable.class ) {
                    imutableClass.putIfAbsent( klass, Boolean.TRUE );
                    return true;
                }
            }
            imutableClass.putIfAbsent( klass, Boolean.FALSE );
        }
        return imutableClass.get( klass );
    }

    public boolean isImmutable( Method method ) {
        if( method == null ) return true;
        if( ! imutableMethod.containsKey(method) ) {
            for( Annotation annotation : method.getAnnotations() ) {
                if( annotation.annotationType() == Immutable.class ) {
                    imutableMethod.putIfAbsent( method, Boolean.TRUE );
                    return true;
                }
            }
            imutableMethod.putIfAbsent( method, Boolean.FALSE );
        }
        return imutableMethod.get( method );
    }

    public boolean isImmutable( Field field ) {
        if( field == null ) return true;
        if( ! imutableField.containsKey(field) ) {
            for( Annotation annotation : field.getAnnotations() ) {
                if( annotation.annotationType() == Immutable.class ) {
                    imutableField.putIfAbsent( field, Boolean.TRUE );
                    return true;
                }
            }
            imutableField.putIfAbsent( field, Boolean.FALSE );
        }
        return imutableField.get( field );
    }


    public boolean isImmutable( Object object ) {
        if( object == null ) return true;
        return isImmutable( object.getClass() );
    }

    public String toString() {
        return imutableClass.toString();
    }

}
