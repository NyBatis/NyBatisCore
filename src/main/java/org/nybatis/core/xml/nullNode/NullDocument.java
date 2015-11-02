package org.nybatis.core.xml.nullNode;

import org.nybatis.core.xml.NXml;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class NullDocument extends NullNode implements Document {

	@Override
    public DocumentType getDoctype() {
	    return null;
    }

	@Override
    public DOMImplementation getImplementation() {
	    return null;
    }

	@Override
    public Element getDocumentElement() {
	    return NXml.NULL_ELEMENT;
    }

	@Override
    public Element createElement( String tagName ) throws DOMException {
	    return NXml.NULL_ELEMENT;
    }

	@Override
    public DocumentFragment createDocumentFragment() {
	    return null;
    }

	@Override
    public Text createTextNode( String data ) {
	    return null;
    }

	@Override
    public Comment createComment( String data ) {
	    return null;
    }

	@Override
    public CDATASection createCDATASection( String data ) throws DOMException {
	    return null;
    }

	@Override
    public ProcessingInstruction createProcessingInstruction( String target, String data ) throws DOMException {
	    return null;
    }

	@Override
    public Attr createAttribute( String name ) throws DOMException {
	    return NXml.NULL_ATTR;
    }

	@Override
    public EntityReference createEntityReference( String name ) throws DOMException {
	    return null;
    }

	@Override
    public NodeList getElementsByTagName( String tagname ) {
	    return NXml.NULL_NODE_LIST;
    }

	@Override
    public Node importNode( Node importedNode, boolean deep ) throws DOMException {
	    return NXml.NULL_NODE;
    }

	@Override
    public Element createElementNS( String namespaceURI, String qualifiedName ) throws DOMException {
	    return NXml.NULL_ELEMENT;
    }

	@Override
    public Attr createAttributeNS( String namespaceURI, String qualifiedName ) throws DOMException {
	    return NXml.NULL_ATTR;
    }

	@Override
    public NodeList getElementsByTagNameNS( String namespaceURI, String localName ) {
	    return NXml.NULL_NODE_LIST;
    }

	@Override
    public Element getElementById( String elementId ) {
	    return NXml.NULL_ELEMENT;
    }

	@Override
    public String getInputEncoding() {
	    return null;
    }

	@Override
    public String getXmlEncoding() {
	    return null;
    }

	@Override
    public boolean getXmlStandalone() {
	    return false;
    }

	@Override
    public void setXmlStandalone( boolean xmlStandalone ) throws DOMException {
    }

	@Override
    public String getXmlVersion() {
	    return null;
    }

	@Override
    public void setXmlVersion( String xmlVersion ) throws DOMException {
    }

	@Override
    public boolean getStrictErrorChecking() {
	    return false;
    }

	@Override
    public void setStrictErrorChecking( boolean strictErrorChecking ) {
    }

	@Override
    public String getDocumentURI() {
	    return null;
    }

	@Override
    public void setDocumentURI( String documentURI ) {
    }

	@Override
    public Node adoptNode( Node source ) throws DOMException {
	    return NXml.NULL_NODE;
    }

	@Override
    public DOMConfiguration getDomConfig() {
	    return null;
    }

	@Override
    public void normalizeDocument() {
    }

	@Override
    public Node renameNode( Node n, String namespaceURI, String qualifiedName ) throws DOMException {
	    return NXml.NULL_NODE;
    }

}
