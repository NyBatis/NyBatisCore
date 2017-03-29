package org.nybatis.core.clone;

import org.nybatis.core.clone.processor.FastCloners;
import org.nybatis.core.clone.processor.IgnorableChecker;
import org.nybatis.core.clone.processor.ImmutableChecker;
import org.nybatis.core.exception.unchecked.InvalidArgumentException;
import org.nybatis.core.util.ClassUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class NewCopier {

//    private Map<Object, Object> valueReference = new IdentityHashMap<>();

    private ImmutableChecker immutableChecker = new ImmutableChecker();
    private IgnorableChecker ignorableChecker = new IgnorableChecker();
    private FastCloners      fastCloners      = new FastCloners();
    private CoreReflector    reflector        = null;

    public NewCopier(){
        this.reflector = new CoreReflector();
    }

    public NewCopier( CoreReflector reflector ) {
        this.reflector = reflector;
    }

    public ImmutableChecker getImmutableChecker() {
        return immutableChecker;
    }

    public void copyObject( Object source, Object target ) throws InvalidArgumentException {

        if( source == null || target == null ) return;

        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();

        boolean isSourceArray = sourceClass.isArray();
        boolean isTargetArray = targetClass.isArray();

        if( isSourceArray ) {
            if( ! isTargetArray ) return;

            int length = Math.min( Array.getLength( source ), Array.getLength( target ) );

            for( int i = 0; i < length; i++ ) {

                Object sourceValue = Array.get( source, i );
                Object targetValue = Array.get( target, i );

                if( sourceValue == targetValue ) continue;

                if( immutableChecker.isImmutable(sourceValue) ) {
                    Array.set( target, i, sourceValue );
                } else {

                    copyBean( sourceValue, targetValue );
                    Array.set( target, i, targetValue );

                }

            }

        } else {

        }

//        throw new InvalidArgumentException( "Can not copy from array({}) to non-array({}).", sourceClass, targetClass );


    }

    public void copyBean( Object source, Object target ) throws InvalidArgumentException {

        if( source == null || target == null ) return;

        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();

        boolean isSourceArray = sourceClass.isArray();
        boolean isTargetArray = targetClass.isArray();

        if( isSourceArray ) {
            if( ! isTargetArray ) return;

            int length = Math.min( Array.getLength( source ), Array.getLength( target ) );

            for( int i = 0; i < length; i++ ) {

                Object sourceValue = Array.get( source, i );
                Object targetValue = Array.get( target, i );

                if( sourceValue == targetValue ) continue;

                if( immutableChecker.isImmutable(sourceValue) ) {
                    Array.set( target, i, sourceValue );
                } else {

                    copyBean( sourceValue, targetValue );
                    Array.set( target, i, targetValue );

                }

            }

        } else {

            if( ClassUtil.isExtendedBy(targetClass, sourceClass) || ClassUtil.isExtendedBy(sourceClass, targetClass) ) {

                Set<Field> sourceFields = reflector.getFields( sourceClass );
                Set<Field> targetFields = reflector.getFields( targetClass );

                for( Field field : sourceFields ) {

                    if( isExclusive( field ) ) continue;
                    if( ! targetFields.contains(field) ) continue;

                    Object sourceValue = reflector.getFieldValue( source, field );
                    Object targetValue = reflector.getFieldValue( target, field );

                    if( sourceValue == targetValue ) continue;

                    if( sourceValue == null ) {
                        reflector.setField( target, field, null );
                    } else if( targetValue == null ) {
                        reflector.setField( target, field, sourceValue );
                    } else {

                        if( field.isSynthetic() || immutableChecker.isImmutable(field) || immutableChecker.isImmutable(sourceValue) ) {
                            reflector.setField( target, field, sourceValue );
                        } else {
                            copyObject( sourceValue, targetValue );
                            reflector.setField( target, field, targetValue );
                        }

                    }

                }

            }

        }

    }

    private boolean isExclusive( Field field ) {
        if( Modifier.isStatic(field.getModifiers()) ) return true;
        if( isAnonymousClass(field) ) return true;
        if( field.isEnumConstant() ) return true;
        if( ignorableChecker.isIgnorable(field) ) return true;
        return false;
    }

    private boolean isAnonymousClass( final Field field ) {
        return "this$0".equals(field.getName());
    }

}
