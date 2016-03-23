package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nybatis.core.model.NDate;

import java.io.IOException;
import java.util.Date;

public class SimpleDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize( final Date value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( new NDate(value).toString(NDate.DEFAULT_OUTPUT_FORMAT ), generator );
    }

}
