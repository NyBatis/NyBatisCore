package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class ByteMapper implements TypeMapperIF<Byte>{

	@Override
    public void setParameter( PreparedStatement statement, int index, Byte param ) throws SQLException {
		statement.setByte( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Byte.class).toCode() );
	}

	@Override
    public Byte getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getByte( columnName );
    }

	@Override
    public Byte getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getByte( columnIndex );
    }

	@Override
    public Byte getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getByte( columnIndex );
    }

}
