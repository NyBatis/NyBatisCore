package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.db.sql.sqlNode.expressionLanguage.node.Node;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.util.StringUtil;

import java.util.List;

public class ElseIfSqlElement extends IfSqlElement {

	public ElseIfSqlElement( String testExpression ) throws SqlParseException {
		super( testExpression );
	}

	public ElseIfSqlElement( String testExpression, List<Node> testCondition, List<SqlElement> children ) {
		super( testExpression, testCondition, children );
	}

	private void toString( StringBuilder buffer, SqlElement node, int depth ) {

		String tab = StringUtil.lpad( "", depth * 2, ' ' );

		buffer.append( String.format( "%s%s", tab, node.toString() ) );

	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append( String.format("[ElseIF test='%s']\n", getTestExpression()) );

		for( SqlElement node : children ) {
			toString( sb, node, 1 );
		}

		return sb.toString();

	}

}
