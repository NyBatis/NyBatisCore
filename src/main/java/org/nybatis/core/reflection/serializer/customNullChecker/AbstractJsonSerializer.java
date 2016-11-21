package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;

/**
 * Abstract Json Serializer for Nybatis ORM null value.
 *
 * @author nayasis@gmail.com
 * @since 2016-09-22
 */
public abstract class AbstractJsonSerializer<T> extends JsonSerializer<T> {

    protected final JsonSerializer defaultSerializer;

    public AbstractJsonSerializer( JsonSerializer defaultSerializer ) {
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize( T value, JsonGenerator generator, SerializerProvider provider ) throws IOException, JsonProcessingException {

        if( isNull( value ) ) {
            provider.defaultSerializeValue( NullValue.STRING, generator );
        } else {
            serializeNotNullValue( defaultSerializer, value, generator, provider );
        }

    }

    public void serializeNotNullValue( JsonSerializer serializer, T value, JsonGenerator generator, SerializerProvider provider ) throws IOException, JsonProcessingException {
        serializer.serialize( value, generator, provider );
    }

    public abstract boolean isNull( T value ) throws IOException;


}
