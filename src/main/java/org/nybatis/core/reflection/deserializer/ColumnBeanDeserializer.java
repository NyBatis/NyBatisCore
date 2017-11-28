package org.nybatis.core.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.nybatis.core.reflection.Reflector;

import java.io.IOException;

public class ColumnBeanDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

	private JavaType javaType;

	@Override
	public Object deserialize( JsonParser p, DeserializationContext ctxt ) throws IOException {
		ObjectMapper objectMapper = Reflector.jsonConverter.getObjectMapper();
		String json = p.getValueAsString();
		if( json == null ) {
			return objectMapper.readValue( p, javaType );
		} else {
			return objectMapper.readValue( json, javaType );
		}
	}

	@Override
	public JsonDeserializer<?> createContextual( DeserializationContext ctxt, BeanProperty property ) throws JsonMappingException {
		javaType = property.getType();
		return this;
	}

}
