package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;
import org.nybatis.core.util.StringUtil;

public class CharacterMapper implements TypeMapperIF<Character> {

	@Override
    public void setParameter( PreparedStatement statement, int index, Character param ) throws SQLException {
		statement.setString( index, StringUtil.nvl( param ) );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Character.class).toCode() );
	}

	@Override
    public Character getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return toCharacter( resultSet.getString(columnName) );
    }

	@Override
    public Character getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return toCharacter( resultSet.getString(columnIndex) );
    }

	@Override
    public Character getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		return toCharacter( statement.getString(columnIndex) );
    }

	private Character toCharacter( String value ) {
		return value == null ? null : value.charAt( 0 );
	}

}
