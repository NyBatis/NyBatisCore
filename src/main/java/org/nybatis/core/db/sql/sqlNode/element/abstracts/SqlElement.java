package org.nybatis.core.db.sql.sqlNode.element.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nybatis.core.db.sql.sqlNode.element.ElseIfSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.ElseSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.IfSqlElement;
import org.nybatis.core.db.sql.sqlNode.element.WhenSqlElement;
import org.nybatis.core.exception.unchecked.SqlParseException;


public abstract class SqlElement {

    protected List<SqlElement> children = new ArrayList<>();

	public String toString( Map param ) throws SqlParseException {

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

	protected List<ElementText> toStringList( Map param ) {

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

			if( isIfSeries(element) ) {
				if( previousCondition == null || previousCondition == false ) {
					previousCondition = getIfSeriesResult( element, param );
				}
			}

			if( isIfCloseSeries(element) ) {
				previousCondition = null;
			}

		}

		return list;

	}

	private boolean isIfSeries( SqlElement element ) {
		return element instanceof IfSqlElement;
	}

	private boolean getIfSeriesResult( SqlElement element, Map param ) {

		if( ! isIfSeries(element) ) return false;

		return ((IfSqlElement) element).isTrue( param );

	}

	private boolean isElseSeries( SqlElement element ) {

		Class klass = element.getClass();

		if( klass == ElseIfSqlElement.class ) return true;
		if( klass == WhenSqlElement.class   ) return true;
		return klass == ElseSqlElement.class;

	}

	private boolean isIfCloseSeries( SqlElement element ) {
		return element instanceof ElseSqlElement;
	}

}