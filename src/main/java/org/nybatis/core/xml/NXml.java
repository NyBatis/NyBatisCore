package org.nybatis.core.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.xml.node.DocumentHandler;
import org.nybatis.core.xml.node.Node;
import org.nybatis.core.xml.nullNode.NullAttr;
import org.nybatis.core.xml.nullNode.NullDocument;
import org.nybatis.core.xml.nullNode.NullElement;
import org.nybatis.core.xml.nullNode.NullNode;
import org.nybatis.core.xml.nullNode.NullNodeList;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An XML document. Methods allow access to the root element.
 *
 * @author nayasis@gmail.com
 *
 */
public class NXml {

	public static Document         NULL_DOCUMENT  = new NullDocument();
	public static Element          NULL_ELEMENT   = new NullElement();
	public static Attr             NULL_ATTR      = new NullAttr();
	public static org.w3c.dom.Node NULL_NODE      = new NullNode();
	public static NodeList         NULL_NODE_LIST = new NullNodeList();

	private Document doc;

	private int tabSize = DocumentHandler.DEFAULT_TAB_SIZE;

	/**
	 * Create new Document
	 */
	public NXml() {
		createNew();
	}

	/**
	 * Create new Document from file.
	 *
	 * @param file file that has xml contents
	 */
	public NXml( File file ) throws ParseException, UncheckedIOException {
		readFrom( file, true );
	}

	/**
	 * Create new Document from file.
	 *
	 * @param file file that has xml contents
	 * @param ignoreDtd ignore XML's DTD ruleset
	 */
	public NXml( File file, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
		readFrom( file, ignoreDtd );
	}

	/**
	 * Create new Document from file.
	 *
	 * @param path file path that has xml contents
	 */
	public NXml( Path path ) throws ParseException, UncheckedIOException {
		readFrom( path, true );
	}

	/**
	 * Create new Document from file.
	 *
	 * @param path file path that has xml contents
	 * @param ignoreDtd ignore XML's DTD ruleset
	 */
	public NXml( Path path, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
		readFrom( path, ignoreDtd );
	}

	/**
	 * Create new document from XML string.
	 *
	 * @param xml XML string
	 */
	public NXml( String xml ) throws ParseException, UncheckedIOException {
		readFrom( xml, true );
	}

	/**
	 * Create new document from XML string.
	 *
	 * @param xml XML string
	 * @param ignoreDtd ignore XML's DTD ruleset
	 */
	public NXml( String xml, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
		readFrom( xml, ignoreDtd );
	}

	/**
	 * Get document's root node. <br>
	 *
	 * If root node is not exist, It returns null node.
	 *
	 * @return document's root node
	 */
	public Node getRoot() {
		return new Node( doc.getDocumentElement() ).setTabSize( tabSize );
	}

	/**
	 * Create root element.
	 *
	 * @param tagName tag name of root element node.
	 * @return root node
	 */
	public Node createRoot( String tagName ) {

		if( doc.getDocumentElement() != null ) {
			doc.removeChild( doc.getDocumentElement() );
		}

		doc.appendChild( doc.createElement( tagName ) );

		return getRoot();

	}

	/**
	 * Rename root element's tag name.
	 *
	 * @param tagName root element's tag name to rename
	 * @return root node
	 */
	public Node renameRoot( String tagName ) {

		Element root = doc.getDocumentElement();

		if( root == null ) {
			return createRoot( tagName );

		} else {
			doc.renameNode( root, root.getNamespaceURI(), tagName );
			return getRoot();
		}

	}

	/**
	 * Get first childNode from root using xpath expression.
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
		return getRoot().getChildNode( xpath ).setTabSize( tabSize );
	}

	/**
	 * Get childNodes from root using xpath expression.
	 *
	 * <table summary="expressiion" border="1" style="border-collapse:collapse; border:1px gray solid;">
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
	 * @return Node List
	 */
	public List<Node> getChildNodes( String xpath ) {
		return getRoot().getChildNodes( xpath );
	}

