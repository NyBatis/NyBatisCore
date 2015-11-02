package org.nybatis.core.xml.nullNode;

import org.nybatis.core.xml.NXml;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class NullNode implements Node {

    public NullNode() {}

    public String getNodeName() {
        return null;
    }

    public String getNodeValue() throws DOMException {
        return null;
    }

    public short getNodeType() {
        return -1;
    }

    public Node getParentNode() {
        return NXml.NULL_NODE;
    }

    public NodeList getChildNodes() {
        return NXml.NULL_NODE_LIST;
    }


    public Node getFirstChild() {
        return NXml.NULL_NODE;
    }


    public Node getLastChild() {
        return NXml.NULL_NODE;
    }


    public Node getPreviousSibling() {
        return NXml.NULL_NODE;
    }


    public Node getNextSibling() {
        return NXml.NULL_NODE;
    }


    public NamedNodeMap getAttributes() {
        return null;
    }


    public Document getOwnerDocument() {
        return NXml.NULL_DOCUMENT;
    }


    public boolean hasChildNodes() {
        return false;
    }


    public Node cloneNode(boolean deep) {
        return NXml.NULL_NODE;
    }


    public void normalize() {}


    public boolean isSupported(String feature, String version) {
        return false;
    }


    public String getNamespaceURI() {
        return null;
    }


    public String getPrefix() {
        return null;
    }

    public String getLocalName() {
        return null;
    }

    public String getBaseURI(){
        return null;
    }

    public boolean hasAttributes() {
        return false;
    }

    public void setNodeValue(String nodeValue) throws DOMException {}


    public Node insertBefore(Node newChild, Node refChild) {
        return NXml.NULL_NODE;
    }


    public Node replaceChild(Node newChild, Node oldChild) {
    	return NXml.NULL_NODE;
    }


    public Node removeChild(Node oldChild) {
    	return NXml.NULL_NODE;
    }

    public Node appendChild( Node newChild ) throws DOMException {
		return NXml.NULL_NODE;
    }

    public void setPrefix( String prefix ) throws DOMException {}

    public short compareDocumentPosition( Node other ) throws DOMException {
	    return 0;
    }

    public String getTextContent() throws DOMException {
	    return null;
    }

    public void setTextContent( String textContent ) throws DOMException {}

    public boolean isSameNode( Node other ) {
    	return other == null || other == NXml.NULL_NODE;
    }

    public String lookupPrefix( String namespaceURI ) {
	    return null;
    }

    public boolean isDefaultNamespace( String namespaceURI ) {
	    return false;
    }

    public String lookupNamespaceURI( String prefix ) {
	    return null;
    }

    public boolean isEqualNode( Node arg ) {
    	return arg == null || arg == NXml.NULL_NODE;
    }

    public Object getFeature( String feature, String version ) {
	    return null;
    }

    public Object setUserData( String key, Object data, UserDataHandler handler ) {
	    return null;
    }

    public Object getUserData( String key ) {
	    return null;
    }


}
