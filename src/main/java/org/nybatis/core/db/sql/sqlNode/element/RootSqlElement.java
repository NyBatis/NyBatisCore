package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.exception.unchecked.SqlParseException;


public class RootSqlElement extends SqlElement {

	private String id = null;

	public RootSqlElement( String id ) throws SqlParseException {
		this.id = id;
	}

	public void setMainId( String mainId ) {
		setMainId( mainId, this );
	}

	private void setMainId( String mainId, SqlElement node ) {

		if( node instanceof RefSqlElement ) {
			((RefSqlElement) node).includeMainId( mainId );
		}

		for( SqlElement child : node.children() ) {
			setMainId( mainId, child );
		}

	}

	public String getId() {
		return id;
	}

	public boolean isNotValid() {
		return children().size() == 0;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();

		for( SqlElement child : children ) {
			sb.append( child.toString() );
		}

		return sb.toString();

	}
}
