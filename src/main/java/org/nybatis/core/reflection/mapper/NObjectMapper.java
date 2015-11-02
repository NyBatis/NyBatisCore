package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.nybatis.core.model.NDate;


public class NObjectMapper extends ObjectMapper {

	public NObjectMapper() {

		configure( JsonParser.Feature.ALLOW_SINGLE_QUOTES, true ); // 문자열 구분기호를 " 뿐만 아니라 ' 도 허용
		configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false ); // Bean 이 null 일 경우 허용
		configure( SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true ); // char 배열을 문자로 강제 변환하지 않는다.
		configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false ); // 대상객체에 매핑할 field가 없을 경우도 허용
		configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false );
		configure( MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true ); // private 변수라도 강제로 매핑

		registerCustomDeserializer();

	}

	private void registerCustomDeserializer() {

		SimpleModule module = new SimpleModule( "NDateDeserializer" );

		module.addDeserializer( NDate.class, new NDateDeserializer() );

		registerModule( module );

	}

}
