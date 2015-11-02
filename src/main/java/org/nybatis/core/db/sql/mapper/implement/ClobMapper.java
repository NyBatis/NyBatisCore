package org.nybatis.core.db.sql.mapper.implement;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class ClobMapper implements TypeMapperIF<String> {

	@Override
    public void setParameter( PreparedStatement statement, int index, String param ) throws SQLException {
		StringReader reader = new StringReader( param );
		statement.setCharacterStream( index, reader, param.length() );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(String.class).toCode() );
	}

	@Override
    public String getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return toString( resultSet.getClob(columnName) );
    }

	@Override
    public String getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return toString( resultSet.getClob(columnIndex) );
    }

	@Override
    public String getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		return toString( statement.getClob(columnIndex) );
    }

	private String toString( Clob clob ) throws SQLException {

		if( clob == null ) return null;

		return clob.getSubString( 1, (int) clob.length() );

	}

}
