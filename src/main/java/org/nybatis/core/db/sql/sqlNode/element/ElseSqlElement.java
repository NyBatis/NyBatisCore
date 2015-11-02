package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.util.StringUtil;

/**
 * @author Administrator
 * @since 2015-10-23
 */
public class ElseSqlElement extends SqlElement {

    private void toString( StringBuilder buffer, SqlElement node, int depth ) {

        String tab = StringUtil.lpad( "", ' ', depth * 2 );

        buffer.append( String.format( "%s%s", tab, node.toString() ) );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append( "[ELSE]\n" );

        for( SqlElement node : children ) {
            toString( sb, node, 1 );
        }

        return sb.toString();

    }

}
