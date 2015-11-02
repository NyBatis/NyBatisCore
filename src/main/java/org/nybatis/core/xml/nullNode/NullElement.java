package org.nybatis.core.xml.nullNode;

import org.nybatis.core.xml.NXml;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public class NullElement extends NullNode implements Element {

	@Override
    public String getTagName() {
	    return null;
    }

	@Override
    public String getAttribute( String name ) {
	    return null;
    }

	@Override
    public void setAttribute( String name, String value ) throws DOMException {
    }

	@Override
    public void removeAttribute( String name ) throws DOMException {
    }

	@Override
    public Attr getAttributeNode( String name ) {
	    return NXml.NULL_ATTR;
    }

	@Override
    public Attr setAttributeNode( Attr newAttr ) throws DOMException {
	    return NXml.NULL_ATTR;
    }

	@Override
    public Attr removeAttributeNode( Attr oldAttr ) throws DOMException {
	    return NXml.NULL_ATTR;
    }

	@Override
    public NodeList getElementsByTagName( String name ) {
	    return NXml.NULL_NODE_LIST;
    }

	@Override
    public String getAttributeNS( String namespaceURI, String localName ) throws DOMException {
	    return null;
    }

	@Override
    public void setAttributeNS( String namespaceURI, String qualifiedName, String value ) throws DOMException {
    }

	@Override
    public void removeAttributeNS( String namespaceURI, String localName ) throws DOMException {
    }

	@Override
    public Attr getAttributeNodeNS( String namespaceURI, String localName ) throws DOMException {
	    return null;
    }

	@Override
    public Attr setAttributeNodeNS( Attr newAttr ) throws DOMException {
	    return NXml.NULL_ATTR;
    }

	@Override
    public NodeList getElementsByTagNameNS( String namespaceURI, String localName ) throws DOMException {
	    return NXml.NULL_NODE_LIST;
    }

	@Override
    public boolean hasAttribute( String name ) {
	    return false;
    }

	@Override
    public boolean hasAttributeNS( String namespaceURI, String localName ) throws DOMException {
	    return false;
    }

	@Override
    public TypeInfo getSchemaTypeInfo() {
	    return null;
    }

	@Override
    public void setIdAttribute( String name, boolean isId ) throws DOMException {
    }

	@Override
    public void setIdAttributeNS( String namespaceURI, String localName, boolean isId ) throws DOMException {
    }

	@Override
    public void setIdAttributeNode( Attr idAttr, boolean isId ) throws DOMException {
    }

}
