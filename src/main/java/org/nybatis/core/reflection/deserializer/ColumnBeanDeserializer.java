package org.nybatis.core.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.lang.annotation.Annotation;
import org.nybatis.core.reflection.Reflector;

public class ColumnBeanDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

	private Annotation[] annotations;
	private Class        fieldClass;

	@Override
	public Object deserialize( JsonParser p, DeserializationContext ctxt ) throws IOException, JsonProcessingException {
		String json = p.getValueAsString();
		if( json == null ) {
			return p.readValueAs( fieldClass );
		} else {
			return Reflector.toBeanFrom( json, fieldClass );
		}
	}

	@Override
	public JsonDeserializer<?> createContextual( DeserializationContext ctxt, BeanProperty property ) throws JsonMappingException {
		fieldClass = property.getType().getRawClass();
		annotations = property.getType().getRawClass().getAnnotations();
		return this;
	}

}
