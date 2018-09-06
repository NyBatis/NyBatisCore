package org.nybatis.core.reflection.serializer.column;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import org.nybatis.core.db.constant.NullValue;
import org.nybatis.core.reflection.Reflector;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class ColumnBeanSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize( Object value, JsonGenerator generator, SerializerProvider provider ) throws IOException, JsonProcessingException {
        if( NullValue.isNull(value) ) {
            generator.writeString( NullValue.STRING );
        } else {
            generator.writeString( Reflector.toJson(value) );
        }
    }

    @Override
    public void serializeWithType( Object value, JsonGenerator generator, SerializerProvider serializers, TypeSerializer typeSerializer ) throws IOException {
        if( NullValue.isNull(value) ) {
            generator.writeString( NullValue.STRING );
        } else {
            serialize( value, generator, serializers );
        }
    }

}
