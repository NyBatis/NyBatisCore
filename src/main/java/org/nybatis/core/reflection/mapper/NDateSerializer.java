package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nybatis.core.model.NDate;

import java.io.IOException;

public class NDateSerializer extends JsonSerializer<NDate> {

    @Override
    public void serialize( final NDate value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( value.toString(), generator );
    }

}