	/**
	 * Get all child nodes from root
	 *
	 * @return all child nodes
	 */
	public List<Node> getChildNodes() {
		return getRoot().getChildNodes();
	}

	/**
	 * Get element nodes from root
	 *
	 * @param tagName elements' tag name
	 *                &lt;<font style="color:red">name</font>&gt;&lt;/<font style="color:red">name</font>&gt;
	 *                &lt;<font style="color:red">job</font>&gt;&lt;/<font style="color:red">job</font>&gt;
	 * @return all element nodes having specific tag name
	 */
	public List<Node> getChildElements( String tagName ) {
		return getRoot().getChildElements( tagName );
	}

	/**
	 * Get one child elements from root
	 * @param tagName elements' tag name
	 *                &lt;<font style="color:red">name</font>&gt;&lt;/<font style="color:red">name</font>&gt;
	 *                &lt;<font style="color:red">job</font>&gt;&lt;/<font style="color:red">job</font>&gt;
	 * @return one element node having specific tag name
	 */
	public Node getChildElement( String tagName ) {
		return getRoot().getChildElement( tagName );
	}

	/**
	 * Get all child elements from root
	 *
	 * @return child elements
	 */
	public Node getChildNode() {
		return getRoot().getChildNode();
	}

	/**
	 * Get child elements from root
	 *
	 * @param id elements' id
	 *                &lt;name id="<font style="color:red">AAA</font>"&gt;&lt;/name&gt;
	 *                &lt;job  id="<font style="color:red">BBB</font>"&gt;&lt;/name&gt;
	 * @return all elements having specific id
	 */
	public List<Node> getChildNodesById( String id ) {
		return getRoot().getChildNodesById( id );
	}

	/**
	 * Get one child element from root
	 *
	 * @param id elements' id
	 *                &lt;name id="<font style="color:red">AAA</font>"&gt;&lt;/name&gt;
	 *                &lt;job  id="<font style="color:red">BBB</font>"&gt;&lt;/name&gt;
	 * @return one element having specific id
	 */
	public Node getChildNodeById( String id ) {
		return getRoot().getChildNodeById( id );
	}

	/**
	 * Remove all child nodes from root
	 */
	public void removeChildNodes() {
		getRoot().removeChild();
	}

	/**
	 * Create element
	 *
	 * @param tagName element's tag name
	 * @return created element
	 */
	public Node createElement( String tagName ) {
		return newNode( doc.createElement( tagName ) );
	}

	/**
	 * Create element from XML string
	 *
	 * @param xml XML string
	 * @return created element
	 */
	public Node createElementFrom( String xml ) {
		return newNode( new DocumentHandler().readXml( xml, true ).getDocumentElement() );
	}

	/**
	 * Create comment node
	 *
	 * @param comment comment node's contents
	 * @return created comment node
	 */
	public Node createComment( String comment ) {
		return newNode( doc.createComment( comment ) );
	}

	/**
	 * Create text node
	 *
	 * @param text text node's contents
	 * @return created text node
	 */
	public Node createText( String text ) {
		return newNode( doc.createTextNode( text ) );
	}

	/**
	 * Add child node to root
	 *
	 * @param node child node to add
	 * @return self instance
	 */
	public NXml addChild( Node node ) {
		getRoot().addChild( node );
		return this;
	}

	private Node newNode( org.w3c.dom.Node node ) {
		return new Node( node ).setTabSize( tabSize );
	}

	/**
	 * Get tab size for print
	 *
	 * @return tab size
	 */
	public int getTabSize() {
		return tabSize;
	}

	/**
	 * Set tab size for print
	 *
	 * @param tabSize tabSize
	 * @return self instance
	 */
	public NXml setTabSize( int tabSize ) {
		this.tabSize = tabSize;
		return this;
	}

