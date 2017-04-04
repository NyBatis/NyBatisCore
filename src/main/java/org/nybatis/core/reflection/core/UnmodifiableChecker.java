package org.nybatis.core.reflection.core;

import org.nybatis.core.util.ClassUtil;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unmodifiable checker for Map or Collection
 *
 * @author nayasis@gmail.com
 * @since 2017-04-03
 */
public class UnmodifiableChecker {

    private ConcurrentHashMap<Class, Boolean> cache = new ConcurrentHashMap<>();

    public boolean isUnmodifiable( Map map ) {

        if( map == null ) return true;

        Class<? extends Map> klass = map.getClass();

        if( ! cache.containsKey(klass) ) {
            Map instance = ClassUtil.createInstance( klass );
            try {
                instance.put( "1", "1" );
                cache.putIfAbsent( klass, Boolean.FALSE );
            } catch( Exception e ) {
                cache.putIfAbsent( klass, Boolean.TRUE );
            }
        }

        return cache.get( klass );

    }

    public boolean isUnmodifiable( Collection collection ) {

        if( collection == null ) return true;

        Class<? extends Collection> klass = collection.getClass();

        if( ! cache.containsKey(klass) ) {
            Collection instance = ClassUtil.createInstance( klass );
            try {
                instance.add( "1" );
                cache.putIfAbsent( klass, Boolean.FALSE );
            } catch( Exception e ) {
                cache.putIfAbsent( klass, Boolean.TRUE );
            }
        }

        return cache.get( klass );

    }

    public boolean isUnmodifiable( Object value ) {

        if( value == null ) return true;

        if( value instanceof Map ) {
            return isUnmodifiable( (Map) value );
        } else if( value instanceof Collection ) {
            return isUnmodifiable( (Collection) value );
        } else {
            return false;
        }

    }


}
