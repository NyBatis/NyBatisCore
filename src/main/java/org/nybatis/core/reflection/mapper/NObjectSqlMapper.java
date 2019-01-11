package org.nybatis.core.reflection.mapper;

import org.nybatis.core.reflection.inspector.ColumnAnnotationInspector;


public class NObjectSqlMapper extends NObjectMapper {

	public NObjectSqlMapper() {
		super();
		addCustomAnnotationIntrospectors();
	}

	private void addCustomAnnotationIntrospectors() {
		setAnnotationIntrospector(
			new ColumnAnnotationInspector()
		);
	}

}
