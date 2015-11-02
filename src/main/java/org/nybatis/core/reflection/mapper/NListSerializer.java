package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nybatis.core.model.NList;

import java.io.IOException;

public class NListSerializer extends JsonSerializer<NList> {

    @Override
    public void serialize( final NList value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( value.toList(), generator );
    }

}
