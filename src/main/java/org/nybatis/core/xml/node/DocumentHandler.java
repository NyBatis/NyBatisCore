package org.nybatis.core.xml.node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.exception.unchecked.SyntaxException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DocumentHandler {

	public    static final int    DEFAULT_TAB_SIZE = 4;
	protected static final XPath  xpath = XPathFactory.newInstance().newXPath();

	public DocumentHandler() {}

	/**
	 * Get node using xpath expression.
	 *
	 * <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
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
	 *    <td>Selects from the root node</td>
	 *  </tr>
	 *  <tr>
	 *    <td>//</td>
	 *    <td>Selects nodes in the document from the current node that match the selection no matter where they are</td>
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
	 *  </table>
	 *
	 * @param xPath xPath Expression
	 * @return Node
	 */
	public Node getNode( Node fromNode, String xPath ) {
		try {
	        return (Node) xpath.evaluate( xPath, fromNode, XPathConstants.NODE );
        } catch( XPathExpressionException e ) {
        	throw new SyntaxException( e, e.getMessage() );
        }
	}

	public NodeList getNodes( Node fromNode, String xPath ) {
		try {
			return (NodeList) xpath.evaluate( xPath, fromNode, XPathConstants.NODESET );
		} catch( XPathExpressionException e ) {
			throw new SyntaxException( e, e.getMessage() );
		}
	}

	private void removeWhitespaceNodes( Node node ) {

		NodeList whiteNodes = getNodes( node, "//text()[normalize-space(.)='']" );

	    for( int i = 0, iCnt = whiteNodes.getLength(); i < iCnt; i++ ) {
	    	Node whiteNode = whiteNodes.item( i );
	    	whiteNode.getParentNode().removeChild( whiteNode );
	    }

	}

    public String toString( Node node, int tabSize, boolean prettyFormat, boolean printRootNode ) {

    	if( node == null || node.getNodeType() == -1 ) return "";

    	removeWhitespaceNodes( node );

    	try {

    		TransformerFactory factory = TransformerFactory.newInstance();
    		Transformer transformer = factory.newTransformer();

    		if( prettyFormat ) {
    			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
    			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(tabSize) );
    		}

    		boolean isDocPrint = node instanceof Document;
    		boolean hasDocType = true;

    		if( isDocPrint ) {

    			DocumentType doctype = ((Document)node).getDoctype();

    			if( doctype != null ) {
					try {
						transformer.setOutputProperty( OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId() );
						transformer.setOutputProperty( OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId() );
					} catch( Exception e ) {
						hasDocType = false;
					}
    			} else {
    				hasDocType = false;
    			}

    		} else {
    			transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
    		}


    		DOMSource    source = new DOMSource( node );
    		StreamResult target = new StreamResult( new StringWriter() );

    		transformer.transform( source, target );

    		String toString = target.getWriter().toString();

    		if( ! printRootNode ) {

    			String rootNodeName = node.getNodeName();

    			toString = toString
    					.replaceFirst( "^<" + rootNodeName + ".*?>", "" )
    					.replaceFirst( "</" + rootNodeName + ">$", "" )
    					.replaceFirst( "^(\n|\r)*", "" )
    					.replaceFirst( "(\n|\r)*$", "" )
    					;
    		}

    		if( isDocPrint ) {
    			if( ! hasDocType )
    				toString = toString.replaceFirst( "^<\\?xml(.*?)>", "<?xml$1>\n" );

    		} else {

    			if( ! printRootNode && prettyFormat ) {

    				toString = toString
    						.replaceFirst( "^ {" + tabSize + "}", "" )
    						.replaceAll( "(\n|\r) {" + tabSize + "}", "\n" )
    						;

    			}

    		}

    		return toString;

    	} catch( TransformerException e ) {
    		throw new ParseException( e, e.getMessage() );
    	}

    }

	public DocumentBuilder getBuilder( boolean ignoreDtd ) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

			if( ignoreDtd ) {

//				Doesn't work
//				docFactory.setIgnoringElementContentWhitespace( true );

				docFactory.setFeature( "http://xml.org/sax/features/validation",                         false );
				docFactory.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",  false );
				docFactory.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
			}

			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			return docBuilder;

        } catch( ParserConfigurationException e ) {
        	throw new ParseException( e, e.getMessage() );
        }

	}

	public Node addElementFrom( Node targetNode, String xml ) {

		if( targetNode == null || targetNode.getOwnerDocument() == null ) return null;

		Document currDoc = targetNode.getOwnerDocument();
		Document newDoc  = readXml( xml, true );

		Node     newNode = currDoc.importNode( newDoc.getDocumentElement(), true );

		return targetNode.appendChild( newNode );

	}

	public Node addElement( Node targetNode, String tagName ) {

		if( targetNode == null || targetNode.getOwnerDocument() == null ) return null;

		Document currDoc = targetNode.getOwnerDocument();

		return targetNode.appendChild( currDoc.createElement(tagName) );

	}

	public Node addText( Node targetNode, String text ) {

		if( targetNode == null || targetNode.getOwnerDocument() == null ) return null;

		Document currDoc = targetNode.getOwnerDocument();

		return targetNode.appendChild( currDoc.createTextNode(text) );

	}

	public Node addComment( Node targetNode, String comment ) {

		if( targetNode == null || targetNode.getOwnerDocument() == null ) return null;

		Document currDoc = targetNode.getOwnerDocument();

		return targetNode.appendChild( currDoc.createComment(comment) );

	}

	public Node renameNode( Node targetNode, String tagName ) {

		if( targetNode == null || targetNode.getOwnerDocument() == null ) return null;

		Document currDoc = targetNode.getOwnerDocument();

		return currDoc.renameNode( targetNode, targetNode.getNamespaceURI(), tagName );

	}

    public Document readXml( String xml, boolean ignoreDtd ) {
    	InputStream is = new ByteArrayInputStream( xml.getBytes() );
    	return readXml( is, ignoreDtd );
    }

	public Document readXml( InputStream inputStream, boolean ignoreDtd ) throws ParseException, UncheckedIOException {

		DocumentBuilder builder = getBuilder( ignoreDtd );

		InputStreamReader inputStreamReader = null;

		try {

			inputStreamReader = new InputStreamReader( inputStream, StandardCharsets.UTF_8.toString() );

			Document doc = builder.parse( new InputSource( inputStreamReader ) );

			doc.setXmlStandalone( true );

			return doc;

		} catch( SAXException e ) {

			if( e instanceof SAXParseException ) {

				SAXParseException se = (SAXParseException) e;

				ParseException pe = new ParseException( se, "lineNumber : {}, columnNumber : {}; {}", se.getLineNumber(), se.getColumnNumber(), se.getMessage() );
				pe.setLineNumber( se.getLineNumber() );
				pe.setColumnNumber( se.getColumnNumber() );

				throw pe;

			}

			throw new ParseException( e, e.getMessage() );

        } catch( IOException e ) {
	        throw new UncheckedIOException( e );
        } finally {
        	if( inputStream != null ) try { inputStream.close(); } catch (IOException e) {}
			if( inputStreamReader != null ) try { inputStreamReader.close(); } catch (IOException e) {}
        }

	}

}
