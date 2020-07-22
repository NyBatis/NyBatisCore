package org.nybatis.core.xml.reader;

import java.io.File;

import org.nybatis.core.db.sql.sqlMaker.QuotChecker;
import org.nybatis.core.file.FileUtil;

public class DeformedXmlReader {

	private boolean onCData = false;

	public String readFrom( String xmlString ) {
		return toXml( xmlString );
	}

    public String readFrom( File file ) {
		return toXml( FileUtil.readFrom( file ) );
	}

    private String toXml( String deformedXml ) {

    	StringBuilder xml  = new StringBuilder();

    	StringBuilder node = null;

    	int xmlLength = deformedXml.length();

        for( int i = 0, iCnt = xmlLength - 1; i < iCnt; i++ ) {

            char currC = deformedXml.charAt( i );
            char nextC = deformedXml.charAt( i + 1 );

            checkCData( currC, nextC, deformedXml, i, iCnt );

            if( node != null ) {

            	if( onCData ) {
            		node.append( currC ); continue;
            	}

				if( currC == '&' && nextC == '&' ) {
					node.append( "&amp;&amp;" ); i++; continue;
				}

            	node.append( currC );

            	if( currC == '>' ) {
            		xml.append( convertNodeString(node) );
            		node = null;
            	}


            } else {

            	if( onCData ) {
            		xml.append( currC ); continue;
            	}

				if( currC == '&' && nextC == '&' ) {
					xml.append( "&amp;&amp;" ); i++; continue;
				}

                if( currC != '<' || nextC == '/' || nextC == '!' || nextC == '?' ) {
                	xml.append( currC ); continue;
                }

                if( isLessThan(nextC) ) {
                	xml.append( "&lt;" ); continue;
                }

                node = new StringBuilder();
                node.append( currC );

            }


        }

        xml.append( deformedXml.charAt(xmlLength - 1) );

//        NLogger.debug( "--------------------------------" );
//        NLogger.debug( xml );

    	return xml.toString();

    }

    private void checkCData( char currCh, char nextCh, String deformedXml, int currentIndex, int lastIndex ) {

    	if( onCData ) {

    		if( currCh != ']' ) return;
    		if( nextCh != ']' ) return;

    		String word = deformedXml.substring( currentIndex, Math.min(currentIndex + 3, lastIndex) );

    		if( word.equalsIgnoreCase( "]]>" ) ) {
    			onCData = false;
    		}

    	} else {

    		if( currCh != '<' ) return;
    		if( nextCh != '!' ) return;

    		String word = deformedXml.substring( currentIndex, Math.min(currentIndex + 9, lastIndex) );

    		if( word.equalsIgnoreCase( "<![CDATA[" ) ) {
    			onCData = true;
    		}

    	}

    }

    private String convertNodeString( StringBuilder nodeString ) {

    	StringBuilder node  = new StringBuilder();
    	StringBuilder token = new StringBuilder();

    	QuotChecker checker = new QuotChecker();

    	for( int i = 0, iCnt = nodeString.length() - 1; i < iCnt; i++ ) {

    		char currCh = nodeString.charAt( i );

    		checker.check( currCh );

        	if( checker.isIn() ) {
        		token.append( currCh ); continue;
        	}

    		if( currCh == ' ' || currCh == '\t' || currCh == '\n' || currCh == '\r' ) {
    			node.append( getNodeFragment(token) ).append( currCh );
    			token = new StringBuilder();
    		} else {
    			token.append( currCh );
    		}

    	}

    	if( token.length() > 0 ) {
    		node.append( getNodeFragment(token) );
    	}

    	node.append( '>' );

    	return node.toString();

    }

    private String getNodeFragment( StringBuilder token ) {

    	if( token.length() == 0 ) return "";

		String tokenToWork = token.toString();

		if( tokenToWork.startsWith("<") ) return tokenToWork;
		if( tokenToWork.startsWith("/") ) return tokenToWork;

		if( ! tokenToWork.contains("=") ) return token.append( "=\"\"" ).toString();

		return tokenToWork.replaceAll( "<", "&lt;" );

    }

	private boolean isLessThan( char c ) {
		if( 65 <= c && c <= 90  ) return false; // a ~ z
		return !( 97 <= c && c <= 122 );
	}

}
