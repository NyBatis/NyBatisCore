package org.nybatis.core.reflection.serializer.customNullChecker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nybatis.core.db.constant.NullValue;
import org.nybatis.core.model.NDate;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends AbstractJsonSerializer<Date> {

    public DateSerializer( JsonSerializer defaultSerializer ) {
        super( defaultSerializer );
    }

    @Override
    public boolean isNull( Date value ) throws IOException {
        return value == NullValue.DATE;
    }

    @Override
    public void serializeNotNullValue( JsonSerializer serializer, Date value, JsonGenerator generator, SerializerProvider provider ) throws IOException, JsonProcessingException {
        provider.defaultSerializeValue( new NDate( value ).toString( NDate.ISO_8601_24H_FULL_FORMAT ), generator );
    }
}
