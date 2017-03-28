package org.nybatis.core.cloneNew.processor;

import org.nybatis.core.cloneNew.interfaces.DeepCloner;
import org.nybatis.core.cloneNew.module.*;
import org.nybatis.core.util.ClassUtil;

import java.util.*;

/**
 * @author nayasis@onestorecorp.com
 * @since 2017-03-28
 */
public class FastCloners {

    private Map<Class,DeepCloner> cloners = new HashMap<>();

    private ArrayCloner arrayConler = new ArrayCloner();

    public FastCloners() {
        add( Calendar.class, new CalanderCloner() );
        add( List.class,     new ListCloner()     );
        add( Date.class,     new DateCloner()     );
        add( Map.class,      new MapCloner()      );
        add( Set.class,      new SetCloner()      );
    }

    public FastCloners add( Class klass, DeepCloner cloner ) {
        cloners.put( klass, cloner );
        return this;
    }

    public DeepCloner getCloner( Class klass ) {
        if( klass == null ) return null;
        if( ! cloners.containsKey(klass) ) {
            for( Class target : cloners.keySet() ) {
                if( ClassUtil.isExtendedBy(klass, target) ) {
                    DeepCloner cloner = cloners.get( target );
                    cloners.putIfAbsent( klass, cloner );
                    return cloner;
                }
            }
            cloners.putIfAbsent( klass, null );
        }
        return cloners.get( klass );
    }

    public DeepCloner getCloner( Object object ) {
        if( object == null ) return null;
        if( object.getClass().isArray() ) return arrayConler;
        return getCloner( object.getClass() );
    }

}
