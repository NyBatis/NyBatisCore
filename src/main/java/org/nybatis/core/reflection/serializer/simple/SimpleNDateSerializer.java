package org.nybatis.core.reflection.serializer.simple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nybatis.core.db.constant.NullValue;
import org.nybatis.core.model.NDate;
import org.nybatis.core.model.NList;

import java.io.IOException;

public class SimpleNDateSerializer extends JsonSerializer<NDate> {

    @Override
    public void serialize( final NDate value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {

        if( value == NullValue.NDATE ) {
            provider.defaultSerializeValue( NullValue.DATE, generator );
        } else {
            provider.defaultSerializeValue( value.toDate(), generator );
        }

    }

}
