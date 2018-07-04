package org.nybatis.core.xml.node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nybatis.core.xml.NXml;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;


public class Node {

	private int tabSize = DocumentHandler.DEFAULT_TAB_SIZE;

	private static final Node NULL = new Node();

	private org.w3c.dom.Node node = null;

	public Node() {
		node = NXml.NULL_ELEMENT;
	}

	public Node( org.w3c.dom.Node node ) {
		this.node = initNode( node );
	}

	public Node( Element element ) {
		this.node = initNode( element );
	}

	private org.w3c.dom.Node getRealNode() {
		return node;
	}

	//@Override
    public String getName() {
		return node.getNodeName();
    }

	//@Override
    public String getValue() throws DOMException {
    	String val = node.getNodeValue();
    	if( val == null ) val = node.getTextContent();
    	return val;
    }

	//@Override
    public Node setValue( String value ) throws DOMException {
		node.setNodeValue( value );
		return this;
    }

	//@Override
    public short getType() {
	    return node.getNodeType();
    }

	private org.w3c.dom.Node initNode( org.w3c.dom.Node node ) {

		if( node == null || node.getNodeType() == -1 ) return NXml.NULL_ELEMENT;

		return node;

//		if( node instanceof Element  ) return (Element) node;
//		if( node instanceof Document ) return (Element) node;
//
//		return initNode( node.getParentNode() );

//		switch( node.getNodeType() ) {
//			case -1 : return NXml.NULL_ElEMENT;
//			case ELEMENT_NODE  : return (Element) node;
//			case DOCUMENT_NODE : return (Element) node;
//
//		      case ATTRIBUTE_NODE:              return "Attribute";
//		      case TEXT_NODE:                   return "Text";
//
//		      case DOCUMENT_TYPE_NODE:          return "Document type";
//		      case ENTITY_NODE:                 return "Entity";
//		      case ENTITY_REFERENCE_NODE:       return "Entity reference";
//		      case NOTATION_NODE:               return "Notation";
//		      case COMMENT_NODE:                return "Comment";
//		      case CDATA_SECTION_NODE:          return "CDATA Section";
//		      case PROCESSING_INSTRUCTION_NODE: return "Attribute";
//			default :
//				return getElementNode( node.getParentNode() );
//
//		}

	}

	//@Override
    public Node getParentNode() {
		return newNode( node.getParentNode() );
    }

	//@Override
    public List<Node> getChildNodes() {
		return getChildNodesFrom( node.getChildNodes() );
    }

    public Node getChildElement() {
    	return getFirstElementFrom( rawElement().getChildNodes() );
    }

    public Node getChildElementLast() {
    	return getLastElementFrom( rawElement().getChildNodes() );
    }

    public List<Node> getChildElements() {
		return toElementNodesFrom( rawElement().getChildNodes() );
    }

    private List<Node> getChildNodesFrom( NodeList nodeList ) {

		List<Node> result = new ArrayList<>();

		if( nodeList == null ) return result;

		for( int i = 0, iCnt = nodeList.getLength(); i < iCnt; i++ ) {
			result.add( newNode(nodeList.item(i)) );
		}

		return result;

    }

    private Node getFirstChildFrom( NodeList nodeList ) {

    	if( nodeList == null ) return NULL;

    	int size = nodeList.getLength();

    	if( size == 0 ) return NULL;

    	return newNode( nodeList.item(0) );

    }

    private List<Node> toElementNodesFrom( NodeList nodeList ) {

    	List<Node> result = new ArrayList<>();

    	if( nodeList == null ) return result;

    	for( int i = 0, iCnt = nodeList.getLength(); i < iCnt; i++ ) {

    		org.w3c.dom.Node node = nodeList.item(i);

    		if( node instanceof Element ) result.add( newNode(node) );
    	}

    	return result;

    }

    private Node getFirstElementFrom( NodeList nodeList ) {

    	if( nodeList == null ) return NULL;

    	for( int i = 0, iCnt = nodeList.getLength(); i < iCnt; i++ ) {

    		org.w3c.dom.Node node = nodeList.item(i);

    		if( node instanceof Element ) {
    			return newNode(node);
    		}
    	}

    	return NULL;

    }

    private Node getLastElementFrom( NodeList nodeList ) {

    	if( nodeList == null ) return NULL;

    	for( int i = nodeList.getLength() - 1; i >= 0; i-- ) {

    		org.w3c.dom.Node node = nodeList.item(i);

    		if( node instanceof Element ) {
    			return newNode(node);
    		}
    	}

    	return NULL;

    }

    private Node newNode( org.w3c.dom.Node node ) {
		return new Node( node ).setTabSize( tabSize );
	}

