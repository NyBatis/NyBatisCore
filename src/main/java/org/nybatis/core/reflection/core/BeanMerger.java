package org.nybatis.core.reflection.core;

import org.nybatis.core.clone.Cloner;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.validation.Validator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

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
    public Object merge( Object source, Object target ) {
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
    public Object merge( Object source, Object target, boolean skipEmpty ) {

        if( Validator.isEmpty(source) ) return target;
        if( Validator.isEmpty(target) ) return source;

        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();

        if( ClassUtil.isExtendedBy(targetClass, sourceClass) || ClassUtil.isExtendedBy(sourceClass, targetClass) ) {
            new Cloner().copy( source, target );
            return target;
        }

        if( source instanceof Map && target instanceof Map ) {
            return merge( (Map) source, (Map) target, skipEmpty );
        } else if( source instanceof Collection && target instanceof Collection ) {
            return merge( (Collection) source, (Collection) target, skipEmpty );
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

            } else if( sourceVal instanceof Map && targetVal instanceof Map ) {
                Map merged = merge( (Map) sourceVal, (Map) targetVal, skipEmpty );
                target.put( key, merged );

            } else if( sourceVal instanceof Collection && targetVal instanceof Collection ) {
                Collection merged = merge( (Collection) sourceVal, (Collection) targetVal, skipEmpty );
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

        Iterator sourceIterator = source.iterator();
        Iterator targetIterator = target.iterator();

        T result = (T) ClassUtil.createInstance( target.getClass() );

        if( Validator.isEmpty(target) ) {
            result.addAll( source );
            return result;
        }

        while( sourceIterator.hasNext() ) {

            boolean noElementInTarget = false;

            Object sourceVal = sourceIterator.next();
            Object targetVal = null;
            try {
                targetVal = targetIterator.next();
            } catch( NoSuchElementException e ) {
                noElementInTarget = true;
            }

            if( noElementInTarget || isSkippable(sourceVal, skipEmpty) ) {
                result.add( sourceVal );
                continue;
            }

            if( targetVal == null ) {
                result.add( sourceVal );
            } else if( sourceVal instanceof Map && targetVal instanceof Map ) {
                Map merged = merge( (Map) sourceVal, (Map) targetVal, skipEmpty );
                result.add( merged );
            } else if( sourceVal instanceof Collection && targetVal instanceof Collection ) {
                Collection merged = merge( (Collection) sourceVal, (Collection) targetVal, skipEmpty );
                result.add( merged );
            } else {
                result.add( sourceVal );
            }

        }

        return result;

    }

    private boolean isSkippable( Object value, boolean skipEmpty ) {
        if( skipEmpty ) {
            if( Validator.isEmpty(value) ) return true;
        } else {
            if( Validator.isNull(value) ) return true;
        }
        return false;
    }

}
