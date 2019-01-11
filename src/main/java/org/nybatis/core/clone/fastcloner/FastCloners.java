package org.nybatis.core.clone.fastcloner;

import org.nybatis.core.clone.fastcloner.implement.*;
import org.nybatis.core.clone.fastcloner.interfaces.DeepCloner;
import org.nybatis.core.util.ClassUtil;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Cloner repository
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class FastCloners {

    private Map<Class,DeepCloner> cloners     = new HashMap<>();
    private ArrayCloner arrayCloner = new ArrayCloner();

    public FastCloners() {

        CalanderCloner calanderCloner   = new CalanderCloner();
        ListCloner listCloner       = new ListCloner();
        CollectionCloner collectionCloner = new CollectionCloner();
        DateCloner dateCloner       = new DateCloner();
        MapCloner mapCloner        = new MapCloner();
        SetCloner setCloner        = new SetCloner();

        add( GregorianCalendar.class,   calanderCloner   );
        add( Date.class,                dateCloner       );
        add( ArrayList.class,           listCloner       );
        add( LinkedList.class,          listCloner       );
        add( Vector.class,              listCloner       );
        add( HashSet.class,             setCloner        );
        add( TreeSet.class,             setCloner        );
        add( HashMap.class,             mapCloner        );
        add( Hashtable.class,           mapCloner        );
        add( TreeMap.class,             mapCloner        );
        add( LinkedBlockingQueue.class, collectionCloner );
        add( PriorityQueue.class,       collectionCloner );
        add( ArrayDeque.class,          collectionCloner );

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
        if( object.getClass().isArray() ) return arrayCloner;
        return getCloner( object.getClass() );
    }

}
