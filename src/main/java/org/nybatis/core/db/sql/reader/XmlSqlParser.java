package org.nybatis.core.db.sql.reader;

import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.element.CaseSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.ElseIfSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.ElseSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.GroupSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.IfSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.ForEachSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.RefSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.RootSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.WhenSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.db.sql.sqlNode.element.StringSqlElement;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.node.Node;

import java.util.LinkedHashMap;
import java.util.Map;

public class XmlSqlParser {

	private String               environmentId = null;
	private Map<String, SqlNode> keySqls       = null;

	public XmlSqlParser( String environmentId ) {
		this.environmentId = environmentId;
	}

	public SqlNode parse( String sqlId, Node sqlNode ) throws SqlParseException, DatabaseConfigurationException {

		keySqls = new LinkedHashMap<>();

		RootSqlElement rootSqlElement = new RootSqlElement( sqlId );

		makeNode( sqlId, rootSqlElement, sqlNode );

		if( rootSqlElement.isNotValid() ) return null;

		return new SqlNode( sqlId, rootSqlElement, environmentId, sqlNode, keySqls );

	}

	public String getEnvironmentId() {
		return environmentId;
	}

	private SqlNode makeRootSqlNode( String sqlId, Node sqlNode ) throws SqlParseException, DatabaseConfigurationException {

		RootSqlElement rootSqlElement = new RootSqlElement( sqlId );

		makeNode( sqlId, rootSqlElement, sqlNode );

		if( rootSqlElement.isNotValid() ) return null;

		return new SqlNode( sqlId, rootSqlElement, environmentId, sqlNode, null );

	}

	private void makeNode( String sqlId, SqlElement parentNode, Node sqlNode ) throws SqlParseException, DatabaseConfigurationException {

		for( Node child : sqlNode.getChildNodes() ) {

			if( child.isText() ) {

				parentNode.append( new StringSqlElement(child.getText()) );

			} else if( child.isElement() ) {

				SqlElement nodeToAppend = null;

				switch( child.getName() ) {

					case "if" :
						nodeToAppend = new IfSqlElement( child.getAttrIgnoreCase("test") );
						break;

					case "elseif" :
						nodeToAppend = new ElseIfSqlElement( child.getAttrIgnoreCase("test") );
						break;

					case "else" :
						nodeToAppend = new ElseSqlElement();
						break;

					case "case" :
						nodeToAppend = new CaseSqlElement();
						break;

					case "when" :
						nodeToAppend = new WhenSqlElement( child.getAttrIgnoreCase("test") );
						break;

					case "ref" :
						nodeToAppend = new RefSqlElement( child.getAttrIgnoreCase("id") );
						break;

					case "key" :
						registKeySqlNode( sqlId, child );
						break;

					case "foreach" :
						nodeToAppend = new ForEachSqlElement(
								child.getAttrIgnoreCase("key"),
								child.getAttrIgnoreCase("open"),
								child.getAttrIgnoreCase("close"),
								child.getAttrIgnoreCase("delimeter"),
								child.getAttrIgnoreCase("indexKey")
						);
						break;

					case "group" :
						nodeToAppend = new GroupSqlElement(
								child.getAttrIgnoreCase("open"),
								child.getAttrIgnoreCase("close"),
								child.getAttrIgnoreCase("delimeter")
						);
						break;
				}

				if( nodeToAppend != null ) {
					makeNode( sqlId, nodeToAppend, child );
					parentNode.append( nodeToAppend );
				}

			}

		}

	}

	private void registKeySqlNode( String sqlId, Node sqlNode ) throws DatabaseConfigurationException {

		String keyId = sqlNode.getAttrIgnoreCase( "id" );

		if( StringUtil.isEmpty(keyId) ) {
			NLogger.info( "keynode id is missing in sql({}).", sqlId );
			return;
		}

		SqlNode keyNode = makeRootSqlNode( keyId, sqlNode );

		if( keyNode == null ) return;

		if( keySqls.containsKey( keyId ) ) {
			throw new SqlConfigurationException( "There is duplicated sql key. (key:[{}], id:[{}])", keyId, sqlId );
		}

		keySqls.put( keyId, keyNode );

	}

}