	private Node newNode() {
		return new Node().setTabSize( tabSize );
	}

    public Node getChildNode() {
    	return newNode( node.getFirstChild() );
    }

	//@Override
    public Node getChildNodeLast() {
    	return newNode( node.getLastChild() );
    }

	//@Override
    public Node getNodePrevious() {
    	return newNode( node.getPreviousSibling() );
    }

	//@Override
    public Node getNodeNext() {
    	return newNode( node.getNextSibling() );
    }

	//@Override
    public Map<String, String> getAttrs() {

    	Map<String, String> result = new LinkedHashMap<>();

    	NamedNodeMap attributes = node.getAttributes();

    	if( attributes == null ) return result;

    	for( int i = 0, iCnt = attributes.getLength(); i < iCnt; i++ ) {
    		Attr attr = (Attr) attributes.item( i );
    		result.put( attr.getNodeName(), attr.getNodeValue() );
    	}

	    return result;

    }

	//@Override
    public Node ownerDocument() {
    	return newNode( node.getOwnerDocument() );
    }

	//@Override
    public Node addChild( Node newChildToInsert, Node refChildBeforeInserted ) throws DOMException {
    	if( isNull() ) return newNode();

    	org.w3c.dom.Node newNode = ( newChildToInsert == null ) ? null : newChildToInsert.getRealNode();
    	org.w3c.dom.Node refNode = ( refChildBeforeInserted == null ) ? null : refChildBeforeInserted.getRealNode();

    	return newNode( node.insertBefore( newNode, refNode ) );

    }

	//@Override
    public Node replaceChild( Node newChild, Node oldChild ) throws DOMException {
    	if( isNull() ) return newNode();
    	org.w3c.dom.Node newNode = ( newChild == null ) ? null : newChild.getRealNode();
    	org.w3c.dom.Node oldNode = ( oldChild == null ) ? null : oldChild.getRealNode();
    	return newNode( node.replaceChild( newNode, oldNode ) );
    }

	//@Override
    public Node removeChild( Node oldChild ) throws DOMException {
    	if( isNull() ) return newNode();
    	org.w3c.dom.Node oldNode = ( oldChild == null ) ? null : oldChild.getRealNode();
    	return newNode( node.removeChild(oldNode) );
    }

    public Node removeChild() {
    	NodeList childNodes = node.getChildNodes();
    	for( int i = 0, iCnt = childNodes.getLength(); i < iCnt; i++ ) {
    		node.removeChild( childNodes.item(i) );
    	}
		return this;
    }

	//@Override
    public Node addChild( Node newChild ) throws DOMException {
    	if( isNull() ) return newNode();
    	org.w3c.dom.Node newNode = ( newChild == null ) ? null : newChild.getRealNode();
    	return newNode( node.appendChild(newNode) );
    }

    public Node addElement( String tagName ) {
    	if( isNull() ) return newNode();
    	return newNode( new DocumentHandler().addElement(node, tagName) );
    }

    public Node addElementFromXml( String xml ) {
    	if( isNull() ) return newNode();
    	return newNode( new DocumentHandler().addElementFrom(node, xml) );
    }

    public Node addComment( String comment ) {
    	if( isNull() ) return newNode();
    	return newNode( new DocumentHandler().addComment(node, comment) );
    }

    public Node addText( String text ) {
    	if( isNull() ) return newNode();
    	return newNode( new DocumentHandler().addText(node, text) );
    }

    public Node rename( String tagName ) {
    	if( ! isNull() ) new DocumentHandler().renameNode( node, tagName );
    	return this;
    }

	//@Override
    public boolean hasChildren() {
    	return node.hasChildNodes();
    }

    public int getChildSize() {
    	return hasChildren() ?  node.getChildNodes().getLength() : 0;
    }

	//@Override
    public Node clone( boolean deep ) {
    	return newNode( node.cloneNode( deep ) );
    }

	//@Override
    public Node normalize() {
    	node.normalize();
    	return this;
    }

	//@Override
    public boolean isSupported( String feature, String version ) {
	    return node.isSupported( feature, version );
    }

	//@Override
    public String getNamespaceURI() {
    	return node.getNamespaceURI();
    }

	//@Override
    public String getPrefix() {
    	return node.getPrefix();
    }

	//@Override
    public Node setPrefix( String prefix ) throws DOMException {
    	node.setPrefix( prefix );
		return this;

    }

	//@Override
    public String getLocalName() {
    	return node.getLocalName();
    }

	//@Override
    public boolean hasAttr() {
	    return node.hasAttributes();
    }

	//@Override
    public String getBaseURI() {
    	return node.getBaseURI();
    }

	//@Override
    public short comparePosition( Node other ) throws DOMException {
	    return node.compareDocumentPosition( other.getRealNode() );
    }

