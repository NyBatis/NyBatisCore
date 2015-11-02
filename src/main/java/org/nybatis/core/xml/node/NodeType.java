package org.nybatis.core.xml.node;

import java.util.HashMap;
import java.util.Map;

public enum NodeType {

	ELEMENT_NODE( org.w3c.dom.Node.DOCUMENT_NODE, "Element" )
  , DOCUMENT_NODE( org.w3c.dom.Node.ELEMENT_NODE, "Document" )
  , ATTRIBUTE_NODE( org.w3c.dom.Node.ATTRIBUTE_NODE, "Attribute" )
  , TEXT_NODE( org.w3c.dom.Node.TEXT_NODE, "Text" )
  , DOCUMENT_TYPE_NODE( org.w3c.dom.Node.DOCUMENT_TYPE_NODE, "DocType" )
  , ENTITY_NODE( org.w3c.dom.Node.ENTITY_NODE, "Entity" )
  , ENTITY_REFERENCE_NODE( org.w3c.dom.Node.ENTITY_REFERENCE_NODE, "Entity reference" )
  , NOTATION_NODE( org.w3c.dom.Node.NOTATION_NODE, "Notation" )
  , COMMENT_NODE( org.w3c.dom.Node.COMMENT_NODE, "Comment" )
  , CDATA_SECTION_NODE( org.w3c.dom.Node.CDATA_SECTION_NODE, "CDATA" )
  , PROCESSING_INSTRUCTION_NODE( org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE, "Attribute" )
	;

	private short  code;
	private String name;
	private static Map<Short, NodeType> map = new HashMap<>();

	static {
		for( NodeType type : NodeType.values() ) {
			map.put( type.code, type );
		}
	}

	NodeType( short code, String name ) {
		this.code = code;
		this.name = name;
	}

	public static NodeType findBy( short code ) {
		return map.get( code );
	}

	public String toString() {
		return name;
	}

}