	/**
	 * Set doctype
	 *
	 * @param publicId The external subset public identifier.
	 * @param systemId The external subset system identifier.
	 * @return self instance
	 * @throws ParseException xml parsing exception
	 */
	public NXml setDocType( String publicId, String systemId ) throws ParseException {

		DocumentType docType = doc.getDoctype();

		if( doc.getDocumentElement() == null ) {
			throw new ParseException( "XML without root element is not possible to set DOCTYPE." );
		}

		String qulifiedName = doc.getDocumentElement().getNodeName();

		if( docType != null ) {
			doc.removeChild( docType );
		}

		DOMImplementation domImpl = doc.getImplementation();
		docType = domImpl.createDocumentType( qulifiedName, publicId, systemId );
		doc.appendChild( docType );

		return this;

	}

	/**
	 * Returns a XML string
	 *
	 * @return XML string
	 */
	public String toString() {
		return toString( true );
	}

	/**
	 * Returns a XML string
	 *
	 * @param prettyFormat whether or not applying pretty format
	 * @return XML string
	 */
    public String toString( boolean prettyFormat ) {
    	return new DocumentHandler().toString( doc, tabSize, prettyFormat, true );
    }


	/**
	 * Create New XML instance
	 */
	private void createNew() {
		doc = new DocumentHandler().getBuilder( true ).newDocument();
		doc.setXmlStandalone( true );
	}

	/**
	 * Read XML from file
	 *
	 * @param path 		file path to read
	 * @param ignoreDtd ignore DTD
	 * @return self instance
	 * @throws ParseException 		XML parsing exception
	 * @throws UncheckedIOException IO exception occurs
	 */
    public NXml readFrom( Path path, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
    	return readFrom( path.toFile(), ignoreDtd );
    }

	/**
	 * Read XML from string
	 *
	 * @param xml 		XML string to read
	 * @param ignoreDtd ignore DTD
	 * @return self instance
	 * @throws ParseException	XML parsing exception
	 */
    public NXml readFrom( String xml, boolean ignoreDtd ) throws ParseException {
    	doc = new DocumentHandler().readXml( xml, ignoreDtd );
    	return this;
    }

	/**
	 * Read XML from file
	 *
	 * @param file 		file to read
	 * @param ignoreDtd ignore DTD
	 * @return self instance
	 * @throws ParseException 		XML parsing exception
	 * @throws UncheckedIOException	I/O exception
	 */
	public NXml readFrom( File file, boolean ignoreDtd ) throws ParseException, UncheckedIOException {

		try {
	        readFrom( new FileInputStream( file ), ignoreDtd );
	        return this;
        } catch( FileNotFoundException e ) {
	        throw new UncheckedIOException( e );
        } catch( UncheckedIOException e ) {
			throw e;
		} catch( ParseException e ) {
			throw new ParseException( String.format( "%s on file[%s].", e.getMessage(), file ), e );
		}

	}

	/**
	 * Read XML from stream
	 *
	 * @param inputStream	input stream to read
	 * @param ignoreDtd		ignore DTD
	 * @return self instance
	 * @throws ParseException		XML parsing exception
	 * @throws UncheckedIOException	I/O exception
	 */
	public NXml readFrom( InputStream inputStream, boolean ignoreDtd ) throws ParseException, UncheckedIOException {
		doc = new DocumentHandler().readXml( inputStream, ignoreDtd );
		return this;
	}

	/**
	 * Write XML to file
	 *
	 * @param file 			file to write
	 * @param prettyFormat	whether or not applying pretty format
	 * @throws UncheckedIOException I/O exception
	 */
	public void writeTo( File file, boolean prettyFormat ) throws UncheckedIOException {
		Assertion.isExists( file, "File[{}] is not exists", file );
		FileUtil.writeTo( file, toString( prettyFormat ) );
	}

	/**
	 * Write XML to file
	 *
	 * @param filePath		file path to write
	 * @param prettyFormat	whether or not applying pretty format
	 * @throws UncheckedIOException	I/O exception
	 */
	public void writeTo( String filePath, boolean prettyFormat ) throws UncheckedIOException {
		writeTo( new File( filePath ), prettyFormat );
	}

	/**
	 * Clone object
	 *
	 * @return cloned object
	 */
	public NXml clone() {

		NXml clone = new NXml();

		clone.doc = (Document) doc.cloneNode( true );
		clone.tabSize = tabSize;

		return clone;

	}

}
