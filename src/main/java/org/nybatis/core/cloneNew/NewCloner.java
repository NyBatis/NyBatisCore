package org.nybatis.core.cloneNew;

import org.nybatis.core.clone.CoreReflector;
import org.nybatis.core.cloneNew.interfaces.DeepCloner;
import org.nybatis.core.cloneNew.processor.FastCloners;
import org.nybatis.core.cloneNew.processor.IgnorableChecker;
import org.nybatis.core.cloneNew.processor.ImmutableChecker;
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

    private CoreReflector reflector;

    public NewCloner(){
        this.reflector = new CoreReflector();
    }

    public NewCloner( CoreReflector reflector ) {
        this.reflector = reflector;
    }

    public <T> T cloneObject( T object, Map valueReference ) throws InvalidAccessException {

        if( object == null || object instanceof NewCloner ) return null;
        if( object instanceof Enum ) return object;
        if( ignorableChecker.isIgnorable(object) ) return null;
        if( immutableChecker.isImmutable(object) ) return object;

        if( valueReference != null && valueReference.containsKey(object) ) {
            return (T) valueReference.get( object );
        }

        Class klass = object.getClass();

        DeepCloner cloner = fastCloners.getCloner( klass );
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

            if( Modifier.isStatic(field.getModifiers()) ) continue;
            if( Modifier.isTransient(field.getModifiers()) ) continue;
            if( isAnonymousClass(field) ) continue;
            if( field.isEnumConstant() ) continue;
            if( ignorableChecker.isIgnorable(field) ) continue;

            Object originalValue = reflector.getFieldValue( bean, field );

            if( field.isSynthetic() || immutableChecker.isImmutable(field) || immutableChecker.isImmutable(originalValue) ) {
                reflector.setField( newBean, field, originalValue );
            } else {
                Object copiedValue = cloneObject( originalValue, valueReference );
                reflector.setField( newBean, field, copiedValue );
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

}
