package org.nybatis.core.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.mapper.NObjectSqlMapper;

public class ColumnBeanDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

	private Annotation[] annotations;
	private Class        fieldClass;
	private Class        typeClass;

	private TypeBase     typeBase;

	@Override
	public Object deserialize( JsonParser p, DeserializationContext ctxt ) throws IOException, JsonProcessingException {

		ObjectMapper objectMapper = (ObjectMapper) p.getCodec();
//		TypeFactory typeFactory = mapper.getTypeFactory();
		String json = p.getValueAsString();
//
//		objectMapper.getTypeFactory().con.constructCollectionType(List.class, typeClass)

		CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType( List.class, typeClass );

		objectMapper.readValue( json, typeBase );

//		p.readValueAs( (TypeReference) typeBase );
//
//		p.readValueAs( typeBase );

		if( json == null ) {
			if( typeClass != null ) {
				List list = new ArrayList<>();
				Iterator iterator = p.readValuesAs( typeClass );
				while( iterator.hasNext() ) {
					list.add( iterator.next() );
				}
				return list;
			} else {
				return p.readValueAs( fieldClass );
			}
		} else {
			if( typeClass != null ) {
				return Reflector.toListFromJson( json, typeClass );
			} else {
				return Reflector.toBeanFrom( json, fieldClass );
			}
		}
	}

	@Override
	public JsonDeserializer<?> createContextual( DeserializationContext ctxt, BeanProperty property ) throws JsonMappingException {

		TypeFactory typeFactory = ctxt.getTypeFactory();

		JavaType type = property.getType();

		if( type.isMapLikeType() ) {
			typeBase = typeFactory.constructMapLikeType( type.getRawClass(), type.getKeyType(), type.getContentType() );
		} else if( type.isCollectionLikeType() ) {
			typeBase = typeFactory.constructCollectionType( (Class<? extends Collection>) type.getRawClass(), type.getContentType() );
		} else if( type.isArrayType() ) {
			typeBase = typeFactory.constructArrayType( type );
		}

		JavaType contentType = property.getType().getContentType();
		if( contentType != null ) {
			typeClass = contentType.getRawClass();
			if( typeClass == Object.class ) {
				typeClass = null;
			}
		}
		fieldClass = property.getType().getRawClass();
		annotations = property.getType().getRawClass().getAnnotations();
		return this;
	}

	/**
	 * return objectMapper.readValue( getArrayContent(jsonString), objectMapper.getTypeFactory().constructCollectionType(List.class, typeClass) );
	 */

}
