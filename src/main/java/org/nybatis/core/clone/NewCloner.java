package org.nybatis.core.clone;

import org.nybatis.core.clone.module.clone.interfaces.DeepCloner;
import org.nybatis.core.clone.processor.FastCloners;
import org.nybatis.core.clone.processor.IgnorableChecker;
import org.nybatis.core.clone.processor.ImmutableChecker;
import org.nybatis.core.exception.unchecked.CloningException;
import org.nybatis.core.exception.unchecked.InvalidAccessException;
import org.nybatis.core.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class NewCloner {

//    private Map<Object, Object> valueReference = new IdentityHashMap<>();

    private ImmutableChecker immutableChecker = new ImmutableChecker();
    private IgnorableChecker ignorableChecker = new IgnorableChecker();
    private FastCloners      fastCloners      = new FastCloners();
    private CoreReflector    reflector        = null;

    public NewCloner(){
        this.reflector = new CoreReflector();
    }

    public NewCloner( CoreReflector reflector ) {
        this.reflector = reflector;
    }

    public <T> T deepClone( T object ) throws CloningException {
        if( object == null ) return null;
        Map valueReference = new IdentityHashMap<>();
        try {
            return cloneObject( object, valueReference );
        } catch( InvalidAccessException e ) {
            throw new CloningException( "error on cloning object : {}", object, e );
        }
    }

    public <T> T shallowClone( T object ) throws CloningException {
        if( object == null ) return null;
        try {
            return cloneObject( object, null );
        } catch( InvalidAccessException e ) {
            throw new CloningException( "error on cloning object : {}", object, e );
        }
    }

    public <T> T cloneObject( T object, Map valueReference ) throws InvalidAccessException {

        if( object == null || object instanceof NewCloner ) return null;
        if( object instanceof Enum ) return object;
        if( ignorableChecker.isIgnorable(object) ) return null;
        if( immutableChecker.isImmutable(object) ) return object;

        if( valueReference != null && valueReference.containsKey(object) ) {
            return (T) valueReference.get( object );
        }

        DeepCloner cloner = fastCloners.getCloner( object );
        if( cloner != null ) {
            Object copiedValue = cloner.clone( object, this, valueReference );
            if( valueReference != null ) {
                valueReference.put( object, copiedValue );
            }
            return (T) copiedValue;
        }

        return cloneBean( object, valueReference );

    }

    private <T> T cloneBean( T bean, Map valueReference ) throws InvalidAccessException {

        if( bean == null ) return null;

        Class<?> klass = bean.getClass();

        T newBean = (T) ClassUtil.createInstance( klass );

        if( valueReference != null ) {
            valueReference.put( bean, newBean );
        }

        Set<Field> fields = reflector.getFields( klass );

        for( Field field : fields ) {

            if( isExclusive(field) ) continue;

            Object originalValue = reflector.getFieldValue( bean, field );

            if( field.isSynthetic() || immutableChecker.isImmutable(field) || immutableChecker.isImmutable(originalValue) ) {
                reflector.setField( newBean, field, originalValue );
            } else {
                if( valueReference == null ) {
                    reflector.setField( newBean, field, originalValue );
                } else {
                    Object copiedValue = cloneObject( originalValue, valueReference );
                    reflector.setField( newBean, field, copiedValue );
                }
            }

        }

        return newBean;

    }

    private boolean isAnonymousClass( final Field field ) {
        return "this$0".equals(field.getName());
    }

    public ImmutableChecker getImmutableChecker() {
        return immutableChecker;
    }

    private boolean isExclusive( Field field ) {
        if( Modifier.isStatic(field.getModifiers()) ) return true;
        if( isAnonymousClass(field) ) return true;
        if( field.isEnumConstant() ) return true;
        if( ignorableChecker.isIgnorable(field) ) return true;
        return false;
    }

}
