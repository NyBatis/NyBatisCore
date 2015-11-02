package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class ShortMapper implements TypeMapperIF<Short>{

	@Override
    public void setParameter( PreparedStatement statement, int index, Short param ) throws SQLException {
		statement.setShort( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Short.class).toCode() );
	}

	@Override
    public Short getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getShort( columnName );
    }

	@Override
    public Short getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getShort( columnIndex );
    }

	@Override
    public Short getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getShort( columnIndex );
    }

}
