package org.nybatis.core.reflection.core;

import org.nybatis.core.clone.Cloner;
import org.nybatis.core.exception.unchecked.InvalidArgumentException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.validation.Validator;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Map merge util
 *
 * @author nayasis@gmail.com
 * @since 2017-03-30
 */
public class BeanMerger {

    /**
     * Merge bean contents.<br><br>
     *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @return merged bean
     */
    public <T> T merge( Object source, T target ) {
        return merge( source, target, true );
    }

    /**
     * Merge bean contents.<br><br>
     *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @param skipEmpty if false, skip merging when source value is null. if true, skip merging when source vale is null or empty.
     * @return merged bean
     */
    public <T> T merge( Object source, T target, boolean skipEmpty ) {

        if( Validator.isEmpty(source) ) return target;
        if( target == null ) {
            throw new InvalidArgumentException( "can not merge from source({}) to null.", source.getClass() );
        }

        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();

        if( isArrayOrCollection(source) ^ isArrayOrCollection(target) ) {
            throw new InvalidArgumentException( "can not merge array to non-array (source:{}, target:{})", sourceClass, targetClass );
        }

        if( isAssignable(sourceClass, targetClass) ) {
            new Cloner().copy( source, target );
            return target;
        }

        if( isMap(source) && isMap(target) ) {
            return (T) merge( (Map) source, (Map) target, skipEmpty );
        } else if( isCollection(source) && isCollection(target) ) {
            return (T) merge( (Collection) source, (Collection) target, skipEmpty );
        } else if( isArrayOrCollection(source) && isArrayOrCollection(target) ) {
            return (T) mergeArray( source, target, skipEmpty );
        }

        Map sourceMap = new NMap( Reflector.toMapFrom(source) ).rebuildKeyForJsonPath();
        Map targetMap = new NMap( Reflector.toMapFrom(target) ).rebuildKeyForJsonPath();
        targetMap = merge( sourceMap, targetMap );

        Object mergedBean = Reflector.toBeanFrom( targetMap, targetClass );

        new Cloner().copy( mergedBean, target );
        return target;

    }

    /**
     * Merge map contents.<br><br>
     *
     * It supposes that map contains no Pojo and only be consist with primitive value, Map and collection ( like JSON structure )
     *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @return merged object
     */
    public Map merge( Map source, Map target ) {
        return merge( source, target, true );
    }

    /**
     * Merge map contents.<br><br>
     *
     * It supposes that map contains no Pojo and only be consist with primitive value, Map and collection ( like JSON structure )
     *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @param skipEmpty if false, skip merging when source value is null. if true, skip merging when source vale is null or empty.
     * @return merged object
     */
    public Map merge( Map source, Map target, boolean skipEmpty ) {

        if( Validator.isEmpty(source) ) return target;
        if( Validator.isEmpty(target) ) return source;

        for( Object key : source.keySet() ) {

            Object sourceVal = source.get( key );
            Object targetVal = target.get( key );

            if( isSkippable(sourceVal, skipEmpty) ) continue;

            if( ! target.containsKey(key) || targetVal == null ) {
                target.put( key, sourceVal );

            } else if( isMap(sourceVal) && isMap(targetVal) ) {
                Map merged = merge( (Map) sourceVal, (Map) targetVal, skipEmpty );
                target.put( key, merged );

            } else if( isCollection(sourceVal) && isCollection(targetVal) ) {
                Collection merged = merge( (Collection) sourceVal, (Collection) targetVal, skipEmpty );
                target.put( key, merged );

            } else if( isArrayOrCollection(sourceVal) && isArrayOrCollection(targetVal) ) {
                Object merged = mergeArray( sourceVal, targetVal, skipEmpty );
                target.put( key, merged );

            } else {
                target.put( key, sourceVal );
            }

        }

        return target;

    }

