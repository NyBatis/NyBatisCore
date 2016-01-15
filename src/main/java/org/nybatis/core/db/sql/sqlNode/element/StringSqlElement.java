package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

public class StringSqlElement extends SqlElement {

	private String text;

	public StringSqlElement( String text ) {
		this.text = StringUtil.nvl( text );
	}

	@Override
    public String toString( QueryParameter param ) {
		return text;
    }

	@Override
    public void append( SqlElement sqlElement ) {}

	public String toString() {
		return text;
	}

}
