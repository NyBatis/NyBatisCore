package org.nybatis.core.reflection.serializer.simple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.nybatis.core.db.constant.NullValue;
import org.nybatis.core.model.NList;

public class SimpleNListSerializer extends JsonSerializer<NList> {

    @Override
    public void serialize( final NList value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        if( value == NullValue.NLIST ) {
            provider.defaultSerializeValue( NullValue.LIST, generator );
        } else {
            provider.defaultSerializeValue( value.toList(), generator );
        }
    }

}