    /**
     * Merge collection contents.<br><br>
     *
     * It supposes that map contains no Pojo and only be consist with primitive value, Map and collection ( like JSON structure )
     *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @return merged collection
     */
    public <T extends Collection> T merge( Collection source, T target ) {
        return merge( source, target, true );
    }

    /**
     * Merge collection contents.<br><br>
     *
     * It supposes that map contains no Pojo and only be consist with primitive value, Map and collection ( like JSON structure )
     *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @param skipEmpty if false, skip merging when source value is null. if true, skip merging when source vale is null or empty.
     * @return merged collection
     */
    public <T extends Collection> T merge( Collection source, T target, boolean skipEmpty ) {

        if( Validator.isEmpty(source) ) return target;

        if( target == null ) {
            return (T) source;
        }

        Iterator sourceIterator = source.iterator();
        Iterator targetIterator = target.iterator();

        T result = (T) ClassUtil.createInstance( target.getClass() );

        while( sourceIterator.hasNext() || targetIterator.hasNext() ) {

            boolean noElementInTarget = false;
            boolean noElementInSource = false;

            Object sourceVal = null;
            Object targetVal = null;

            try {
                sourceVal = sourceIterator.next();
            } catch( Exception e ) {
                noElementInSource = true;
            }
            try {
                targetVal = targetIterator.next();
            } catch( NoSuchElementException e ) {
                noElementInTarget = true;
            }

            if( noElementInSource || isSkippable(sourceVal, skipEmpty) ) {
                result.add( targetVal );
                continue;
            }
            if( noElementInTarget ) {
                result.add( sourceVal );
                continue;
            }

            if( targetVal == null ) {
                result.add( sourceVal );
            } else if( isMap(sourceVal) && isMap(targetVal) ) {
                Map merged = merge( (Map) sourceVal, (Map) targetVal, skipEmpty );
                result.add( merged );
            } else if( isCollection(sourceVal) && isCollection(targetVal) ) {
                Collection merged = merge( (Collection) sourceVal, (Collection) targetVal, skipEmpty );
                result.add( merged );
            } else if( isArrayOrCollection(sourceVal) && isArrayOrCollection(targetVal) ) {
                Object merged = mergeArray( sourceVal, targetVal, skipEmpty );
                result.add( merged );
            } else {
                result.add( sourceVal );
            }

        }

        return result;

    }

    private Object mergeArray( Object source, Object target, boolean skipEmpty ) {
        Collection merged = merge( toCollection(source), toCollection(target), skipEmpty );
        if( isArray(target) ) {
            Object array = Array.newInstance( target.getClass().getComponentType(), merged.size() );
            Iterator iterator = merged.iterator();
            int i = 0;
            while( iterator.hasNext() ) {
                Array.set( array, i++, iterator.next() );
            }
            return array;
        } else {
            return merged;
        }
    }


    private boolean isSkippable( Object value, boolean skipEmpty ) {
        if( skipEmpty ) {
            if( Validator.isEmpty(value) ) return true;
        } else {
            if( Validator.isNull(value) ) return true;
        }
        return false;
    }

    private boolean isMap( Object value ) {
        return value != null && value instanceof Map;
    }

    private boolean isArrayOrCollection( Object value ) {
        return isCollection( value ) || isArray( value );
    }

    private boolean isCollection( Object value ) {
        return value != null && value instanceof Collection;
    }

    private boolean isArray( Object value ) {
        return value != null && value.getClass().isArray();
    }

    private boolean isAssignable( Class sourceClass, Class targetClass ) {
        return ClassUtil.isExtendedBy(targetClass, sourceClass) || ClassUtil.isExtendedBy(sourceClass, targetClass);
    }


    private Collection toCollection( Object value ) {
        if( isCollection(value) ) return (Collection) value;
        if( isArray(value) ) {
            List converted = new ArrayList();
            int length = Array.getLength( value );
            for( int i = 0; i < length; i++ ) {
                converted.add( Array.get( value, i ) );
            }
            return converted;
        }
        return new ArrayList();
    }
}
