package org.nybatis.core.db.sql.sqlNode.element.abstracts;

import java.util.ArrayList;
import java.util.List;

import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.sqlNode.element.ElseIfSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.ElseSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.IfSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.WhenSqlElement;
import org.nybatis.core.exception.unchecked.SqlParseException;

public abstract class SqlElement {

    protected List<SqlElement> children = new ArrayList<>();

	public String toString( QueryParameter param ) throws SqlParseException {

		StringBuilder sb = new StringBuilder();

		for( ElementText element : toStringList( param ) ) {
			sb.append( element.getText() );
		}

		return sb.toString();

	}

	public void append( SqlElement sqlElement ) {
		children.add( sqlElement );
	}

	public List<SqlElement> children() {
		return children;
	}

	protected List<ElementText> toStringList( QueryParameter param ) {

		List<ElementText> list = new ArrayList<>();

		Boolean previousCondition = null;

		for( SqlElement element : children ) {

			if( isElseSeries(element) ) {
				if( previousCondition != null && previousCondition != true ) {
					list.add( new ElementText( element.getClass(), element.toString( param ) ) );
				}
			} else {
				list.add( new ElementText( element.getClass(), element.toString( param ) ) );
			}

			if( isIf(element) ) {
				previousCondition = getIfSeriesResult( element, param );

			} else if( isElseIf( element ) ) {
				if( previousCondition == null || previousCondition == false ) {
					previousCondition = getIfSeriesResult( element, param );
				}

			} else if( isElse( element ) ) {
				previousCondition = null;
			}

		}

		return list;

	}

	private boolean isIf( SqlElement element ) {
		return element.getClass() == IfSqlElement.class;
	}

	private boolean isElseIf( SqlElement element ) {
		return element.getClass() == ElseIfSqlElement.class;
	}

	private boolean isElse( SqlElement element ) {
		return element.getClass() == ElseSqlElement.class;
	}

	private boolean getIfSeriesResult( SqlElement element, QueryParameter param ) {
		if( ! isIf( element ) ) return false;
		return ((IfSqlElement) element).isTrue( param );
	}

	private boolean isElseSeries( SqlElement element ) {

		Class klass = element.getClass();

		if( klass == ElseIfSqlElement.class ) return true;
		if( klass == WhenSqlElement.class   ) return true;
		return klass == ElseSqlElement.class;

	}

}