package org.nybatis.core.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;
import org.nybatis.core.model.NDate;
import org.nybatis.core.util.StringUtil;

import static org.nybatis.core.util.StringUtil.toYn;

public class ColumnBooleanDeserializer extends JsonDeserializer<Boolean> {

	@Override
    public Boolean deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		try {
			String val = jp.getValueAsString();
			return StringUtil.toBoolean( val );
		} catch( JsonParseException e ) {
			return jp.getBooleanValue();
		}
    }

}
