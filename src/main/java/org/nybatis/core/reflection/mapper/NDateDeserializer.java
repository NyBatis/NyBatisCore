package org.nybatis.core.reflection.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.nybatis.core.model.NDate;

public class NDateDeserializer extends JsonDeserializer<NDate> {

	@Override
    public NDate deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		long value = jp.getLongValue();
		return new NDate( value );
    }

}
