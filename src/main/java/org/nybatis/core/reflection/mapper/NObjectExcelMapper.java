package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.nybatis.core.file.annotation.ExcelReadAnnotationInspector;
import org.nybatis.core.file.annotation.ExcelWriteAnnotationInspector;
import org.nybatis.core.reflection.deserializer.SimpleDateDeserializer;
import org.nybatis.core.reflection.serializer.simple.SimpleDateSerializer;

import java.util.Date;


public class NObjectExcelMapper extends NObjectMapper {

	public NObjectExcelMapper( boolean sort ) {
		init( sort );
		setDefaultFilter();
		setCustomDeserializer();
		setCustomAnnotationIntrospectors();
	}

	@Override
	protected void setCustomDeserializer() {

		SimpleModule module = new SimpleModule( "DateSerializer" );

		module.addSerializer(   Date.class,  new SimpleDateSerializer()   );
		module.addDeserializer( Date.class,  new SimpleDateDeserializer() );

		registerModule( module );

	}

	private void setCustomAnnotationIntrospectors() {
		setAnnotationIntrospectors(
				new ExcelReadAnnotationInspector(),
				new ExcelWriteAnnotationInspector()
		);
	}

}
