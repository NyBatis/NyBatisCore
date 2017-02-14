package org.nybatis.core.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.nybatis.core.model.NDate;

import java.io.IOException;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

	@Override
    public Date deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {

		try {

			long value = jp.getLongValue();
			return new Date( value );

		} catch( JsonParseException e ) {

			String value = jp.getValueAsString();

			// if value is null (even empty string), it is a different data structure and not a single value.
			if( value == null ) {
				throw e;
			}

			return new NDate( value, NDate.ISO_8601_24H_FULL_FORMAT ).toDate();

		}

    }

}
