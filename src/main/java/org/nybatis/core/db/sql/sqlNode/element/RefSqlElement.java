package org.nybatis.core.db.sql.sqlNode.element;

import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.element.abstracts.SqlElement;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.util.StringUtil;

public class RefSqlElement extends SqlElement {

	private String referenceSqlId = null;

	public RefSqlElement( String referenceSqlId ) {
		if( StringUtil.isEmpty( referenceSqlId ) ) return;
		this.referenceSqlId = referenceSqlId;
	}

	/**
	 * MainId가 세팅되지 않은 참조ID는
	 * Root Sql MainId 가 세팅될 경우, 이에 맞도록 참조ID를 변경시킨다.
	 *
	 * @param mainId
	 */
	public void includeMainId( String mainId ) {
		if( referenceSqlId != null && ! referenceSqlId.contains( "." ) ) {
			referenceSqlId = String.format( "%s.%s", mainId, referenceSqlId );
		}
	}

	@Override
    public String toString( QueryParameter param ) {

		if( referenceSqlId == null ) return "";

		SqlNode sql = SqlRepository.get( referenceSqlId );

		if( sql == null ) throw new SqlConfigurationException( "refId[{}] is not exists.", referenceSqlId );

		return sql.getText( param );

	}

	public String toString() {
		return referenceSqlId == null ? "" : String.format( "<ref id=\"%s\"/>", referenceSqlId );
	}

}
