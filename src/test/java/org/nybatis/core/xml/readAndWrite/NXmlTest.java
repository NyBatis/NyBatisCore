package org.nybatis.core.xml.readAndWrite;

import java.io.IOException;
import java.nio.file.Paths;

import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.node.Node;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class NXmlTest {

	@Test
	public void basicParse() {

		NXml xml02 = new NXml( Paths.get(Const.path.getBase(), "/xml/a_test.xml") );

		xml02.setDocType( "MERONG", "http://struts.apache.org/dtds/struts-2.0.dtd" );

		NLogger.debug( xml02 );
		NLogger.debug( "------------------------------" );
		NLogger.debug( xml02.toString(false) );
		NLogger.debug( "------------------------------" );

		NXml xml03 = new NXml( "<tag>\n"
				+ "<nested>hello</nested></tag>" );

//		xml03.setDocType( "-//Apache Software Foundation//DTD Struts Configuration 2.0//EN", "http://struts.apache.org/dtds/struts-2.0.dtd" );

		NLogger.debug( xml03 );
		NLogger.debug( "------------------------------" );
		NLogger.debug( xml03.toString(false) );
		NLogger.debug( "------------------------------" );

	}

	@Test
	public void nodeParse() {

		NXml xml = new NXml( getSampleTreeXml() );

		Node node = xml.getChildNode( "//row" );

		NLogger.debug( node.toString() );
		NLogger.debug( node.toInnerString() );

	}

	@Test
	public void setDocType() {

		NXml fileXmlNoError = new NXml( Paths.get(Const.path.getBase(), "/xml/a_test.xml") );

		fileXmlNoError.setDocType( "MERONG", "http://struts.apache.org/dtds/struts-2.0.dtd" );

		NXml xmlNoError = new NXml( "<tag><nested>hello</nested></tag>" );

		xmlNoError.setDocType( "-//Apache Software Foundation//DTD Struts Configuration 2.0//EN", "http://struts.apache.org/dtds/struts-2.0.dtd" );

		NXml xmlError = new NXml();

		try {
			xmlError.setDocType( "-//Apache Software Foundation//DTD Struts Configuration 2.0//EN", "http://struts.apache.org/dtds/struts-2.0.dtd" );
			fail( "XML without root element is not possible to set DOCTYPE." );
		} catch( ParseException e ) {
			// SUCCESS
		}


	}

	@Test
	public void printContents() {

		NXml xml = new NXml( getSampleSqlXml() );

		Node root = xml.getRoot().setTabSize( 4 );

		NLogger.debug( "root name : {}", root.getName() );

		for( Node node : root.getChildElements( "insert" ) ) {
			NLogger.debug( node );
		}

	}

	@Test
	public void xpathTest() {

		NXml xml = new NXml( getSampleTreeXml() );

		Node node = xml.getChildNode( "//*[@id='c2']" );

		assertEquals( "<col2 id=\"c2\" val=\"val2\">값2</col2>", node.toString().trim() );
		assertEquals( "{id=c2, val=val2}", node.getAttrs().toString() );

		Node root = xml.getRoot();

		root.setTabSize( 2 );

		Node child01 = root.getChildNode( "./row[2]/col2" );
		Node child02 = root.getChildNode( "//col2[2]" );

		assertEquals( "<col2 id=\"c4\">값4</col2>", child01.toString().trim() );
		assertEquals( "c4", child01.getAttr("id") );
		assertEquals( "", child02.toString().trim() );
		assertEquals( null, child02.getAttr("id") );

		// Iterator

		assertEquals( root.getChildElements( "row" ).size(), 3 );

		Node node2 = root.getChildElements( "row" ).get( 0 );

		assertEquals( node2.getChildNodes( "//col" ).size(), 0 );
		assertEquals( node2.getChildNodes( "./row" ).size(), 1 );
		assertEquals( node2.getChildNodes( "./*[contains(local-name(), 'col')]" ).size(), 1 );
		assertEquals( node2.getChildNodes( ".//*[contains(local-name(), 'col')]" ).size(), 2 );

		Node node3 = node2.getChildNodes( "./row" ).get( 0 ).getChildNodes( "./col2" ).get( 0 );

		assertEquals( "col2", node3.getName() );
		assertEquals( "값2",  node3.getText() );
		assertEquals( "값2",  node3.getValue() );
		assertEquals( "c2",   node3.getAttr("id") );
		assertEquals( "val2", node3.getAttr("val") );
		assertEquals( "{id=c2, val=val2}", node3.getAttrs().toString() );

	}


	@Test
	public void loadSql() {

		NXml xml = new NXml( getSampleSqlXml() );

		Node root = xml.getRoot();

		NLogger.debug( root.toTagString() );

		for( Node node : root.getChildNodes() ) {

			if( node.isText() || node.isComment() ) continue;

			NLogger.debug( "\t{}", node.toTagString() );

			if( ! node.hasChildren() ) continue;

			for( Node node2nd : node.getChildNodes() ) {
				if( node2nd.isComment() ) continue;
				NLogger.debug( "\t\t{}", node2nd.toTagString() );
			}

		}

	}

	@Test
	public void makeNode() {

		String nodeString = "<node>merong</node>";

		NXml xml = new NXml();

		xml.getRoot().addElementFromXml( nodeString );
		assertEquals( 0, xml.getRoot().getChildNodes().size() );

		xml.createRoot( "root" );
		xml.getRoot().addElementFromXml( nodeString );
		assertEquals( 1, xml.getRoot().getChildNodes().size() );

		xml.createRoot( "merong" );
		xml.getRoot().addElementFromXml( nodeString );
		assertEquals( 1, xml.getRoot().getChildNodes().size() );

		xml.renameRoot( "merong" );
		xml.getRoot().addElementFromXml( nodeString );
		assertEquals( 2, xml.getRoot().getChildNodes().size() );

	}

	@Test
	public void readXmlTolerantly() throws IOException, ParseException {

		NXmlDeformed xml = new NXmlDeformed( Paths.get( Const.path.getBase(), "/config/db/grammer/Grammer.xml" ) );

		NLogger.debug( xml.toString() );

	}

	private String getSampleTreeXml() {
		return
				"<root>\n" +
				"    <row>\n" +
				"        <col1 id=\"c1\">값1</col1>\n" +
        				"    <row>\n" +
				"                 <col2 id=\"c2\" val=\"val2\">값2</col2>\n" +
        				"    </row>\n" +
				"    </row>\n" +
				"    <row>\n" +
				"        <col1 id=\"c3\">값3</col1>\n" +
				"        <col2 id=\"c4\">값4</col2>\n" +
				"    </row>\n" +
				"</root>";
	}


	private String getSampleSqlXml() {

		return

				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
				"<!DOCTYPE sqlMap PUBLIC \"-//iBATIS.com//DTD SQL Map 2.0//EN\" \"http://www.ibatis.com/dtd/sql-map-2.dtd\">\n" +
				"\n" +
				"<sqlMap namespace=\"Cash\">\n" +
				"\n" +
				"	<insert id=\"chargePoint\" parameterClass=\"java.util.Map\">\n" +
				"	    <!-- Merong Merong -->\n" +
				"		<selectKey keyProperty=\"POINT_ID\" resultClass=\"java.lang.String\">\n" +
				"			<![CDATA[\n" +
				"				SELECT 'PO' || LPAD (SEQ_OD_POINT_HST.NEXTVAL, 18, '0') AS POINT_ID\n" +
				"  				FROM DUAL\n" +
				"			]]>\n" +
				"		</selectKey>  \n" +
				"	  INSERT /* patch_20121120 PurchasePoc, Cash_SqlMap.xml, chargePoint, KimEungjin, 2012-11-12 */\n" +
				"	  INTO TST_OD_POINT_HST\n" +
				"            (POINT_ID\n" +
				"           , SVC_CD\n" +
				"           , ORDER_NO\n" +
				"           , MBR_NO\n" +
				"           , OCCR_DT\n" +
				"           , OCCR_TM\n" +
				"           , EXTN_PLAN_DT\n" +
				"           , EXTN_PLAN_TM\n" +
				"           , OCCR_ST_CD\n" +
				"           , PROC_TYPE\n" +
				"           , OCCR_AMT\n" +
				"           , AVAIL_AMT\n" +
				"            )\n" +
				"     VALUES (#POINT_ID#\n" +
				"           , 'OR003101' /*SVC_CD(Tstore)*/         \n" +
				"           , #PRCHS_ID#\n" +
				"           , #MBR_NO#\n" +
				"           , TO_CHAR(SYSDATE, 'YYYYMMDD')\n" +
				"           , TO_CHAR(SYSDATE, 'HH24MISS')\n" +
				"           <isEqual property=\"CARD_TYPE\" compareValue=\"OR002902\">\n" +
				"           , TO_CHAR(ADD_MONTHS(SYSDATE, 12*5), 'YYYYMMDD')\n" +
				"           , TO_CHAR(ADD_MONTHS(SYSDATE, 12*5), 'HH24MISS')\n" +
				"           </isEqual>\n" +
				"           <isEqual >15 AABB</isEqual>\n" +
				"           <isNotEqual property=\"CARD_TYPE\" compareValue=\"OR002902\">\n" +
				"           , TO_CHAR(ADD_MONTHS(SYSDATE, 12*1), 'YYYYMMDD')\n" +
				"           , TO_CHAR(ADD_MONTHS(SYSDATE, 12*1), 'HH24MISS')\n" +
				"           </isNotEqual>  \n" +
				"           , 'OR003201' /*OCCR_STAT(발생)*/\n" +
				"           , 'OR003311' /*OP_TYPE(Gift등록)*/\n" +
				"           , #AMT#\n" +
				"           , #AMT#\n" +
				"            )\n" +
				"	</insert>\n" +
				"\n" +
				"</sqlMap>";

	}

}