	//@Override
    public String getText() throws DOMException {
	    return node.getTextContent();
    }

	//@Override
    public Node setText( String textContent ) throws DOMException {
    	node.setTextContent( textContent );
		return this;
    }

	//@Override
    public String lookupPrefix( String namespaceURI ) {
	    return node.lookupPrefix( namespaceURI );
    }

	//@Override
    public boolean isDefaultNamespace( String namespaceURI ) {
    	return node.isDefaultNamespace( namespaceURI );
    }

	//@Override
    public String lookupNamespaceURI( String prefix ) {
    	return node.lookupNamespaceURI( prefix );
    }

	//@Override
    public boolean isSame( Node other ) {
	    return node.isSameNode( other.getRealNode() );
    }

	//@Override
    public boolean isEqual( Node arg ) {
    	return node.isEqualNode( arg.getRealNode() );
    }

	//@Override
    public Object getFeature( String feature, String version ) {
    	return node.getFeature( feature, version );
    }

//	//@Override
//    public Object setUserData( String key, Object data, UserDataHandler handler ) {
//    	return node.setUserData( key, data, handler );
//    }
//
//	//@Override
//    public Object getUserData( String key ) {
//    	return node.getUserData( key );
//    }

//	//@Override
//    public String getTagName() {
//    	return node.getTagName();
//    }

	//@Override

    public String getAttrIgnoreCase( String key ) {

    	Map<String, String> attributes = getAttrs();

    	for( String k : attributes.keySet() ) {
    		if( k.equalsIgnoreCase(key) ) {
    			return attributes.get( k );
    		}
    	}

    	return null;
    }

    public String getAttr( String key ) {
    	return rawElement().getAttribute( key );
    }

    public boolean hasAttr( String key ) {
    	return getAttrs().containsKey( key );
    }

    public boolean hasAttrIgnoreCase( String key ) {
    	return getAttrIgnoreCase( key ) != null;
    }

	//@Override
    public Node setAttr( String key, String value ) throws DOMException {
    	rawElement().setAttribute( key, value );
    	return this;
    }

	//@Override
    public Node removeAttr( String key ) throws DOMException {
    	rawElement().removeAttribute( key );
		return this;
    }

	//@Override
    public List<Node> getChildElements( String tagName ) {
    	return getChildNodesFrom( rawElement().getElementsByTagName( tagName ) );
    }

    public Node getChildElement( String tagName ) {
    	return getFirstChildFrom( rawElement().getElementsByTagName(tagName) );
    }

	//@Override
    public String getAttrNS( String namespaceURI, String localName ) throws DOMException {
	    return rawElement().getAttributeNS( namespaceURI, localName );
    }

	//@Override
    public Node setAttrNS( String namespaceURI, String qualifiedName, String value ) throws DOMException {
    	rawElement().setAttributeNS( namespaceURI, qualifiedName, value );
		return this;
    }

	//@Override
    public Node removeAttrNS( String namespaceURI, String localName ) throws DOMException {
    	rawElement().removeAttributeNS( namespaceURI, localName );
		return this;
    }

	//@Override
    public List<Node> getChildNodesByNS( String namespaceURI, String localName ) throws DOMException {
    	return getChildNodesFrom( rawElement().getElementsByTagNameNS( namespaceURI, localName ) );
    }

    public Node getChildNodeByNS( String namespaceURI, String localName ) throws DOMException {
    	NodeList nodeList = rawElement().getElementsByTagNameNS( namespaceURI, localName );
    	return nodeList.getLength() == 0 ? newNode() : newNode( nodeList.item(0) ) ;
    }

	//@Override
    public boolean hasAttrNS( String namespaceURI, String localName ) throws DOMException {
    	return rawElement().hasAttributeNS( namespaceURI, localName );
    }

	//@Override
    public TypeInfo getSchemaTypeInfo() {
    	return rawElement().getSchemaTypeInfo();
    }

    // This is unique method

	//@Override
    public List<Node> getChildNodesById( String id ) {
	    return getChildNodes( String.format(".//*[@id='%s']", id)  );
    }

    public Node getChildNodeById( String id ) {
    	return getChildNode( String.format(".//*[@id='%s']", id)  );
    }

