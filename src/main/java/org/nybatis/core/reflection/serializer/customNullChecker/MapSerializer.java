package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.nybatis.core.db.constant.NullValue;

import java.io.IOException;
import java.util.Map;

public class MapSerializer extends AbstractJsonSerializer<Map> {

    public MapSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Map value ) throws IOException {
        return value == NullValue.MAP || value == NullValue.NMAP;
    }

//    @Override
//    public void serializeNotNullValue( JsonSerializer serializer, Map value, JsonGenerator generator, SerializerProvider provider ) throws IOException, JsonProcessingException {
//        super.serializeNotNullValue( serializer, value, generator, provider );
//    }
}
