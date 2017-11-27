package org.nybatis.core.reflection.mapper;

import org.nybatis.core.reflection.inspector.ColumnAnnotationInspector;


public class NObjectSqlMapper extends NObjectMapper {

	public NObjectSqlMapper( boolean sort ) {
		init( sort );
		setDefaultFilter();
		setCustomDeserializer();
		setCustomAnnotationIntrospectors();
	}

	private void setCustomAnnotationIntrospectors() {
		setAnnotationIntrospector(
			new ColumnAnnotationInspector()
		);
	}

}
