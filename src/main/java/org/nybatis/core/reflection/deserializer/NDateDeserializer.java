package org.nybatis.core.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.nybatis.core.model.NDate;

public class NDateDeserializer extends JsonDeserializer<NDate> {

	@Override
    public NDate deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		try {
			long value = jp.getLongValue();
			return new NDate( value );
		} catch( JsonParseException e ) {
			String value = jp.getValueAsString();
			return new NDate( value, NDate.ISO_8601_24H_FULL_FORMAT );
		}
    }

}