	/**
	 * Get node using xpath expression.
	 *
	 * <table summary="expression" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <th>Expression</th>
	 *    <th>Description</th>
	 *  </tr>
	 *  <tr>
	 *    <td>nodename</td>
	 *    <td>Selects all nodes have [nodename]</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/</td>
	 *    <td>Selects from the root node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//</td>
	 *    <td>Selects from the root node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.</td>
	 *    <td>Selects the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>..</td>
	 *    <td>Selects the parent of the current node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>./</td>
	 *    <td>Selects from the current node to 1 depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//</td>
	 *    <td>Selects from the current node to infinite depth</td>
	 *  </tr>
	 *  <tr>
	 *    <td>@</td>
	 *    <td>Selects attributes</td>
	 *  </tr>
	 *  </table>
	 *
	 *  <p>Example</p>
	 *
	 *  <table summary="example" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *  <tr>
	 *    <td>Path</td>
	 *    <td>Result</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employee</td>
	 *    <td>Selects all nodes with the name “employee”</td>
	 *  </tr>
	 *  <tr>
	 *    <td>employees/employee</td>
	 *    <td>Selects all employee elements that are children of employees</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee</td>
	 *    <td>Selects all book elements no matter where they are in the document</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[1]</td>
	 *    <td>Selects the first employee element that is the child of the employees element.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()]</td>
	 *    <td>Selects the last employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>/employees/employee[last()-1]</td>
	 *    <td>Selects the last but one employee element that is the child of the employees element</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//employee[@type='admin']</td>
	 *    <td>Selects all the employee elements that have an attribute named type with a value of 'admin'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//*[@id='c2']</td>
	 *    <td>Selects all elements that have an attribute named id with a value of 'c2'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[local-name()='granada']</td>
	 *    <td>Selects all elements that their tag name equals 'granada'</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' for example 'SelectName', 'PeriodName' IloveNameInSpace' and so on.</td>
	 *  </tr>
	 *  <tr>
	 *    <td>.//*[contains(local-name(),'Name'][local-name() != 'SurrName']</td>
	 *    <td>Selects all elements that their tag name contains 'Name' and not equals to 'SurrName'</td>
	 *  </tr>
	 *  </table>
	 *
	 * @param xpath xPath Expression
	 * @return Node
	 */
	public Node getChildNode( String xpath ) {
		return newNode( new DocumentHandler().getNode( node, xpath ) );
	}

	public List<Node> getChildNodes( String xpath ) {
		if( getChildSize() == 0 ) return new ArrayList<>();
		return getChildNodesFrom( new DocumentHandler().getNodes( node, xpath ) );
	}

	public int getTabSize() {
		return tabSize;
	}

	public Node setTabSize( int tabSize ) {
		this.tabSize = tabSize;
		return this;
	}


	public String toString( boolean prettyFormat ) {
		return new DocumentHandler().toString( node, tabSize, prettyFormat, true );
	}

	public String toString() {
		return toString( true );
	}

	public String toTagString() {
		return String.format( "<%s%s/>", getName(), getAttrString() );
	}

	private String getAttrString() {

		Map<String, String> attributes = getAttrs();

		if( attributes.isEmpty() ) return "";

		StringBuilder sb = new StringBuilder();

		for( String key : attributes.keySet() ) {
			sb.append( String.format(" %s=\"%s\"", key, attributes.get(key)) );
		}

		return sb.toString();

	}

	public String toInnerString( boolean prettyFormat ) {
		return new DocumentHandler().toString( node, tabSize, prettyFormat, false );
	}

	public String toInnerString() {
		return toInnerString( true );

	}

    private Element rawElement() {

    	if( node == null ) return NXml.NULL_ELEMENT;

    	if( !( node instanceof Element ) ) {
    		throw new DOMException( DOMException.NOT_SUPPORTED_ERR, String.format("This(%s)is not element node.", node) );
    	}

    	return (Element) node;

    }

	public String getXpath() {
    	return getXpath( this );
    }

    private String getXpath( Node node ) {

    	if( node == null || node.getType() == -1 ) return "";

    	Node parent = node.getParentNode();

    	if( parent.getRealNode() != NXml.NULL_ELEMENT ) {
    		return String.format( "%s/%s", getXpath( parent ), node.getName() );
    	} else {
    		return "";
    	}

    }

    public boolean isNull() {
    	return node == null || node.getNodeType() == -1;
    }

    public boolean isDocument() {
    	return NodeType.findBy( getType() ) == NodeType.DOCUMENT_NODE;
    }

    public boolean isElement() {
    	NodeType nodeType = NodeType.findBy( getType() ) ;
    	return nodeType == NodeType.DOCUMENT_NODE || nodeType == NodeType.ELEMENT_NODE;
    }

    public boolean isText() {
    	return NodeType.findBy( getType() ) == NodeType.TEXT_NODE;
    }

    public boolean isComment() {
    	return NodeType.findBy( getType() ) == NodeType.COMMENT_NODE;
    }

    public boolean isAttribute() {
    	return NodeType.findBy( getType() ) == NodeType.ATTRIBUTE_NODE;
    }

}
