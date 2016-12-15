package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.ElementText;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupSqlElement extends SqlElement {

	private String open;
	private String close;
	private String delimeter;

	public GroupSqlElement( String open, String close, String delimeter ) {

		this.open        = StringUtil.trim( open );
		this.close       = StringUtil.trim( close );
		this.delimeter   = StringUtil.trim( delimeter );

	}

	@Override
    public String toString( QueryParameter param ) throws SqlParseException {

		List<StringBuilder> paragraph = getParagraph( param );

		StringBuilder sb = new StringBuilder();

		sb.append( open );
		sb.append( StringUtil.join( paragraph, delimeter ) );
		sb.append( close );

		return sb.toString();

	}

	private List<StringBuilder> getParagraph( QueryParameter param ) {

		boolean delimiterOff = StringUtil.isEmpty( delimeter );

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

		sb.append( String.format("[GROUP open='%s' close='%s' delimeter='%s']\n", open, close, delimeter) );

		for( SqlElement node : children ) {
			toString( sb, node, 0 );
		}

		return sb.toString();

	}

}
