package org.nybatis.core.db.sql.reader;

import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.node.Node;

/**
 * Sql Reader to make SqlNode<br/>
 *
 * environmentId was must be included in SqlNode.
 *
 * @author nayasis@gmail.com
 * @since 2015-09-11
 */
public class SqlReader {

    public SqlNode read( String xmlSql ) {
        return read( null, null, xmlSql );
    }

    public SqlNode read( String environmentId, String xmlSql ) {
        return read( environmentId, null, xmlSql );
    }

    public SqlNode read( String environmentId, String sqlId, String xmlSql ) {

        xmlSql = getWellFormedXml( sqlId, xmlSql );

        NXml xmlReader = new NXmlDeformed( xmlSql );

        Node sqlNode = xmlReader.getRoot();

        return new XmlSqlParser( environmentId ).parse( sqlId, sqlNode );

    }

    private String getWellFormedXml( String sqlId, String xmlSql ) {

        // ** xml sample is like below.
        // <sql id="selectKey" fetch="50" cache="cacheId" flush="60" >
        //
        // but properties cache or flush can be controlled by [SqlNode's Properties]
        // so it dose not need to consider about in here.

        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sql id=\"%s\" >%s\n</sql>",
                sqlId, xmlSql
        );

    }

}
