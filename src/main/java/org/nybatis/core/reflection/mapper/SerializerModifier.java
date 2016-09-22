package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.serializer.customNullChecker.ArraySerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.BigDecimalSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.BooleanSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.DateSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.DoubleSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.FloatSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.IntegerSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.ListSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.LongSerializer;
import org.nybatis.core.reflection.serializer.customNullChecker.SetSerializer;
import org.nybatis.core.util.ClassUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * Custom Serializer modifier
 *
 * @author nayasis@gmail.com
 * @since 2016-09-21
 */
public class SerializerModifier extends BeanSerializerModifier {

    @Override
    public JsonSerializer<?> modifySerializer( SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer ) {

        Class<?> klass = beanDesc.getBeanClass();

        NLogger.debug( klass );

        if( klass == Boolean.class ) {
            return new BooleanSerializer( serializer );
        } else if( klass == Integer.class ) {
            return new IntegerSerializer( serializer );
        } else if( klass == Long.class ) {
            return new LongSerializer( serializer );
        } else if( klass == Float.class ) {
            return new FloatSerializer( serializer );
        } else if( klass == Double.class ) {
            return new DoubleSerializer( serializer );
        } else if( klass == BigDecimal.class ) {
            return new BigDecimalSerializer( serializer );
        } else if( ClassUtil.isExtendedBy( klass, Date.class ) ) {
            return new DateSerializer( serializer );
        }

        return serializer;

    }

    public JsonSerializer<?> modifyArraySerializer( SerializationConfig config, ArrayType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer ) {
        return new ArraySerializer( serializer );
    }

    public JsonSerializer<?> modifyCollectionSerializer( SerializationConfig config, CollectionType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer ) {

        Class<?> klass = beanDesc.getBeanClass();

        if( ClassUtil.isExtendedBy(klass, Set.class) ) {
            return new SetSerializer( serializer );
        } else {
            return new ListSerializer( serializer );
        }

    }

}


