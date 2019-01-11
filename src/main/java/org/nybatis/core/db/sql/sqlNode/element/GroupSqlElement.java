package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.ElementText;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupSqlElement extends SqlElement {

	private String open;
	private String close;
	private String concater;

	public GroupSqlElement( String open, String close, String concater ) {

		this.open        = StringUtil.trim( open );
		this.close       = StringUtil.trim( close );
		this.concater = StringUtil.trim( concater );

	}

	@Override
    public String toString( QueryParameter param ) throws SqlParseException {

		List<StringBuilder> paragraph = getParagraph( param );

		StringBuilder sb = new StringBuilder();

		sb.append( open );
		sb.append( StringUtil.join( paragraph, concater ) );
		sb.append( close );

		return sb.toString();

	}

	private List<StringBuilder> getParagraph( QueryParameter param ) {

		boolean delimiterOff = StringUtil.isEmpty( concater );

		List<StringBuilder> paragraph = new ArrayList<>();

		StringBuilder buffer = new StringBuilder();

		for( ElementText element : toStringList( param ) ) {

			buffer.append( element.getText() );

			if( delimiterOff ) continue;
			if( element.getKlass() == StringSqlElement.class ) continue;
			if( StringUtil.isBlank(element.getText()) ) continue;

			paragraph.add( buffer );

			buffer = new StringBuilder();

		}

		if( ! StringUtil.isBlank(buffer) ) {
			paragraph.add( buffer );
		}
		return paragraph;
	}

	private void toString( StringBuilder buffer, SqlElement node, int depth ) {

		String tab = StringUtil.lpad( "", depth * 2, ' ' );

		buffer.append( String.format( "%s%s", tab, node.toString() ) );

	}

	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append( String.format("[GROUP open='%s' close='%s' concater='%s']\n", open, close, concater ) );

		for( SqlElement node : children ) {
			toString( sb, node, 0 );
		}

		return sb.toString();

	}

}
